package com.example.reservation.auth;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class AuthInfo {
    private long id;
    private String role;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(Map.of(
                    "id", id,
                    "role", role
            ));
        } catch (JsonProcessingException ex) {
            System.getLogger(AuthInfo.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return str;
    }
}
