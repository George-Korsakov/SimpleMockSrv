
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import unirest.shaded.com.google.gson.JsonArray;
import unirest.shaded.com.google.gson.JsonElement;
import unirest.shaded.com.google.gson.JsonObject;
import unirest.shaded.com.google.gson.JsonParser;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;


public class TestSimpleMock {
    // простые тесты для проверки simpleMock по запросу на каждую ветку
    @BeforeClass
    public void setUp() {
        Unirest.config()
                .socketTimeout(1000)
                .connectTimeout(1000)
                .followRedirects(true);
    }

    @AfterClass
    public void tearDown(){
        Unirest.shutDown();
    }

    @Test
    public void testQuery() {
        String url = "http://localhost:8080/query?";
        String query = generateString();
        String  resp =  Unirest.get(url)
                .header("Accept", "application/json")
                .queryString("ID", query)
                .asString()
                .getStatusText();
        assertEquals("OK", resp);

    }
    @Test
    public void testQueryRespJson() {
        String url = "http://localhost:8080/query?";
        String ID1 = generateString();
        String ID2 = generateString();
        String query = ID1 + "," + ID2 ;

        String  respBody =  Unirest.get(url)
                .header("Accept", "application/json")
                .queryString("ID", query)
                .asString()
                .getBody();
        assertTrue(respBody.contains("checking"));
        // парсинг JSON ответа (в ответе массив объектов)
        JsonObject jsonObject = JsonParser.parseString(respBody).getAsJsonObject();
        JsonArray ja = (JsonArray) jsonObject.get("Answers");
        JsonObject joFirst = ja.get(0).getAsJsonObject();
        JsonObject joSecond = ja.get(1).getAsJsonObject();

        // проверка наличия в ответе ID из запроса
        assertEquals(ID1, joFirst.get("ObjId").toString().replaceAll("^\"+|\"+$", ""));
        assertEquals(ID2, joSecond.get("ObjId").toString().replaceAll("^\"+|\"+$", ""));
        // проваерка, что checking может иметь значение true или false
        assertTrue(joFirst.get("checking").toString().contains("true") || joFirst.get("checking").toString().contains("false") );
        assertTrue(joSecond.get("checking").toString().contains("true") || joSecond.get("checking").toString().contains("false") );
        System.out.println("\n Debug: request" + query + " \n  responce Body: "+ respBody);
    }

    @Test
    public void testRequestXMLByName() {
        String url = "http://localhost:8080/";
        String name = getRandomString(10);
        String body = generateXMLprice(name);
        // формируем запрос с query в виде строки
        String  respBody =  Unirest.post(url)
                .header("SOAPAction", "http://localhost:8080/test")
                .header("Content-Type","text/xml; charset=UTF-8")
                .body(body)
                .asString()
                .getBody();

        System.out.println("DEBUG : request" + body + " \n  responce Body: "+ respBody);

        assertTrue(respBody.contains("Item"));
        assertTrue(respBody.contains("Price"));
        assertTrue(respBody.contains("productId"));
        assertEquals(name, getValueInRequestXMLBody(respBody, "m:Item"));
        assertTrue(Integer.parseInt(getValueInRequestXMLBody(respBody, "m:Price")) >= 0);


    }

    @Test
    public void testRequestXMLById() {
        String url = "http://localhost:8080/";
        String name = String.valueOf(rnd(1000));
        String body = generateXMLprice(name);
        // формируем запрос с query содержит число
        String respBody =  Unirest.post(url)
                .header("SOAPAction", "http://localhost:8080/test")
                .header("Content-Type","text/xml; charset=UTF-8")
                .body(body)
                .asString()
                .getBody();

        System.out.println("DEBUG2 : request" + body + " \n  responce Body: "+ respBody);
        assertTrue(respBody.contains("Item"));
        assertTrue(respBody.contains("Price"));
        assertTrue(respBody.contains("productId"));
        assertEquals(name, getValueInRequestXMLBody(respBody, "m:productId"));
        assertTrue(Integer.parseInt(getValueInRequestXMLBody(respBody, "m:Price")) >= 0);
    }


    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return  uuid;
    }

    public static int rnd(int max)
    {
        return (int) (Math.random() * ++max);
    }

    // генерация случайной строки заданной длинны
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(52);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String generateXMLprice( String value){

        String XMLstr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "      <query>"+value+"</query>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        return XMLstr;
    }

    // возвразает занчение из строки xml по тегу
    private String getValueInRequestXMLBody(String body, String param) {
        // получаем значене из xml прведставленного в виде строки
        final Pattern pattern = Pattern.compile("<"+param+">(.+?)</"+param+">", Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(body);
        matcher.find();
        //System.out.println("TEST DEBUG matcher = " + matcher.group(1));
        String answer = matcher.group(1);
        return answer;
    }

}
