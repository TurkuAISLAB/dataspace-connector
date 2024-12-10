package org.wccsconnector.extensions.datatransfer;


import org.json.JSONObject;
import org.json.JSONArray;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.net.URL;

public class DataPlatformIamAdapter {

    public static String getToken(String tokenUrl, String clientId, String clientSecret){

        String dataPlatformAccessToken = "";
        try {
            URL url = new URL(tokenUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            String request = String.format("client_id=%s&client_secret=%s&grant_type=client_credentials", clientId, clientSecret);
            OutputStream os = con.getOutputStream();
            os.write(request.getBytes());
            os.flush();
            os.close();

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject responseJson = new JSONObject(content.toString());
            dataPlatformAccessToken = responseJson.getString("access_token");
        }
        catch (Exception error){
            System.out.println("Error while fetching Client Token from Data Platform "+error);
        }
        return dataPlatformAccessToken;
    }
}
