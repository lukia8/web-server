package com.lukia.htmls;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lukia.Config;
import com.lukia.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpHandlers implements HttpHandler {

    private final Config config;

    public HttpHandlers() {
        this.config = Main.getConfig();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String htmlResponse;

        int htmlPort = config.getInt("web-server.html-port"),
            phpPort = config.getInt("web-server.php-port");
        String currentTime = getCurrentTime();

        // クライアントからのレスポンスによって、場合分けする
        switch(path.toLowerCase()) {
            case "/manage" -> htmlResponse = "<html lang=\"ja\"><head><meta charset=\"UTF-8\"></head><body><h1>管理画面</h1></body></html>";
            default -> {
                htmlResponse = String.format("""
                <html lang="ja">
                    <head>
                        <meta charset="UTF-8">
                    </head>
                    <body>
                        <h1>ホーム画面</h1>
                        <br>
                        <a href="./manage"><h1>管理画面</h1></a>
                        <br>
                        <a href="http://localhost:%s"><h1>phpサーバー</h1></a>
                    </body>
                </html>
                """, phpPort);
            }
        }
            
        // ログ出力
        System.out.println(currentTime + "Accessed at: " + "http://localhost:" + htmlPort + "/" + path);

        // レスポンスのコンテンツタイプを設定
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        // レスポンスのサイズを設定
        exchange.sendResponseHeaders(200, htmlResponse.getBytes(StandardCharsets.UTF_8).length);

        // レスポンスを送信
        try (
            OutputStream os = exchange.getResponseBody(); 
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8); // OutputStreamWriter を使用して UTF-8 でエンコーディングされた文字列を書き込む
        ) {
            writer.write(htmlResponse);
            writer.flush();  // データを強制的に出力
            writer.close();  // ここでOutputStreamを閉じる
            exchange.close();  // exchangeオブジェクトを閉じる
        }
    }

    private String getCurrentTime() {
        // 現在時刻を取得
        LocalDateTime now = LocalDateTime.now();
        // フォーマットを指定
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");
        // フォーマットされた日時を取得
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
}
