package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;
    private final List<NameValuePair> nameValuePairs;

    public Request(String method, String path, Map<String, String> headers, InputStream in, List<NameValuePair> nameValuePairs) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
        this.nameValuePairs = nameValuePairs;
    }

    public static Request fromInputStream(InputStream in) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        final String requestLine = bufferedReader.readLine();
        final String[] parts = requestLine.split(" ");
        List<NameValuePair> nameValuePairs;

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
        if (method.equals("Get")) {
            nameValuePairs = getQueryParam(parts[1]);
        } else {
            char[] buffer = parseBodyPost(headers);
            nameValuePairs = getPostParams(String.valueOf(buffer));
        }

        return new Request(method, path, headers, in, nameValuePairs);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    private static List<NameValuePair> getQueryParam(String path) {
        return URLEncodedUtils.parse(path, Charset.defaultCharset(), '?');
    }

    private static char[] parseBodyPost(Map<String, String> headers) {
        return new char[Integer.parseInt(headers.get("Content-Length"))];
    }

    private static List<NameValuePair> getPostParams(String body) {
        return URLEncodedUtils.parse(body, Charset.defaultCharset());
    }
}
