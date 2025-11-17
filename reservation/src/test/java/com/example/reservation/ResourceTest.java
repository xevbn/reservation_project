package com.example.reservation;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.reservation.resource.Resource;
import com.example.reservation.resource.ResourceRepository;
import com.example.reservation.resource.ResourceService;
import com.example.reservation.user.UserDto;
import com.example.reservation.user.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin", roles="ADMIN")
public class ResourceTest {
    @Autowired
    ResourceService resourceService;
    @Autowired
    MockMvc mvc;
    @Autowired
    UserService userService;
    @Autowired
    ResourceRepository resourceRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        UserDto userDto = new UserDto("admin", "passwd", "email");
        userService.registrationForAdmin(userDto);

        Resource resource = new Resource();
        resource.setName("room1");
        resource.setDocname("name1");

        resourceService.addResource(resource);
    }

    @Test
    public void addResource() throws Exception{
        Resource newResource = new Resource();
        newResource.setName("room2");
        newResource.setDocname("name2");

        assertNotNull(mvc);

        mvc.perform(post("/resource/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newResource)))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllResources() throws Exception {
        MvcResult rs = mvc.perform(get("/resource/list"))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, List<Resource>> resBody = objectMapper.readValue(rs.getResponse().getContentAsString(),
            new TypeReference<Map<String, List<Resource>>>() {});

        assertEquals(resBody.get("resourceList").get(0).getName(), "room1");
    }

    @Test
    public void deleteResource() throws Exception {
        Resource newResource = new Resource();
        newResource.setName("delete");
        newResource.setDocname("deleted");
        Long id = resourceService.addResource(newResource).getId();

        mvc.perform(delete("/resource/" + id + "/delete"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void editResource() throws Exception {
        Resource newResource = new Resource();
        newResource.setName("asdf");
        newResource.setDocname("asdf");
        Long id = resourceService.addResource(newResource).getId();

        Resource edit = new Resource();
        edit.setName("new");
        edit.setDocname("docname");

        MvcResult rs = mvc.perform(put("/resource/" + id.toString() + "/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(edit)))
            .andExpect(status().isNoContent())
            .andReturn();

        Map<String, String> resBody = objectMapper.readValue(rs.getResponse().getContentAsString(),
            new TypeReference<Map<String, String>>() {});

        assertEquals(resBody.get("name"), "new");
    }

    @Test
    @WithMockUser(username="test")
    public void tryToAccessWithoutAdmin() throws Exception {
        mvc.perform(post("/resource/add"))
            .andExpect(status().isForbidden());
    }
}
