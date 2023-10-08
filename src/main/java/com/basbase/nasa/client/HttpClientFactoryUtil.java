package com.basbase.nasa.client;

import com.basbase.nasa.client.impl.SimpleHttpSocketClient;
import com.basbase.nasa.model.Scheme;
import com.basbase.nasa.model.Uri;
import lombok.SneakyThrows;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class HttpClientFactoryUtil {
    public static HttpClient createHttpClient() {
        return new SimpleHttpSocketClient();
    }

    @SneakyThrows
    public static Socket openSocket(Uri uri) {
        return uri.getScheme().equals(Scheme.HTTP) ?
                new Socket(uri.getHost(), uri.getPort()) :
                SSLSocketFactory.getDefault().createSocket(InetAddress.getByName(uri.getHost()), uri.getPort());
    }

    @SneakyThrows
    public static PrintWriter openPrintWriter(Socket client) {
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
    }

    public static void writeToSocket(PrintWriter writer, String toSocket) {
        writer.write(toSocket);
        writer.flush();
    }

    @SneakyThrows
    public static BufferedReader openInputReader(Socket client) {
        return new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
}
