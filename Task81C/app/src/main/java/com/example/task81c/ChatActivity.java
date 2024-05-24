package com.example.task81c;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ApiService apiService;
    private List<ChatHistoryItem> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String username = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        chatHistory = new ArrayList<>();

        // Initialize Retrofit with logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.readTimeout(10, TimeUnit.MINUTES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        apiService = retrofit.create(ApiService.class);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                messageList.add(new Message(messageText, "USER"));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageEditText.setText("");

                // Update chat history
                chatHistory.add(new ChatHistoryItem("User", messageText));
                sendMessageToApi(messageText);
            }
        });
    }

    private void sendMessageToApi(String messageText) {
        ChatRequest request = new ChatRequest(messageText, chatHistory);

        Call<ChatResponse> call = apiService.sendMessage(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    String chatMessage = chatResponse.getMessage();

                    // Update chat history
                    chatHistory.add(new ChatHistoryItem("Llama", chatMessage));

                    runOnUiThread(() -> {
                        messageList.add(new Message(chatMessage, "BOT"));
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
