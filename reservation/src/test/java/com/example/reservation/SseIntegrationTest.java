package com.example.reservation;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.reservation.application.reservation.SseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class SseIntegrationTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    SseService service;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSseSubscriptionAndEventSending() throws Exception {
        LocalDate date = LocalDate.now();
        Long id = 1L;

        MvcResult rs = mvc.perform(get("/" + id.toString() + "/sse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("date", date.toString()))))
                .andDo(print())
                .andExpect(request().asyncStarted())
                .andReturn();

        MvcResult asyncResult = mvc.perform(asyncDispatch(rs))
            .andExpect(status().isOk())
            .andReturn();

        String key = id.toString() + ":" + date.toString();
        assertEquals(1, service.getEmitters().get(key).size());

        AtomicReference<Object> received = new AtomicReference<>();
        service.sendUpdate(date, id, "DATA");

        Thread.sleep(100);

        assertEquals("DATA", received.get());
    }
}
