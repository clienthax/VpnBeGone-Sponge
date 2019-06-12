package uk.co.haxyshideout.vpnbegone.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpUtils {

    public static String getStringFromSite(String url) {
        String text = "";
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "VPNBeGone");
//            connection.setConnectTimeout(VPNBeGone.getConfig().providers.timeoutMS);//TODO
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                text = reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            try {
                if(connection != null) {
                    System.out.println(connection.getResponseCode());
                    if (connection.getResponseCode() == 429) {//TODO refactor
                        return "429";
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            //VPNBeGone.getLogger().info("Failed to getStringFromUrl "+url);//TODO
        }
        return text;
    }

}
