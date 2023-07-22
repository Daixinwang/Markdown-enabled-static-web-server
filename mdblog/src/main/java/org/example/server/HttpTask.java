package org.example.server;

import org.example.md.FileTranslator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpTask implements Runnable {

    private final Socket socket;
    private final FileTranslator translator;

    public HttpTask(Socket socket, FileTranslator translator) {
        this.socket = socket;
        this.translator = translator;
    }

    @Override
    public void run() {
        if (socket == null) return;
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter out = new PrintWriter(output);
            Request request = Request.BuildFromInputStream(socket.getInputStream());
            System.out.println(request);
            try {
                Response response = null;
                if (request.getMethod().toLowerCase(Locale.ROOT).equals("get")) {
                    response = Response.BuildFileResp(request);
                }
                if (request.getMethod().toLowerCase(Locale.ROOT).equals("post")) {
                    if (request.getUri().equals("/upload")) {
                        response = handleUpload(request);
                    }
                }
                assert response != null;
                System.out.println(response);
                response.writeToOutput(output);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = "HTTP/1.1 500 Internal Error\r\n" +
                        "Content-Type: text/html;charset=UTF-8\r\n" +
                        "\r\n" + e.getMessage();
                out.print(errorMessage);
            }
            out.flush();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Response handleUpload(Request request) {
        Map<String, String> data = new HashMap<>();
        try {
            Map<String, File> files = request.parseFiles();
            if (!files.isEmpty()) {
                System.out.println("发布成功！开始重新生成网页...");
                translator.translate();
            }
            data.put("ok", "成功发布!");
        } catch (IOException e) {
            e.printStackTrace();
            data.put("error", e.toString());
        }
        return Response.BuildJsonResp(request, data);
    }
}
