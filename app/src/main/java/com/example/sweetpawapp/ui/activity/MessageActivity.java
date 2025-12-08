package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.chat.ChatMessage;
import com.example.sweetpawapp.data.model.chat.MessageRequest;
import com.example.sweetpawapp.service.SocketManager;
import com.example.sweetpawapp.ui.adapter.MessageAdapter;
import com.example.sweetpawapp.data.model.chat.Chat;
import com.example.sweetpawapp.data.model.chat.Message;
import com.example.sweetpawapp.ui.viewmodel.ChatViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// import io.socket.client.Socket;

public class MessageActivity extends AppCompatActivity implements SocketManager.SocketListener {

    private static final String TAG = "CHAT";

    private RecyclerView recyclerView;
    private EditText input;
    private ImageView btnSend, imgBack;

    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private ChatViewModel viewModel;
    private PreferencesManager pref;
    // private Socket socket;
    private Chat currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        recyclerView = findViewById(R.id.recycler_view_messages);
        input = findViewById(R.id.input_message_chat);
        btnSend = findViewById(R.id.btn_send);
        imgBack = findViewById(R.id.imgBack);

        pref = PreferencesManager.getInstance(this);

        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Init socket
        SocketManager.setListener(this);
        SocketManager.initSocket("https://sweetpaw-be.azurewebsites.net/"); // SocketManager.initSocket("https://sweetpaw-bev1.azurewebsites.net/");
        SocketManager.connect();

        // Lắng nghe sự kiện socket
        // registerSocketEvents();

        // kết nối socket
        // socket.connect();

        // Lấy hoặc tạo chat từ server
        loadChat();

        btnSend.setOnClickListener(v -> sendMessage());
        imgBack.setOnClickListener(v -> finish());
    }


    // ===== Callback realtime từ Admin =====
    @Override
    public void onAdminMessage(JSONObject json) {
        runOnUiThread(() -> {
            try {
                Log.d(TAG, "Nhận tin nhắn realtime từ admin: " + json);

                JSONObject msgJson = json.getJSONObject("message");

                Message msg = new Message();
                msg.setMessageId(msgJson.getString("_id"));
                msg.setContent(msgJson.getString("content"));
                msg.setSenderModel(msgJson.getString("senderModel"));
                msg.setRead(true);

                messages.add(msg);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);

            } catch (JSONException e) {
                Log.e(TAG, "Lỗi parse JSON tin nhắn: " + e.getMessage());
            }
        });
    }

    private void loadChat() {
        String token = "Bearer " + pref.getToken();
        viewModel.getOrCreateChat(token).observe(this, chat -> {
            if (chat == null) {
                Log.e(TAG, "Không lấy được chat");
                return;
            }

            currentChat = chat;

            messages.clear();
            if (chat.getMessages() != null) messages.addAll(chat.getMessages());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);

            // Nếu socket đã connect, join chat room
            if (SocketManager.isConnected()) joinChatRoom(chat);
        });
    }

    private void joinChatRoom(Chat chat) {
        SocketManager.openChat(chat.getUser().getUserId(), chat.getChatId());
        Log.d(TAG, "Đã join room chat: " + chat.getChatId());
    }

    private void sendMessage() {
        String text = input.getText().toString().trim();
        if (text.isEmpty() || currentChat == null) return;

        String token = "Bearer " + pref.getToken();
        MessageRequest request = new MessageRequest(text, "");

        viewModel.sendMessage(token, request).observe(this, response -> {
            if (response == null) return;

            ChatMessage serverMsg = response.getData().getMessage();

            Message uiMsg = new Message();
            uiMsg.setMessageId(serverMsg.getMessageId());
            uiMsg.setContent(serverMsg.getContent());
            uiMsg.setMedia(serverMsg.getMedia());
            uiMsg.setTimestamp(serverMsg.getTimestamp());
            uiMsg.setRead(serverMsg.isRead());
            uiMsg.setSenderModel("User");

            messages.add(uiMsg);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);

            input.setText("");

            // === Gửi socket realtime
            SocketManager.sendMessage(
                    currentChat.getUser().getUserId(),
                    serverMsg.getContent()
            );
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentChat != null) {
            SocketManager.closeChat(currentChat.getUser().getUserId());
        }

        SocketManager.disconnect();
    }

}
