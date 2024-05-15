package com.etim.atom.services;

import com.etim.atom.requests.EmptyTopicRequest;
import com.etim.atom.requests.MessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ITTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @Tag("it")
    public void testShowAllTopics() throws Exception {
        setUser();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/topic"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                [
                    {
                        "topicId": "e9494fd2-78ae-4adb-b805-d3b08f0399c9",
                        "topicName": "Topic1",
                        "createdAt": "2024-03-28T12:20:20+03:00"
                    },
                    {
                        "topicId": "360df656-5c25-4157-8047-8fb64d0c9563",
                        "topicName": "Topic2",
                        "createdAt": "2024-03-28T12:30:33+03:00"
                    },
                    {
                        "topicId": "0dd1644a-a854-46dd-bea7-8c36dd785d10",
                        "topicName": "Topic3",
                        "createdAt": "2024-03-28T12:40:03+03:00"
                    },
                    {
                        "topicId": "c3873100-8bef-48f3-958b-c07ff1cce17f",
                        "topicName": "Topic4",
                        "createdAt": "2024-03-28T12:40:03+03:00"
                    }
                ]
                """));
    }

    @Test
    @Order(2)
    @Tag("it")
    public void testShowTopicWithMessages() throws Exception {
        setUser();
        String topicId = "e9494fd2-78ae-4adb-b805-d3b08f0399c9";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/topic/{topicId}", topicId))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                {
                    "topicUuid": "e9494fd2-78ae-4adb-b805-d3b08f0399c9",
                    "topicName": "Topic1",
                    "createdAt": "2024-03-28T12:20:20+03:00",
                    "messages": [
                        {
                            "messageUuid": "2fde6f8b-973f-4c6c-8573-62505c3a30e4",
                            "text": "Hello from user1!",
                            "author": "user1",
                            "createdAt": "2024-03-28T12:21:54+03:00"
                        },
                        {
                            "messageUuid": "6c4bca4f-4bd4-4f7d-8c56-8c0c2740fd95",
                            "text": "Hi from user2!",
                            "author": "user2",
                            "createdAt": "2024-03-28T12:22:19+03:00"
                        },
                        {
                            "messageUuid": "ffe05725-d36c-4c44-982b-c20474998d5a",
                            "text": "Admin is here",
                            "author": "admin",
                            "createdAt": "2024-03-28T12:23:47+03:00"
                        }
                    ]
                }
                """));
    }

    @Test
    @Order(3)
    @Tag("it")
    public void testCreateTopicWithMessage() throws Exception {
        setUser();

        EmptyTopicRequest emptyTopicRequest = new EmptyTopicRequest("Test topic");
        MessageRequest messageRequest = new MessageRequest("Test message");
        String jsonCombinedRequest = "{\"emptyTopicRequest\":" + objectMapper.writeValueAsString(emptyTopicRequest) +
                ",\"messageRequest\":" + objectMapper.writeValueAsString(messageRequest) + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCombinedRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicName").value("Test topic"))
                .andExpect(jsonPath("$.messages[0].text").value("Test message"))
                .andExpect(jsonPath("$.topicUuid").isNotEmpty())
                .andExpect(jsonPath("$.messages[0].messageUuid").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isString());
    }

    @Test
    @Order(4)
    @Tag("it")
    public void testCreateMessageInExistingTopic() throws Exception {
        setUser();
        String topicId = "360df656-5c25-4157-8047-8fb64d0c9563";
        MessageRequest messageRequest = new MessageRequest("Test message");

        String jsonMessageRequest = objectMapper.writeValueAsString(messageRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/topic/{topicId}/message", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessageRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[3].text").value("Test message"))
                .andExpect(jsonPath("$.messages[3].messageUuid").isNotEmpty());
    }

    @Test
    @Order(5)
    @Tag("it")
    public void testDeleteTopicByUser_NotSuccess() throws Exception {
        setUser();
        String topicId = "0dd1644a-a854-46dd-bea7-8c36dd785d10";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/topic/{topicId}", topicId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/topic/{topicId}", topicId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @Tag("it")
    public void testDeleteTopicByAdmin_Success() throws Exception {
        setAdmin();
        String topicId = "c3873100-8bef-48f3-958b-c07ff1cce17f";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/topic/{topicId}", topicId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/topic/{topicId}", topicId))
                .andExpect(status().isNotFound());
    }

    private void setAdmin() {
        UserDetails userDetails = new User("admin", "password",
                Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }

    private void setUser() {
        UserDetails userDetails = new User("user", "password",
                Collections.singleton(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
