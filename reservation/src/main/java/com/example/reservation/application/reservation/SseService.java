package com.example.reservation.application.reservation;

import java.time.LocalDate;
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
    public SseEmitter subscribe(LocalDate date, Long resourceId) {
        //1시간으로 생성
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        String key = resourceId + ":" + date;

        emitters.computeIfAbsent(key, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() ->  emitters.remove(key));

        return emitter;
    }

    //sse 구독 취소 시
    public void removeEmitter(LocalDate date, Long resourceId, SseEmitter emitter) {
        String key = resourceId + ":" + date;
        List<SseEmitter> list = emitters.get(key);
        if (list != null) list.remove(emitter);
    }

    //변경 사항 업데이트
    public void sendUpdate(LocalDate date, Long resourceId, Object data) {
        String key = resourceId + ":" + date;
        List<SseEmitter> list = emitters.get(key);
        if (list == null) return;

        for (SseEmitter emitter : list) {
            try {
                if(sendHook != null) {
                    sendHook.accept(emitter, data);
                }
                
                emitter.send(
                    SseEmitter.event()
                        .name("timeslots")
                        .data(data)
                        .build()
                );
            } catch (Exception e) {
                //오류 로직 추가
                emitter.complete();
            }
        }
    }

    public Map<String, List<SseEmitter>> getEmitters() {
        return this.emitters;
    }
}
