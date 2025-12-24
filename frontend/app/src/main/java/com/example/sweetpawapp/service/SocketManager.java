package com.example.sweetpawapp.service;

import android.util.Log;

import com.example.sweetpawapp.data.model.chat.Message;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    private static final String TAG = "CHAT";
    private static Socket socket;
    private static boolean initialized = false;

    // ===== Listener cho Activity =====
    public interface SocketListener {
        void onAdminMessage(JSONObject msg);
    }

    private static SocketListener listener;

    public static void setListener(SocketListener l) {
        listener = l;
    }

    public static void initSocket(String baseUrl) {
        if (initialized) return;

        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;

            socket = IO.socket(baseUrl, options);
            initialized = true;

            setupListeners();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket init error: " + e.getMessage());
        }
    }

    public static Socket getSocket() {
        if (!initialized) {
            throw new IllegalStateException("Socket chưa được init! Hãy gọi initSocket() trước.");
        }
        return socket;
    }

    public static void connect() {
        if (initialized && !socket.connected()) {
            socket.connect();
        }
    }

    public static void disconnect() {
        if (initialized && socket.connected()) {
            socket.disconnect();
        }
    }

    public static boolean isConnected() {
        return initialized && socket.connected();
    }

    // ===== Bổ sung các sự kiện =====
    private static void setupListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "Socket connected");
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d(TAG, "Socket disconnected");
        });

        // Nhận tin nhắn từ Admin
        socket.on("adminMessage", args -> {
            try {
                JSONObject msg = (JSONObject) args[0];
                Log.d(TAG, "Admin Message Received: " + msg.toString());

                if (listener != null) {
                    listener.onAdminMessage(msg);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error parsing admin message: " + e.getMessage());
            }
        });
    }

    // ===== Tham gia phòng =====
    public static void joinRoom(String userId) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            data.put("role", "User");
            socket.emit("joinRoom", data);
        } catch (Exception e) {
            Log.e(TAG, "joinRoom error: " + e.getMessage());
        }
    }

    // ===== Mở chat =====
    public static void openChat(String userId, String chatId) {
        try {
            joinRoom(userId);
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            data.put("chatId", chatId);
            socket.emit("openChat", data);

        } catch (Exception e) {
            Log.e(TAG, "openChat error: " + e.getMessage());
        }
    }

    // ===== Gửi tin nhắn =====
    public static void sendMessage(String userId, String content) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("senderModel", "User");
            msg.put("userId", userId);
            msg.put("content", content);
            socket.emit("sendMessage", msg);
        } catch (Exception e) {
            Log.e(TAG, "sendMessage error: " + e.getMessage());
        }
    }

    // ===== Đóng chat =====
    public static void closeChat(String userId) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            socket.emit("closeChat", data);
        } catch (Exception e) {
            Log.e(TAG, "closeChat error: " + e.getMessage());
        }
    }
}
