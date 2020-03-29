package com.tcg.libgdxmultiplayerdemo.net;

import io.socket.client.Socket;
import org.json.JSONException;

public final class SocketUtil {

    public static void emit(Socket socket, String event, JSONAble data) throws JSONException {
        socket.emit(event, data.toJSON());
    }

}
