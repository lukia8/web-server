package com.lukia.htmls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;

import com.lukia.Config;
import com.lukia.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpHandlers implements HttpHandler {

    private final Config config;
    private final Logger logger;

    public HttpHandlers() {
        this.config = Main.getConfig();
        this.logger = Main.getLogger();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String htmlResponse = null;

        int htmlPort = config.getInt("web-server.html.port"),
            phpPort = config.getInt("web-server.php.port");
        String currentTime = getCurrentTime();
        boolean isOK = true;
        Path htmlDirPath = RunHTMLServer.getHtmlDir();
        Path htmlErrorDirPath = htmlDirPath.resolve("error");

        // クライアントからのレスポンスによって、場合分けする
        switch(path.toLowerCase()) {
            case "/" -> {
                Path htmlIndexPath = htmlDirPath.resolve("index.html");
                String index = getFile(htmlIndexPath);
                if(Objects.nonNull(index)) {
                    htmlResponse = index;
                } else {
                    htmlResponse = String.format("""
                    <html lang="ja">
                        <head>
                            <meta charset="UTF-8">
                        </head>
                        <body>
                            <h1>index.htmlが存在しません！</h1>
                        </body>
                    </html>
                    """);
                }
            }
            case "/manage" -> {
                htmlResponse = "<html lang=\"ja\"><head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"./assets/css/style.css\"></head><body><h1>管理画面だよ</h1></body></html>";
            }
            case "/test" -> {
                htmlResponse = String.format("""
                <html lang="ja">
                    <head>
                        <meta charset="UTF-8">
                        <link rel="stylesheet" href="./assets/css/style.css">
                    </head>
                    <body>
                        <h1>テストだよ</h1>
                    </body>
                </html>
                """, phpPort);
            }
            // 上のcaseで場合分けしているリクエストPATH以外のものは、すべてdataDirectory/html/filename.htmlに飛ばす。
            default -> {
                // pathの先頭一文字を切り落とす
                path = path.substring(1);

                Path htmlFilePath = htmlDirPath.resolve(path); // htmlディレクトリ内の<path>.htmlのパス取得
                Path htmlError404Path = htmlErrorDirPath.resolve("404.html");

                String source = getFile(htmlFilePath);
                String error = getFile(htmlError404Path);

                if(Objects.nonNull(source)) {
                    htmlResponse = source;
                } else {
                    // 404 not found
                    isOK = false;
                    if(Objects.nonNull(error)) {
                        htmlResponse = error;
                    }
                }
            }
        }
            
        // ログ出力
        System.out.println("[" + currentTime + "]" + " Accessed at: " + "http://localhost:" + htmlPort + "/" + path);

        // レスポンスのコンテンツタイプを設定
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        // レスポンスのサイズを設定
        if(isOK) {
            if(htmlResponse != null) {
                exchange.sendResponseHeaders(200, htmlResponse.getBytes(StandardCharsets.UTF_8).length);
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        } else {
            if(htmlResponse != null) {
                exchange.sendResponseHeaders(404, htmlResponse.getBytes(StandardCharsets.UTF_8).length);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }

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

    private String getFile(Path path) {
        // ファイルが存在するか確認
        if (!Files.exists(path)) {
            return null;
        }

        // InputStreamを使用してファイルを読み込み
        try (InputStream inputStream = Files.newInputStream(path)) {
            // InputStreamからすべてのバイトを読み込み、それを文字列に変換して返す
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("An IOException error occurred: " + e.getMessage(), e);
            return null;
        }
    }

    private String getCurrentTime() {
        // 現在時刻を取得
        LocalDateTime now = LocalDateTime.now();
        // フォーマットを指定
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
        // フォーマットされた日時を取得
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
}
