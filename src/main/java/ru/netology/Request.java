package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;

    public Request(String method, String path, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
    }

    public static Request fromInputStream(InputStream in) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        final String requestLine = bufferedReader.readLine();
        final String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException();
        }
        String method = parts[0];
        String path = parts[1];
        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = bufferedReader.readLine()).equals("")) {
            int indexOf = line.indexOf(":");
            String headerName = line.substring(0, indexOf);
            String headerValue = line.substring(indexOf + 2);
            headers.put(headerName, headerValue);
        }
        return new Request(method, path, headers, in);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
