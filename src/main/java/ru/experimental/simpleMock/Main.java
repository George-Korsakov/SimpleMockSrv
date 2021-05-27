package ru.experimental.simpleMock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // создаем HTTP сервер который принимает запросы на заданном порту и кол-во на очередь соединений
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 5);
        // проверка URI выполняет MyHandler
        server.createContext("/", new SimpleServerTest.queryHandler());

        String addrr = server.getAddress().toString();
        System.out.println("DEBUG: " + addrr) ;

        //server.createContext("?query=", new SimpleServerTest.queryHandler());
        ExecutorService executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.start();


        // ожидание заверщение работы HTTP серврера
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

    }

    public static class SimpleServerTest {

        static class MyHandler implements HttpHandler {
            public void handle(HttpExchange t) throws IOException {
                // формирвется и возвращается ответа
                String response = "This is the response";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());

            }
        }

        static  class  queryHandler implements  HttpHandler {
            public void handle(HttpExchange t) throws IOException {
                // формирвется и возвращается ответа

                //Create a response form the request query parameters
                URI uri = t.getRequestURI();
                String url = t.getRequestURI().toString();
                String metod = t.getRequestMethod().toString();
                String body = t.getRequestBody().toString();
                String headers = t.getRequestHeaders().toString();
                System.out.println("URI = " + url);
                System.out.println("METOD = " + metod);
                System.out.println("HEADERS = " + headers);
                System.out.println("BODY = " + body);

                String response = createResponseFromQueryParams(uri);
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());

            }

            private String createResponseFromQueryParams(URI uri) {
                //получение запроса query
                String query = uri.getQuery();
                String answer = "Query contains: " + query;
                return answer;
            }

            public void handleIN(HttpExchange t) throws IOException {
                String uri = t.getRequestURI().toString();
                String metod = t.getRequestMethod().toString();
                String body = t.getRequestBody().toString();
                String headers = t.getRequestHeaders().toString();
                System.out.println("URI = " + uri);
                System.out.println("METOD = " + metod);
                System.out.println("HEADERS = " + headers);
                System.out.println("BODY = " + body);

            }
        }

    }
}