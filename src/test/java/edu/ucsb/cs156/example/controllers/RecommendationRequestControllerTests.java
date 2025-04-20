package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {
        @MockBean
        RecommendationRequestRepository recommendationRequestRepository;
        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/recommendation-requests/all: Test that logged-out users cannot access and logged-in users can access
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendation-requests/all"))
                    .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendation-requests/all"))
                    .andExpect(status().is(200)); // logged
        }

        // POST tests for /api/recommendation-requests/post: Test that logged-out users cannot post and logged-in users can post
        @Test
        public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendation-requests/post"))
                    .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendation-requests/post"))
                    .andExpect(status().is(403)); // only admins can post
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_recommendation_request() throws Exception {
            // arrange
            LocalDateTime dateRequested = LocalDateTime.parse("2025-06-01T10:00:00");
            LocalDateTime dateNeeded = LocalDateTime.parse("2025-06-10T10:00:00");

            RecommendationRequest rr = RecommendationRequest.builder()
                .requesterEmail("student@example.edu")
                .professorEmail("prof@example.edu")
                .explanation("Need one for grad school")
                .dateRequested(dateRequested)
                .dateNeeded(dateNeeded)
                .done(false)
                .build();

            when(recommendationRequestRepository.save(any())).thenReturn(rr);

            // act
            MvcResult response = mockMvc.perform(
                post("/api/recommendation-requests/post")
                    .param("requesterEmail", "student@example.edu")
                    .param("professorEmail", "prof@example.edu")
                    .param("explanation", "Need one for grad school")
                    .param("dateRequested", "2025-06-01T10:00:00")
                    .param("dateNeeded", "2025-06-10T10:00:00")
                    .param("done", "false")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).save(any());
            String expectedJson = mapper.writeValueAsString(rr);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
        }

}