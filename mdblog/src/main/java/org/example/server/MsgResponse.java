package org.example.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MsgResponse extends Response {
    private final String message;

    protected MsgResponse(Request request, String message) {
        super(request);
        this.message = message;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain;charset=utf-8");
        headers.put("Content-Length", String.valueOf(message.getBytes().length));
        this.setHeaders(headers);
    }

    @Override
    public void writeBodyToOutput(OutputStream output) throws IOException {
        output.write(message.getBytes());
    }
}
