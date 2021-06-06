import kong.unirest.Unirest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestSimpleMock {

/*    @BeforeAll
    public void startSimpleMock() {
       // new ru.experimental.simpleMock.Main();
    }*/

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
        String query = generateString() + "," + generateString() ;

        String  respBody =  Unirest.get(url)
                .header("Accept", "application/json")
                .queryString("ID", query)
                .asString()
                .getBody();
        assertTrue(respBody.contains("checking"));
        System.out.println("Debug: " + query + " \n "+ respBody);
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
}
