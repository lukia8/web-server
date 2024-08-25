package com.lukia;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Sample01 {

   public static void main(String[] args) throws IOException {

      int port = 8080;


      // 指定のポートでHTTPサーバを作成

      HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);


      // ホーム画面

      server.createContext("/", new RootHandler());


      // 管理画面

      server.createContext("/manage", new ManageHandler());


      // サーバーを起動

      server.start();

      System.out.println("実行ポート：" + port);

   }


   // ホーム画面の処理を記載

   static class RootHandler implements HttpHandler {

      @Override

      public void handle(HttpExchange exchange) throws IOException{

         // 画面に表示するHTMLコンテンツ

         String htmlResponse = "<html><body><h1>ホーム画面</h1></body></html>";


         // レスポンスのコンテンツタイプを設定

         exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");


         // レスポンスのサイズを設定

         exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);


         // レスポンスを送信

         OutputStream os = exchange.getResponseBody();

         os.write(htmlResponse.getBytes());


      }

   }


   // 管理画面の処理を記載

   static class ManageHandler implements HttpHandler {

      @Override

      public void handle(HttpExchange exchange) throws IOException {

         // 画面に表示するHTMLコンテンツ

         String htmlResponse = "<html><body><h1>管理画面</h1></body></html>";


         // レスポンスのコンテンツタイプを設定

         exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");


         // レスポンスのヘッダー情報を設定

         exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);


         // レスポンスを送信

         OutputStream os = exchange.getResponseBody();

         os.write(htmlResponse.getBytes());

      }

   }

}