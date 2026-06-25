package com.ami.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ami.service.GroqService;

@Service
public class GroqServiceImpl
        implements GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate =
            new RestTemplate();

    @Override
    public String generateResponse(
            String prompt) {

        try {

            String url =
                    "https://api.groq.com/openai/v1/chat/completions";

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON);

            headers.setBearerAuth(
                    apiKey);

            Map<String, Object> body =
                    Map.of(
                            "model",
                            model,
                            "messages",
                            List.of(
                                    Map.of(
                                            "role",
                                            "user",
                                            "content",
                                            prompt)));

            HttpEntity<Map<String, Object>>
                    entity =
                    new HttpEntity<>(
                            body,
                            headers);

            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            Map.class);
            
            if (response.getBody() == null) {

                return "No response received from Groq.";
            }


            List choices =
                    (List)
                            response.getBody()
                                    .get("choices");
            
            if (choices == null ||
                    choices.isEmpty()) {

                return "Groq returned empty choices.";
            }

            Map choice =
                    (Map)
                            choices.get(0);

            Map message =
                    (Map)
                            choice.get("message");

            return message
                    .get("content")
                    .toString();

        } catch (Exception ex) {

            ex.printStackTrace();

            return "Groq Error : "
                    + ex.getMessage();
        }
        }
    
}