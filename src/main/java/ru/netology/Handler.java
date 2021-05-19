package ru.netology;

import java.io.*;

@FunctionalInterface
public interface Handler {
    void handle(Request request, BufferedOutputStream out);
}