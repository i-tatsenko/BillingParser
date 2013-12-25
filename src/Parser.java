import java.io.*;
import java.net.*;
import java.util.*;

public class Parser {

    public static void main(String[] args) throws Exception{
        String SessionId, username, pass;
        if (args.length < 1 || args[args.length - 1].split("@").length < 2) {
            System.out.println("Bad program call!");
            System.exit(1);
        }
        username = args[args.length - 1].split("@")[0];
        pass = args[args.length - 1].split("@")[1];
        byte[] login = ("phone=" + username + "&pass=" + pass).getBytes();

        //Receiving Cookies while first connection to server
        HttpURLConnection conn = (HttpURLConnection)new URL("https://assa.intertelecom.ua/ru/login/").openConnection();
        conn.setRequestMethod("GET");
        SessionId = conn.getHeaderField("Set-Cookie").split(";")[0];
        printHeader(conn);
        conn.disconnect();

        //Sending authorisation data while second connection to server
        conn = (HttpURLConnection)new URL("https://assa.intertelecom.ua/ru/login/").openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Cookie", SessionId);
        conn.setDoOutput(true);
        conn.getOutputStream().write(login);
        conn.getOutputStream().flush();
        printHeader(conn);
        conn.disconnect();

        //Receiving statistics while third connection to the server
        conn = (HttpURLConnection)new URL("https://assa.intertelecom.ua/ru/statistic/").openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Cookie", SessionId);
        InputStreamReader in =  new InputStreamReader(conn.getInputStream());
        printHeader(conn);
        String htmlPage = getPage(in);
        conn.disconnect();

        System.out.println(findSaldo(htmlPage));





    }

    static void printHeader(HttpURLConnection conn) {
        Map<String, List<String>> header = conn.getHeaderFields();
        for (String key:header.keySet()) {
            System.out.print("KEY: " + key + "  :::  ");
            for (String parameter:header.get(key)) {
                System.out.print(parameter + "; ");
            }
            System.out.println();
        }
        System.out.println("~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~");

    }

    static void printPage(InputStreamReader in) throws Exception{
        int b;
        while((b = in.read()) != -1) {
            System.out.print((char)b);
        }
    }

    static String getPage(InputStreamReader in) throws Exception {
        StringBuffer out = new StringBuffer();
        int b;
        while((b = in.read()) != -1) {
            out.append((char) b);
        }
        return out.toString();
    }


    //this function parses current billing page
    //in case page is changed there are high chances for this code not to work properly
    static String findSaldo(String page) {
        int start = page.indexOf("Сальдо");
        if (start < 0) {
            System.err.print("Can`t find saldo. Check page source!");
            System.exit(1);
        }
        page = page.substring(start + 25);
        page = page.substring(0, page.indexOf("td"));
        page = page.substring(page.indexOf(">"), page.indexOf("<"));
        String[] saldo = page.split("\\s+");
        return saldo[1];
    }

}
