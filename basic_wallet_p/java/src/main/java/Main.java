import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import javafx.util.Pair;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter id");
            String name = input.nextLine();
            if (name.equals("q")) {
                break;
            }
            Pair<Long, ArrayList<String>> values = getValues(getChain(), name);
            System.out.println(values.getKey());
            System.out.println(values.getValue());
        }


    }

    private static String getChain() {
        String url = "http://0.0.0.0:5000/chain";
        OkHttpClient client = new OkHttpClient();
        Request chain = new Request.Builder().url(url).method("GET", null).build();
        String chainList = "";
        try {
            Response response = client.newCall(chain).execute();
            chainList = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chainList;
    }

    private static Pair<Long, ArrayList<String>> getValues(String json, String id) {
        long total = 0;
        ArrayList<String> list = new ArrayList<String>();
        try {
            Object obj = new JSONParser().parse(json);
            JSONObject jo = (JSONObject) obj;
            JSONArray chain = (JSONArray) jo.get("chain");
            for (Object o : chain) {
                JSONObject items = (JSONObject) o;
                JSONArray info = (JSONArray) items.get("transactions");
                if (info.size() > 0) {
                    for (Object o2 : info) {
                        JSONObject transaction = (JSONObject) o2;
                        long amount = (long) transaction.get("amount");
                        String recipient = (String) transaction.get("recipient");
                        String sender = (String) transaction.get("sender");
                        if (recipient.equals(id) || sender.equals(id)) {
                            list.add(o2.toString());
                        }
                        if (recipient.equals(id)) {
                            total += amount;
                        }
                        if (sender.equals(id)) {
                            total -= amount;
                        }
                    }
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return new Pair<>(total, list);
    }
}
