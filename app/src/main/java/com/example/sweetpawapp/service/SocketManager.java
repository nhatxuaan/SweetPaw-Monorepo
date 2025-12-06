package com.example.sweetpawapp.service;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static Socket socket;
    private static boolean initialized = false;

    public static void initSocket(String baseUrl) {
        if (initialized) return;

        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;

            socket = IO.socket(baseUrl, options);
            initialized = true;

        } catch (URISyntaxException e) {
            Log.e("SocketManager", "Socket init error: " + e.getMessage());
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
}
