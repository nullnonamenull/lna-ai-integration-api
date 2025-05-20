package com.noname.lnaintegrationapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.noname.lnaaiintegrationdto.ChatRequest;
import com.noname.lnaaiintegrationdto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    private final WebClient webClient;


    public OpenAiController(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        JsonNode root = webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String content = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

        return ResponseEntity.ok(ChatResponse.builder()
                .message(content)
                .build());
    }

}