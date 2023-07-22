package org.example.server;

import org.example.utils.BytesUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Request {

    private static final String encoding = "UTF-8";

    private String method;
    private String uri;
    private String version;
    private Map<String, String> headers;
    private byte[] body;

    private Request() {}

    public static Request BuildFromInputStream(InputStream inputStream) {
        Request request = new Request();
        try {
            byte[] buffer = new byte[4096];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int binaryEnd = bufferedInputStream.read(buffer);
            int end = BytesUtil.indexOf(buffer, "\r\n\r\n".getBytes());
            String requestHeader = new String(Arrays.copyOfRange(buffer, 0, end + 4));
            BufferedReader httpReader = new BufferedReader(new StringReader(requestHeader));
            // 解析请求头部首行
            String requestLine = httpReader.readLine();
            String[] tokens = requestLine.split(" ");
            request.setMethod(tokens[0]);
            request.setUri(tokens[1]);
            request.setVersion(tokens[2]);
            // 解析请求头部其他选项
            Map<String, String> headers = new HashMap<>(16);
            String line = httpReader.readLine();
            String[] kvEntry;
            while (!"".equals(line)) {
                kvEntry = line.split(":");
                headers.put(kvEntry[0].trim(), kvEntry[1].trim());
                line = httpReader.readLine();
            }
            request.setHeaders(headers);
            // 获取请求体内容
            int contentLen = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (contentLen != 0) {
                byte[] body = new byte[contentLen];
                int bufferedBodyLen = binaryEnd - end - 4;
                System.arraycopy(buffer, end + 4, body, 0, bufferedBodyLen);
                while (bufferedBodyLen < contentLen) {
                    int readLen = bufferedInputStream.read(body,  bufferedBodyLen, contentLen - bufferedBodyLen);
                    bufferedBodyLen += readLen;
                }
                request.setBody(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
            request = new Request();
        }
        return request;
    }

    public static Request BuildFromInputStreamOld(InputStream inputStream) {
        Request request = new Request();
        try {
            BufferedReader httpReader = new BufferedReader(new InputStreamReader(inputStream, encoding));
            // 解析请求头部首行
            String requestLine = httpReader.readLine();
            String[] tokens = requestLine.split(" ");
            request.setMethod(tokens[0]);
            request.setUri(tokens[1]);
            request.setVersion(tokens[2]);
            // 解析请求头部其他选项
            Map<String, String> headers = new HashMap<>(16);
            String line = httpReader.readLine();
            String[] kvEntry;
            while (!"".equals(line)) {
                kvEntry = line.split(":");
                headers.put(kvEntry[0].trim(), kvEntry[1].trim());
                line = httpReader.readLine();
            }
            request.setHeaders(headers);
            // 获取请求体内容
            int contentLen = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (contentLen != 0) {
                byte[] body = new byte[contentLen];
                if (inputStream.read(body) != -1)
                    request.setBody(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
            request = new Request();
        }
        return request;
    }

    public Map<String, File> parseFiles() throws IOException {
        Map<String, File> files = new HashMap<>();
        String contentType = getHeaders().get("Content-Type");
        String[] splitContentType = contentType.split("; ");
        if (splitContentType.length < 2) {
            return null;
        }
        String boundary = splitContentType[1].replace("boundary=", "");
        byte[] body = getBody();
        byte[][] splitBody = BytesUtil.split(body, ("--" + boundary + "\r\n").getBytes());
        for (byte[] bodyCell : splitBody) {
            int headEnd = BytesUtil.indexOf(bodyCell, "\r\n\r\n".getBytes());
            String headStr = new String(Arrays.copyOfRange(bodyCell, 0, headEnd));
            if (!headStr.contains("Content-Disposition") || !headStr.contains("Content-Type"))
                continue;
            String fileName = null, fileType = null;
            String[] headLines = headStr.split("\r\n");
            for (String line : headLines) {
                if (line.contains("Content-Disposition")) {
                    int idx = line.indexOf("filename=");
                    if (idx == -1) continue;
                    fileName = line.substring(idx + 9);
                    if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                        fileName = fileName.substring(1, fileName.length() - 1);
                    }
                } else if (line.contains("Content-Type")) {
                    fileType = line.substring(15);
                }
            }
            if (fileName == null) continue;
            int startBinary = headEnd + 4;
            int endBinary = BytesUtil.indexOf(bodyCell, ("\r\n--" + boundary).getBytes(), startBinary);
            if (endBinary == -1) {
                endBinary = bodyCell.length - 2;
            }
            File file;
            if (fileName.endsWith(".md")) {
                file = new File("blog", fileName);
            } else {
                file = new File("webroot/pic", fileName);
            }
            boolean newFile = file.createNewFile();
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(bodyCell, startBinary, endBinary - startBinary);
            writer.close();
            files.put(file.getName(), file);
        }
        return files;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
