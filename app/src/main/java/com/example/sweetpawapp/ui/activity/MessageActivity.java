package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.service.SocketManager;
import com.example.sweetpawapp.ui.adapter.MessageAdapter;
import com.example.sweetpawapp.data.model.chat.Message;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONObject;

import io.socket.client.Socket;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText input;
    private ImageView btnSend, imgBack;

    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        recyclerView = findViewById(R.id.recycler_view_messages);
        input = findViewById(R.id.input_message_chat);
        btnSend = findViewById(R.id.btn_send);
        imgBack = findViewById(R.id.imgBack);

        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SocketManager.initSocket("https://sweetpaw-be.azurewebsites.net/");

        // SocketManager.initSocket("https://sweetpaw-bev1.azurewebsites.net/");

        Socket socket = SocketManager.getSocket();

        socket.connect();

        /*socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SOCKET", "Connected: " + socket.id());
            socket.emit("joinRoom", userId);
            socket.emit("openChat", new JSONObject()
                    .put("userId", userId)
                    .put("chatId", chatId));
        });*/

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage() {
        String text = input.getText().toString().trim();

        if (text.isEmpty()) return;

        // Add message from user
        messages.add(new Message(text, true));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        input.setText("");
    }
}
