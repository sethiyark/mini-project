package com.example.raunaksethiya.reviewsys1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Raunak Sethiya on 14-Sep-16.
 */
public class PostToServer {

    private String link;

    public PostToServer(String link) {
        this.link = link;
    }

    public String postToServer() {
        URLConnection conn;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            URL url = new URL(link);
            conn = url.openConnection();
            conn.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"), 8);
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();

        } catch (IOException e) {
            //Toast.makeText(RegisterActivity.this, "exception urlconn",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

}
