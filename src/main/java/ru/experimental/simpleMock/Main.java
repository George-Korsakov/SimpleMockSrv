package ru.experimental.simpleMock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


// тестовая заглука для имитации ответа  HTTP сервсиа внешней системы по данным из запроса
// для того, что бы получить ответ запрос должен содержать query в uri
// или слово test
// или заголовоки SOAPAction или Content-Type:text/xml , а в теле POST запроса XML в котором проверяется знаечние query
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // создаем HTTP сервер который принимает запросы на заданном порту и кол-во на очередь соединений
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 5);
        // проверка URI выполняет MyHandler
        server.createContext("/", new SimpleServerTest.Handler());
        //запскаем сервис
        ExecutorService executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.start();
        // ожидание заверщение работы HTTP серврера
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

    }

    public static class SimpleServerTest extends Prices {
        // формирование простейшего ответа с статус кодом 200
        static class MyHandler implements HttpHandler {
            public void handle(HttpExchange t) throws IOException {
                // формирвется и возвращается ответа
                String response = "This is the response";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());

            }
        }

        static class Handler implements HttpHandler {
            public void handle(HttpExchange t) throws IOException {
                // формирвется и возвращается ответа

                // для отладки
                String uri = t.getRequestURI().toString();
                String metod = t.getRequestMethod();
                System.out.println("URI = " + uri);
                System.out.println("query in URI exist  " + uri.contains("?"));
                System.out.println("METOD = " + metod);

                //System.out.println("HEADER Content-Type = " + t.getRequestHeaders().get("Content-Type"));
                if (t.getRequestURI().toString().contains("?")) {
                    System.out.println("TEST if query exist");
                    // получаем URI запроса
                    URI query = t.getRequestURI();
                    // формирует ответ по query в запросе
                    String response = createResponseFromQueryParams(query);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    // формирование ответа при совпадении uri
                } else if (t.getRequestURI().toString().contains("test")) {
                    String response = "TEST";
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    // проверяем наличие в запросе заголовка SOAPAction или Content-Type:text/xml
                }  else if (t.getRequestHeaders().containsKey("SOAPAction") || t.getRequestHeaders().get("Content-Type").contains("text/xml")) {
                    // получаем содержимое телa запроса в виде строки
                    InputStream bodyReq = t.getRequestBody();
                    String bodyXml = new BufferedReader(new InputStreamReader(bodyReq)).lines()
                            .parallel().collect(Collectors.joining("\n"));
                    System.out.println("TEST reqBody: " + bodyXml);
                    // получеаем значение из тела запроса
                    String value = getValueInRequestXMLBody(bodyXml);
                    assert value != null;
                        //  елси в запросе цифры то по ID поиск, если нет то по наименованию
                        if (value.matches("^\\D*$")) {
                            String response = Prices.productByName(value);
                            t.sendResponseHeaders(200, response.length());
                            OutputStream os = t.getResponseBody();
                            os.write(response.getBytes());
                        } else {
                            String response = Prices.productById(value);
                            t.sendResponseHeaders(200, response.length());
                            OutputStream os = t.getResponseBody();
                            os.write(response.getBytes());
                        }
                }  else {
                    // если запрос не подходит ни под одно условие от вовзвращаем ответ с таким текстом
                    String response = " ERROR! Request not contain SOAPAction header or query string";
                    t.sendResponseHeaders(404, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                }
            }

            // возврщает строук по query с его знчением
            private String createResponseFromQueryParams(URI uri) {
                //получение запроса query
                String query = uri.getQuery();
                String queryValue = query.substring(query.indexOf("=")+1);
                // получаем массив из запроса
                List<String> items = Arrays.asList(queryValue.split(","));
                if(items.size()>1){
                    // создаем JSON
                    JSONObject resultJson = new JSONObject();
                    JSONObject obj = new JSONObject();
                    // создаем и наполняем массив в JSON
                    JSONArray ar = new JSONArray();
                    for(int i=0; i<items.size();i++) {
                        ar.add("" + items.get(i) + "");
                        if (i%2 == 0) { ar.add("true");}
                        else { ar.add("false");}
                    }
                    obj.put("Serach result", "SUCCESS");
                    // формируем JSON для ответа
                    resultJson.put("paramsArray", ar);
                    resultJson.put("paramsObj", obj);
                    return resultJson.toString();
                } else {
                    String answer = "{ \"Query in request contain One item\": \"" + items.get(0) + "\" }";
                    return answer;
                }

            }

            // возвразает занчение из строки xml по тегу
            private String getValueInRequestXMLBody(String body) {
                // получаем значене из xml прведставленного в виде строки
                final Pattern pattern = Pattern.compile("<query>(.+?)</query>", Pattern.DOTALL);
                final Matcher matcher = pattern.matcher(body);
                matcher.find();
                System.out.println("TEST DEBUG matcher = " + matcher.group(1));
                String answer = matcher.group(1);
                return answer;
            }
        }

    }
}
