package org.example.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Response {
    private static String WEB_ROOT = "webroot";

    private String version;
    private int code;
    private String status;
    private Map<String, String> headers;

    public static void setWebRoot(String webRoot) {
        WEB_ROOT = webRoot;
    }

    protected Response(Request request) {
        this.version = request.getVersion();
        this.code = 200;
        this.status = "OK";
    }

    public static Response BuildMsgResp(Request request, String message) {
        return new MsgResponse(request, message);
    }

    public static Response BuildJsonResp(Request request, Map<String, String> data) {
        return new JsonResponse(request, data);
    }

    public static Response BuildFileResp(Request request) {
        String resource = request.getUri();
        if (resource.equals("/")) {
            resource = "/index.html";
        }
        File file = new File(WEB_ROOT, resource);
        if (!file.exists()) {
            return BuildMsgResp(request, "找不到文件：" + resource);
        }
        return new FileResponse(request, file);
    }

    public void writeToOutput(OutputStream output) {
        StringBuilder sb = new StringBuilder();
        sb.append(getVersion()).append(" ").append(getCode()).append(" ")
                .append(getStatus()).append("\n");

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        sb.append("\n");
        try {
            output.write(sb.toString().getBytes());
            writeBodyToOutput(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void writeBodyToOutput(OutputStream output) throws IOException;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "Response{" +
                "version='" + version + '\'' +
                ", code=" + code +
                ", status='" + status + '\'' +
                ", headers=" + headers +
                '}';
    }
}
