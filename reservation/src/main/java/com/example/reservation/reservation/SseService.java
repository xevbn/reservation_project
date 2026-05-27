package com.example.reservation.reservation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


//서버에서 클라이언트로 지속적으로 데이터를 보내는 sse를 위한 서비스
@Service
public class SseService {
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private BiConsumer<SseEmitter, Object> sendHook;

    public void setSendHook(BiConsumer<SseEmitter, Object> sendHook) {
        this.sendHook = sendHook;
    }

    //sse 구독 시 
    public SseEmitter subscribe(Long userId) {
        //1시간으로 생성
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        String key = "user:" + userId.toString();

        List<SseEmitter> list = emitters.computeIfAbsent(key, id -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> list.remove(emitter));
        emitter.onTimeout(() ->  list.remove(emitter));
        emitter.onError((e) -> list.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            list.remove(emitter);
        }

        return emitter;
    }

    //sse 구독 취소 시
    public void removeEmitter(Long userId, SseEmitter emitter) {
        String key = "user:" + userId.toString();
        List<SseEmitter> list = emitters.get(key);
        if (list != null) list.remove(emitter);
    }

    //변경 사항 업데이트
    public void sendUpdate(Long userId, Object data) {
        String key ="user:" + userId.toString();
        List<SseEmitter> list = emitters.get(key);
        if (list == null) return;

        for (SseEmitter emitter : list) {
            try {
                if(sendHook != null) {
                    sendHook.accept(emitter, data);
                }
                
                emitter.send(
                    SseEmitter.event()
                        .name("message")
                        .data(data)
                        .build()
                );
            } catch (Exception e) {
                //오류 로직 추가
                System.out.println("sse Error: " + e.getMessage());
                emitter.complete();
            }
        }
    }

    //redis를 이용하여 여러 사용자에게 메세지를 브로드캐스팅
    public void broadcast(String message) {
        emitters.forEach((key, list) -> {
            for(SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message));
                } catch (Exception e) {
                    list.remove(emitter);
                }
            }
        });
    }

    public Map<String, List<SseEmitter>> getEmitters() {
        return this.emitters;
    }
}
