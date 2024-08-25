package com.lukia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecutePHP {
    public static void main(String[] args) throws IOException {
        // PHPスクリプトを外部プロセスとして実行
        ProcessBuilder processBuilder = new ProcessBuilder("php", "script.php");
        Process process = processBuilder.start();

        // PHPスクリプトの出力を取得
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}

