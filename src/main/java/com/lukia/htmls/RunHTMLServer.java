package com.lukia.htmls;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.lukia.Config;
import com.lukia.Main;
import com.sun.net.httpserver.HttpServer;


public class RunHTMLServer {

    private final Config config;
    // dataDirectoryは下のgenPHPDirで定義するが、その下のstartPHPServerで使うので、フィールドに書いておく
    private static Path dataDirectory;
    
    public RunHTMLServer() {
        this.config = Main.getConfig();
    }

    public synchronized void genDir() throws IOException {
        // configを作るときに、Jarファイルと同じ階層のパスは取得済みなので、Configクラスからそのパスを取得する。
        Path defaultJarPath = Config.getJarDirPath();

        // ディレクトリPath取得
        RunHTMLServer.dataDirectory = defaultJarPath.resolve("html"); // フィールドにセット
        Path errorDirectory = dataDirectory.resolve("error");
        Path assetsDirectory = dataDirectory.resolve("assets");
        Path cssDirectory = assetsDirectory.resolve("css");
        
        // ディレクトリ内ファイルPath取得
        Path indexPath = dataDirectory.resolve("index.html");
        Path error404Path = errorDirectory.resolve("404.html");
        Path cssPath = cssDirectory.resolve("style.css");
        
        // ディレクトリの作成
        if (Files.notExists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }
        
        if (Files.notExists(errorDirectory)) {
            Files.createDirectories(errorDirectory);
        }

        if (Files.notExists(assetsDirectory)) {
            Files.createDirectories(assetsDirectory);
            if(Files.notExists(cssDirectory)) {
                Files.createDirectories(cssDirectory);
            }
        }

        // ファイルの作成
        if (!Files.exists(indexPath)) {
            try (InputStream in = getClass().getResourceAsStream("/html/index.html")) {
            	if (Objects.isNull(in)) {
            		System.out.println("Default index.html not found in resources/html.");
                    return;
            	}

                Files.copy(in, indexPath);
                
                // 読み込みと新規内容の追加
                String existingContent = Files.readString(indexPath);
                String addContents = "";
                
                // 新しい内容を追加してファイルに書き込み
                Files.writeString(indexPath, existingContent + addContents);
            }
        }

        // ファイルの作成
        if (!Files.exists(error404Path)) {
            try (InputStream in = getClass().getResourceAsStream("/html/error/404.html")) {
            	if (Objects.isNull(in)) {
            		System.out.println("Default 404.html not found in resources/html/error.");
                    return;
            	}

                Files.copy(in, error404Path);
                
                // 読み込みと新規内容の追加
                String existingContent = Files.readString(error404Path);
                String addContents = "";
                
                // 新しい内容を追加してファイルに書き込み
                Files.writeString(error404Path, existingContent + addContents);
            }
        }

        if (!Files.exists(cssPath)) {
            try (InputStream in = getClass().getResourceAsStream("/html/assets/css/style.css")) {
            	if (Objects.isNull(in)) {
            		System.out.println("Default style.css not found in resources/html/style.css.");
                    return;
            	}

                Files.copy(in, cssPath);
                
                // 読み込みと新規内容の追加
                String existingContent = Files.readString(cssPath);
                String addContents = "";
                
                // 新しい内容を追加してファイルに書き込み
                Files.writeString(cssPath, existingContent + addContents);
            }
        }
    }

    public void run() throws IOException {
        // 生成されたConfigから色々読み込む
        boolean allowHtml = config.getBoolean("web-server.html.mode", false);
        if(!allowHtml) {
            // modeがfalseであれば、サーバー起動を中断
            System.err.println("html-web-server is canceled because false given to web-server.html.mode in config.\nif you want to start a server, set web-server.html.mode to true.");
            return;
        }

        int port = config.getInt("web-server.html.port");

        // 指定のポートでHTTPサーバを作成
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // ホーム画面
        // http://localhost:8080でアクセス可能
        server.createContext("/", new HttpHandlers());

        // サーバーを起動
        server.start();

        System.out.println("Running HTML-Server at " + "http://localhost:" + port);
    }

    // HttpHandlersクラスで、用いる
    public static Path getHtmlDir() {
        return RunHTMLServer.dataDirectory;
    }
}