package ru.experimental.simpleMock;

import java.util.Random;
import java.util.UUID;

public class Prices {

    public static String productById(String id) {
        String name = getRandomString(10);
        Number price = rnd(5);
        String resp = "<?xml version=\"1.0\"?>\n" +
                "<soap:Envelope\n" +
                "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"\n" +
                "soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">\n" +
                "<soap:Body>\n" +
                "  <m:GetPrice xmlns:m=\"https://www.w3schools.com/prices\">\n" +
                "    <m:productId>"+ id + "</m:productId>\n" +
                "    <m:Item>"+ name +"</m:Item>\n" +
                "    <m:Price>"+ price +"</m:Price>\n" +
                "  </m:GetPrice>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>";
        return resp ;
    }


    public static String productByName(String name) {
        String id = generateString();
        Number price = rnd(5);
        String resp = "<?xml version=\"1.0\"?>\n" +
                "<soap:Envelope\n" +
                "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"\n" +
                "soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">\n" +
                "<soap:Body>\n" +
                "  <m:GetPrice xmlns:m=\"https://www.w3schools.com/prices\">\n" +
                "    <m:productId>"+ id + "</m:productId>\n" +
                "    <m:Item>"+ name +"</m:Item>\n" +
                "    <m:Price>"+ price +"</m:Price>\n" +
                "  </m:GetPrice>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>";
        return resp ;
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
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
