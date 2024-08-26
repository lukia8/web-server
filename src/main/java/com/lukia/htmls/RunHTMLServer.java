package com.lukia.htmls;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.lukia.Config;
import com.lukia.Main;
import com.sun.net.httpserver.HttpServer;


public class RunHTMLServer {

    private final Config config;

    public RunHTMLServer() {
        this.config = Main.getConfig();
    }

    public void run() throws IOException {
        // 生成されるConfigからポートを読み込む
        int port = config.getInt("web-server.port");

        // 指定のポートでHTTPサーバを作成
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // ホーム画面
        // http://localhost:8080でアクセス可能
        server.createContext("/", new HttpHandlers());

        // サーバーを起動
        server.start();

        System.out.println("Running HTML-Server at " + "http://localhost:" + port);
    }
}