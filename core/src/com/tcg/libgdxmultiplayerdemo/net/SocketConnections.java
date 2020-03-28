package com.tcg.libgdxmultiplayerdemo.net;

import io.socket.client.Socket;

import java.util.ArrayList;
import java.util.List;

public class SocketConnections {

    private static List<Socket> sockets;

    public static void addConnection(Socket socket) {
        if (sockets == null) {
            synchronized (SocketConnections.class) {
                if (sockets == null) {
                    sockets = new ArrayList<>();
                }
            }
        }
        sockets.add(socket);
    }

    public static void dispose() {
        if (sockets == null) return;
        for (Socket socket : sockets) {
            socket.disconnect();
        }
        sockets.clear();
    }

}
