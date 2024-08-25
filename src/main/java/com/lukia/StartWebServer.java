package com.lukia;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class StartWebServer {

    public static void main(String[] args) throws IOException {

        // 生成されるConfigからポートを読み込む
        int port = Main.getConfig().getInt("web-server.port");

        // 指定のポートでHTTPサーバを作成
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);


        // ホーム画面
        // http://localhost:8000でアクセス可能
        server.createContext("/", new RootHandler());

        // 管理画面
        server.createContext("/manage", new ManageHandler());

        // サーバーを起動
        server.start();

        System.out.println("実行ポート：" + port);
    }


    // ホーム画面の処理を記載
    public static class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // 画面に表示するHTMLコンテンツ
            String htmlResponse = "<html lang=\"ja\"><head><meta charset=\"UTF-8\"></head><body><h1>ホーム画面</h1><br><h1 href=\"./manage\">管理画面</h1><h1 href=\"./test\">テスト</h1></body></html>";

            // ログ出力
            System.out.println("RootHandlerが呼び出されました。");

            // レスポンスのコンテンツタイプを設定
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

            // レスポンスのサイズを設定
            exchange.sendResponseHeaders(200, htmlResponse.getBytes(StandardCharsets.UTF_8).length);

            // レスポンスを送信
            try (
                OutputStream os = exchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                ) {
                // OutputStreamWriter を使用して UTF-8 でエンコーディングされた文字列を書き込む
                
                writer.write(htmlResponse);
                writer.flush();  // データを強制的に出力
                writer.close();  // ここでOutputStreamを閉じる
                // 上のos.close();がないと、データが正しく送信されず、文字化けが発生する可能性がある。
                exchange.close();  // exchangeオブジェクトを閉じる
            }
        }
    }

    // 管理画面の処理を記載
    public static class ManageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 画面に表示するHTMLコンテンツ
            String htmlResponse = "<html lang=\"ja\"><head><meta charset=\"UTF-8\"></head><body><h1>管理画面</h1></body></html>";

            // レスポンスのコンテンツタイプを設定
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

            // レスポンスのヘッダー情報を設定
            exchange.sendResponseHeaders(200, htmlResponse.getBytes(StandardCharsets.UTF_8).length);

            // レスポンスを送信
            try (
                OutputStream os = exchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                ) {
                // OutputStreamWriter を使用して UTF-8 でエンコーディングされた文字列を書き込む
                
                writer.write(htmlResponse);
                writer.flush();  // データを強制的に出力
                writer.close();  // ここでOutputStreamを閉じる
                // 上のos.close();がないと、データが正しく送信されず、文字化けが発生する可能性がある。
                exchange.close();  // exchangeオブジェクトを閉じる
            }
        }
    }
}