import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame();
        JButton b = new JButton("Submit");
        JTextField tf = new JTextField("Enter Id");
        JTextArea money = new JTextArea();
        JTextArea transactions = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(transactions);


        tf.setBounds(100, 100, 200, 40);
        money.setBounds(100, 160, 200, 16);
        scrollPane.setBounds(20, 200, 355, 200);
        b.setBounds(130, 425, 130, 40);

        money.setEditable(false);
        transactions.setEditable(false);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = tf.getText();
                Pair<Long, ArrayList<String>> values = getValues(getChain(), name);
                money.setText("Monies: " + values.getKey());
                transactions.setText(String.valueOf(formatTransactions(values.getValue())));
            }
        });

        f.add(tf);
        f.add(money);
        f.add(scrollPane);
        f.add(b);

        f.setSize(400, 500);
        f.setLayout(null);
        f.setVisible(true);
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
    public static ArrayList<String> formatTransactions(ArrayList<String> list) {
        ArrayList<String> format = new ArrayList<String>();
        for (String transaction : list) {
            String formatted = transaction + "\n";
            format.add(formatted);
        }
        return format;
    }
}
