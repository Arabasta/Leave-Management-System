package com.team4.leaveprocessingsystem.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

// reference: https://mailtrap.io/blog/spring-send-email/#:~:text=Using%20the%20%40Autowired%20annotation%2C%20inject,to%20send%20the%20email%20message.

@Service
public class EmailApiService {
    private final OkHttpClient client;

    public EmailApiService() {
        this.client = new OkHttpClient().newBuilder().build();
    }

    public void sendEmail(String recipient, String subject, String text) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String bodyContent = String.format(
                "{\"from\":{\"email\":\"notifications-noreply@example.com\",\"name\":\"NUS-ISS Leave Application Notification\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"%s\",\"text\":\"%s\"}",
                recipient, subject, text
        );

        RequestBody body = RequestBody.create(mediaType, bodyContent);
        Request request = new Request.Builder()
                .url("https://sandbox.api.mailtrap.io/api/send/2953403") // Verify this URL with Mailtrap's documentation
                .method("POST", body)
                .addHeader("Authorization", "Bearer fdab49635ee9e6e437d017439547ea1c") // Ensure apiKey is correctly injected
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Email notification unsuccessful " + response);
            }
        }
    }
}
