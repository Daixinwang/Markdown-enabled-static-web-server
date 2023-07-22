package org.example.server;

import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonResponse extends Response {

    private final Map<String, String> data;
    private final String message;

    protected JsonResponse(Request request, Map<String, String> data) {
        super(request);
        this.data = data;
        this.message = JSONArray.toJSONString(data);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(message.getBytes().length));
        this.setHeaders(headers);
    }

    @Override
    protected void writeBodyToOutput(OutputStream output) throws IOException {
        output.write(message.getBytes());
    }

    public Map<String, String> getData() {
        return this.data;
    }
}
