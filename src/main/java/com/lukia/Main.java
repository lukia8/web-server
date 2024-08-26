package com.lukia;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lukia.htmls.RunHTMLServer;
import com.lukia.phps.RunPHPServer;

public class Main {
    public static Config config;
    // Maven->Lifecycle->packageでJarにした後、Manifestで登録したエントリーポイントより一番最初に読み込まれ、開始されるメソッド
    // 左上の再生マークでもここのメソッドは開始できる
    // Jarパッケージにした後は、resourcesディレクトリにあるstartJar.batを参考に、.jarファイルを実行できる。
    // このメソッドから枝分かれしていくイメージ
    public static void main(String[] args) throws IOException {
        System.out.println("This is first loading class!!");

        Logger logger = LoggerFactory.getLogger("web-server");

        try {
            Config.main(new String[] {"",""}); // 左のように引数をいくつかStringのリスト形式に渡せる
            Main.config = new Config();
            // configファイルを生成
            config.loadConfig();
        } catch (IOException | URISyntaxException e) {
            logger.error("An IOException | URISyntaxException error occurred: " + e.getMessage(), e);
            // configファイルを生成できなかったら、このメソッドを中断
            return;
        }

        // web-server起動
        RunHTMLServer html = new RunHTMLServer();
        html.run();

        // phpの組み込みweb-serverを起動
        RunPHPServer php = new RunPHPServer();
        // Jarと同じ階層に、phpディレクトリを作り、index.phpをいれておく。
        php.genDir();
        php.run();
    }

    // try構文でerrorをcatchしなかったら、以降、ここのゲッターからconfigインスタンスにアクセス可能
    public static Config getConfig() {
        return Main.config;
    }
}