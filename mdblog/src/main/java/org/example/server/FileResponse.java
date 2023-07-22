package org.example.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileResponse extends Response {
    private static final int BUFFER_SIZE = 1024;
    private final File file;

    protected FileResponse(Request request, File file) {
        super(request);
        int idx = file.getName().indexOf(".");
        String suffix = file.getName().substring(idx+1);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", Mime.getMime(suffix));
        headers.put("Content-Length", String.valueOf(file.length()));
        this.setHeaders(headers);
        this.file = file;
    }

    @Override
    protected void writeBodyToOutput(OutputStream output) {
        byte[] bytes = new byte[BUFFER_SIZE];
        try (FileInputStream fis = new FileInputStream(file)) {
            int ch = fis.read(bytes, 0, BUFFER_SIZE);
            while (ch != -1) {
                output.write(bytes, 0, ch);
                ch = fis.read(bytes, 0, BUFFER_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
