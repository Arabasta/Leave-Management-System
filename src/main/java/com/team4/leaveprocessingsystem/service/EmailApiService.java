package com.team4.leaveprocessingsystem.service;

import okhttp3.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

// reference: https://mailtrap.io/blog/spring-send-email/#:~:text=Using%20the%20%40Autowired%20annotation%2C%20inject,to%20send%20the%20email%20message.
// we got no money for domains. if you want to verify the email is being sent and received,
// please go to https://mailtrap.io/ and login username: gdipsa58team4@gmail.com, pw: team4mailtrap and navigate to email testing

@Service
public class EmailApiService {
    private final OkHttpClient client;

    public EmailApiService() {
        this.client = new OkHttpClient().newBuilder().build();
    }

    @Async
    public void sendEmail(String recipient, String subject, String text) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String bodyContent = String.format(
                "{\"from\":{\"email\":\"notifications-noreply@example.com\",\"name\":\"NUS-ISS Leave Application Notification\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"%s\",\"html\":\"%s\"}",
                recipient, subject, text
        );
        RequestBody body = RequestBody.create(bodyContent, mediaType);

        Request request = new Request.Builder()
                .url("https://sandbox.api.mailtrap.io/api/send/2953403")
                .method("POST", body)
                .addHeader("Authorization", "Bearer fdab49635ee9e6e437d017439547ea1c")
                .addHeader("Content-Type", "application/json")
                .build();



        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Email notification unsuccessful " + response);
            }
        }
    }
}

