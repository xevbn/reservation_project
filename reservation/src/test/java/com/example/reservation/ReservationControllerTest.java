package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.reservation.reservation.ReservationDto;
import com.example.reservation.reservation.ReservationResponse;
import com.example.reservation.reservation.ReservationService;
import com.example.reservation.resource.Resource;
import com.example.reservation.resource.ResourceRepository;
import com.example.reservation.user.User;
import com.example.reservation.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class ReservationControllerTest {
    @Autowired
    ReservationService reservationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResourceRepository resourceRepository;
    private String resourceName;
    @Autowired
    MockMvc mvc;
    public static ObjectMapper objectMapper = new ObjectMapper();

    Long id;

    public void makeAuth(int num) {
        User testUser = new User();
        testUser.setUsername("username" + num);
        testUser.setPassword("password");
        testUser.setEmail("email" + num);
        testUser.setUserRole("USER");

        userRepository.save(testUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUser.getUsername(), testUser.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    public void setUp() {
        makeAuth(0);
        Resource resource = new Resource("room1");
        resourceRepository.save(resource);
        resourceName = resource.getName();

        LocalDate date = LocalDate.now();
        ReservationDto dto = new ReservationDto(
            date,
            LocalTime.of(15, 0),
            LocalTime.of(16, 0),
            resourceName
            
        );

        id = reservationService.makeReservation(dto).getId();

        ReservationDto dto2 = new ReservationDto(
            date,
            LocalTime.of(17, 0),
            LocalTime.of(18, 0),
            resourceName
        );
        reservationService.makeReservation(dto2);
    }

    @Test
    public void makeReservationAtAlreadyAssigned() throws Exception{
        LocalDate date = LocalDate.now();
        ReservationDto dto = new ReservationDto(
            date,
            LocalTime.of(15, 0),
            LocalTime.of(17, 0),
            resourceName
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        mvc.perform(post("/" + date.getMonthValue() + "." + date.getDayOfMonth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void makeReservation() throws Exception {
        LocalDate date = LocalDate.now();
        ReservationDto dto = new ReservationDto(
            date,
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            resourceName
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        mvc.perform(post("/" + date.getMonthValue() + "." + date.getDayOfMonth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void editReservation() throws Exception {
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime start = LocalTime.of(15, 0);
        ReservationDto dto = new ReservationDto(
            date,
            start,
            LocalTime.of(17, 0),
            resourceName
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        MvcResult rs = mvc.perform(post("/" + date.getMonthValue() + "." + date.getDayOfMonth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String bodyString = rs.getResponse().getContentAsString();
        Map<String, String> response = objectMapper.readValue(bodyString, new TypeReference<Map<String, String>>() {});
        Long thisId = Long.valueOf(response.get("id"));

        ReservationDto change = new ReservationDto(
            date.plusDays(2),
            LocalTime.of(12, 0),
            LocalTime.of(13, 0),
            resourceName
        );

        String editRequest = objectMapper.writeValueAsString(change);

        mvc.perform(put("/" + thisId + "/detail")
            .contentType(MediaType.APPLICATION_JSON)
            .content(editRequest))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteNonExistingReservation() throws Exception {
        mvc.perform(delete("/20/detail"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void cancelReservation() throws Exception {
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime start = LocalTime.of(15, 0);
        ReservationDto dto = new ReservationDto(
            date,
            start,
            LocalTime.of(17, 0),
            resourceName
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        MvcResult rs = mvc.perform(post("/" + date.getMonthValue() + "." + date.getDayOfMonth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String bodyString = rs.getResponse().getContentAsString();
        Map<String, String> responseBody = objectMapper.readValue(bodyString, new TypeReference<Map<String, String>>() {});
        Long savedId = Long.valueOf(responseBody.get("id"));

        mvc.perform(delete("/" + savedId + "/detail"))
            .andExpect(status().isNoContent());

        MvcResult result = mvc.perform(get("/" + date.getMonthValue() + "." + date.getDayOfMonth()))
            .andExpect(status().isOk())
            .andReturn();

        List<ReservationResponse> list = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            new TypeReference<List<ReservationResponse>>() {}
        );

        assertThat(list.size() == 2);
    }

    @Test
    public void getReservationDate() throws Exception{
        makeAuth(1);

        LocalDate date = LocalDate.now().plusDays(1);
        String dateString = date.getMonthValue() + "." + date.getDayOfMonth(); 

        ReservationDto dto = new ReservationDto(
            date,
            LocalTime.of(15, 0),
            LocalTime.of(16, 0),
            resourceName
        );
        String requestBody = objectMapper.writeValueAsString(dto);

        mvc.perform(post("/" + dateString)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        MvcResult result = mvc.perform(get("/detail"))
            .andExpect(status().isOk())
            .andReturn();

        List<ReservationResponse> list = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<List<ReservationResponse>>() {});

        assertThat(list.size() == 1);
    }

    @Test
    public void tryToChangeReservationWithAnotherUser() throws Exception{
        makeAuth(3);

        LocalDate date = LocalDate.now();
        String dateString = date.getMonthValue() + "." + date.getDayOfMonth();
        LocalTime start = LocalTime.of(15, 0);

        ReservationDto change = new ReservationDto(
            date,
            start,
            LocalTime.of(16, 0),
            resourceName
        );
        String request = objectMapper.writeValueAsString(change);

        mvc.perform(put("/" + id + "/detail")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getReservationDetail() throws Exception {
        System.out.println(id);
        if(id == null) throw new NullPointerException("id is null");

        makeAuth(10);

        MvcResult rs = mvc.perform(get("/" + id.toString() + "/detail"))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String body = rs.getResponse().getContentAsString();
    }

    @Test
    public void subscribeAvailability_returnsEventStream() throws Exception {
        LocalDate date = LocalDate.now();
        String body = objectMapper.writeValueAsString(Map.of("date", date.toString()));

        mvc.perform(get("/" + id.toString() + "/sse")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
            .andDo(print());
    }

    @Test
    public void getReservationList() throws Exception {
        LocalDate date = LocalDate.now();
        
        MvcResult rs = mvc.perform(get("/" + date.toString())
            .param("resourceId", id.toString()))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        Map<String, Boolean> occupied = objectMapper.readValue(rs.getResponse().getContentAsString(), 
            new TypeReference<Map<String, Boolean>>() {});

        assertThat(occupied.get("15:00-16:00") == true);
    }
}
