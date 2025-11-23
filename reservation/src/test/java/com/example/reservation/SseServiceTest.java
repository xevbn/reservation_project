package com.example.reservation;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.reservation.reservation.SseService;

@SpringBootTest
public class SseServiceTest {
    @Autowired
    SseService sseService;

    @Test
    public void testSendUpdateToMultiEmitters() throws Exception {
        LocalDate date = LocalDate.now();
        Long id = 1L;

        SseEmitter emitter1 = sseService.subscribe(date, id);
        SseEmitter emitter2 = sseService.subscribe(date, id);

        AtomicReference<Object> receive1 = new AtomicReference<>();
        AtomicReference<Object> receive2 = new AtomicReference<>();

        sseService.setSendHook((emitter, data) -> {
            if(emitter == emitter1) receive1.set(data);
            if(emitter == emitter2) receive2.set(data);
        });

        sseService.sendUpdate(date, id, "DATA");

        Thread.sleep(100);

        assertEquals("DATA", receive1.get());
        assertEquals("DATA", receive2.get());
    }
}
