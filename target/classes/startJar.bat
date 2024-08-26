@echo off
rem ただビルドしたJarを実行するだけのバッチファイル
rem 拡張機能「Batch Runner」(by Nils Soderman)を導入すれば、右上の再生マークから実行できる。
chcp 65001
rem Java17の環境変数を設定
set path=C:\Program Files\Java\jdk-17\bin;%PATH%
echo switched to java17

cd ./../../../target
rem echo カウントディレクトリ: %~dp0
echo --------------Java Jar Executing Now--------------
java -jar web-server.jar
pause