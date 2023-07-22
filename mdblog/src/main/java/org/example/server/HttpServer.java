package org.example.server;

import org.example.md.FileTranslator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer {

    private final ThreadPoolExecutor taskExecutor;
    private final FileTranslator translator;

    public HttpServer(FileTranslator translator) {
        this.translator = translator;
        int nThreads = Runtime.getRuntime().availableProcessors();
        taskExecutor = new ThreadPoolExecutor(nThreads, nThreads, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("服务器启动，开始监听端口[%d]\n", port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(socket);
                    HttpTask task = new HttpTask(socket, translator);
                    taskExecutor.submit(task);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
