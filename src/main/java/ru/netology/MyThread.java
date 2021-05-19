package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;

public class MyThread implements Runnable {
    private final Socket socket;
    private final List<String> validPath;
    private final Map<String, Map<String, Handler>> handlers;

    public MyThread(List<String> validPath, Socket socket, Map<String, Map<String, Handler>> handlers) throws IOException {
        this.socket = socket;
        this.validPath = validPath;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (final InputStream in = socket.getInputStream();
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            while (!currentThread().isInterrupted()) {
                Request requestLine = Request.fromInputStream(in);
                Map<String, Handler> handlerMap = handlers.get(requestLine.getMethod());
                if (handlerMap != null) {
                    Handler handler = handlerMap.get(requestLine.getPath());
                    if (handler != null) {
                        handler.handle(requestLine, out);
                    } else {
                        invalidPath(out);
                    }
                } else {
                    invalidPath(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invalidPath(BufferedOutputStream out) {
        try {
            out.write(("HTTP/1.1 404Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
