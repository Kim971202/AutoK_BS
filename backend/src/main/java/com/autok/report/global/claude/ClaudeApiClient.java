package com.autok.report.global.claude;

import com.autok.report.global.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaudeApiClient {

    private final RestTemplate restTemplate;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model}")
    private String model;

    @Value("${claude.api.url}")
    private String apiUrl;

    /**
     * Send a prompt to Claude API and return the text response.
     */
    public String generate(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 4096,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<ClaudeResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    ClaudeResponse.class
            );

            ClaudeResponse body = response.getBody();
            if (body == null || body.content == null || body.content.isEmpty()) {
                throw new BusinessException(
                        "Claude API로부터 유효한 응답을 받지 못했습니다.",
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "CLAUDE_API_ERROR"
                );
            }

            return body.content.get(0).text;

        } catch (HttpClientErrorException e) {
            log.error("Claude API client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(
                    "Claude API 요청 오류가 발생했습니다. (인증 키 또는 요청 형식을 확인해 주세요)",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CLAUDE_API_CLIENT_ERROR"
            );
        } catch (HttpServerErrorException e) {
            log.error("Claude API server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(
                    "Claude API 서버에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CLAUDE_API_SERVER_ERROR"
            );
        } catch (ResourceAccessException e) {
            log.error("Claude API connection error: {}", e.getMessage());
            throw new BusinessException(
                    "Claude API 서버에 연결할 수 없습니다. 네트워크 연결을 확인해 주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CLAUDE_API_TIMEOUT"
            );
        }
    }

    /**
     * Claude API response structure.
     */
    private static class ClaudeResponse {
        public String id;
        public String type;
        public String role;
        public List<ContentBlock> content;
        public String model;

        @JsonProperty("stop_reason")
        public String stopReason;
    }

    private static class ContentBlock {
        public String type;
        public String text;
    }
}
