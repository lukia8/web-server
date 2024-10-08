package com.lukia.phps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.lukia.Config;
import com.lukia.Main;

public class RunPHPServer {

    private final Config config;
    // dataDirectoryは下のgenPHPDirで定義するが、その下のstartPHPServerで使うので、フィールドに書いておく
    private Path dataDirectory;

    public RunPHPServer() {
        this.config = Main.getConfig();
    }

    public synchronized void genDir() throws IOException {
        // configを作るときに、Jarファイルと同じ階層のパスは取得済みなので、Configクラスからそのパスを取得する。
        Path defaultJarPath = Config.getJarDirPath();
        // フィールドにセット
        this.dataDirectory = defaultJarPath.resolve("php");
        Path indexPath = dataDirectory.resolve("index.php");
        
        // ディレクトリの作成
        if (Files.notExists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }
        
        // ファイルの作成
        if (!Files.exists(indexPath)) {
            try (InputStream in = getClass().getResourceAsStream("/php/index.php")) {
            	if (Objects.isNull(in)) {
            		System.out.println("Default index.php not found in resources.");
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
    }

    public void run() throws IOException {
        boolean allowphp = config.getBoolean("web-server.php.mode", false);
        if(!allowphp) {
            // modeがfalseであれば、サーバー起動を中断
            System.out.println("php-web-server is canceled because false given to web-server.php.mode in config.\nif you want to start a server, set web-server.php.mode to true.");
            return;
        }

        String phpEXEPath = config.getString("web-server.php.php-exe-path", "");
        int phpServerPort = config.getInt("web-server.php.port", 0);

        if(phpEXEPath != null && !phpEXEPath.isEmpty() && phpServerPort != 0) {
            // PHPスクリプトを外部プロセスとして実行
            ProcessBuilder processBuilder = new ProcessBuilder(phpEXEPath, "-S", "localhost:" + phpServerPort);
            // このコマンドを実行する場所をセット
            processBuilder.directory(dataDirectory.toFile());
            // 標準出力と標準エラーを現在のプロセスにリダイレクト(phpの組み込みサーバーに出力されるコンソールを引き継ぐ)
	        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            processBuilder.start();

            System.out.println("Running PHP-Server at " + "http://localhost:" + phpServerPort);
        } else {
            System.err.println("Error: starting php-web-server is canceled caused by an insufficient config given.\nSee config.");
        }
    }
}

