package com.project.ws.apiServices;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.project.ws.Model.ResponseMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dropbox {

    private String oauth1_token = "";
    private String oauth1_token_secret = "";
    private String TOKEN = "";
    private static dropbox instance;

    private dropbox(){
        getAuthorizeInfo();
    }

    public static dropbox getInstance() {
        if(instance==null){
            instance = new dropbox();
        }
        return instance;
    }

    public void RefreshToken() {
        JSONParser parser = new JSONParser();

        String url = "https://api.dropboxapi.com/2/auth/token/from_oauth1";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            String auth = oauth1_token + ":" + oauth1_token_secret;
            byte[] encodedBytes = Base64.encodeBase64(auth.getBytes());
            System.out.println("encodedBytes " + new String(encodedBytes));

            con.setRequestProperty("Authorization", "Basic " + new String(encodedBytes));

            con.setDoOutput(true);

            con.setRequestProperty("Content-Type", "application/json");
            String urlParameters = "{\"oauth1_token\":\"" + oauth1_token + "\"" +
                    ",\"oauth1_token_secret\":\"" + oauth1_token_secret + "\"}";
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            Object obj1 = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj1;
            TOKEN = (String) jsonObject.get("oauth2_token");
            System.out.println("TOKEN: " + TOKEN);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void getAuthorizeInfo() {
        JSONParser parser = new JSONParser();
        try {
            System.out.println("getAuthorizeInfo");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream("/dropbox.json" )));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }
            JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
            System.out.println(jsonObject);

            oauth1_token = (String) jsonObject.get("oauth1_token");
            oauth1_token_secret = (String) jsonObject.get("oauth1_token_secret");
            TOKEN = (String) jsonObject.get("token");

            System.out.println("Client_id: " + oauth1_token);
            System.out.println("CLIENT_SECRET: " + oauth1_token_secret);
            System.out.println("TOKEN: " + TOKEN);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getUserInfo() {
        JSONParser parser = new JSONParser();

        if(oauth1_token==null || oauth1_token==""
                || oauth1_token_secret==null || oauth1_token_secret==""
                || TOKEN==null || TOKEN==""){
            getAuthorizeInfo();
            RefreshToken();
        }
        String url = "https://api.dropboxapi.com/2/users/get_current_account";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);

            con.setDoOutput(true);

//            con.setRequestProperty("Content-Type", "application/json");
//            String urlParameters = "{\"oauth1_token\":\"" + oauth1_token + "\"" +
//                    ",\"oauth1_token_secret\":\"" + oauth1_token_secret + "\"}";

//            con.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.flush();
//            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
//            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            Object obj1 = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj1;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public ResponseMessage getListFile(){
        JSONParser parser = new JSONParser();

        String url = "https://api.dropboxapi.com/2/files/list_folder";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);

            con.setDoOutput(true);

            con.setRequestProperty("Content-Type", "application/json");
            String urlParameters = "{\"path\":\"\"}";

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
//            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in;
            if( (con.getResponseCode()>=200) && (con.getResponseCode() < 300) ) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            else{
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            Object obj1 = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj1;

            return new ResponseMessage(200, jsonObject);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        return new ResponseMessage(502, serverErr);
    }

    public int DeleteFile(String fileid) {
        String url = "https://api.dropboxapi.com/2/files/delete";
        URL obj;
        JSONParser parser = new JSONParser();

        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            //con.setRequestProperty("Content-Type","application/json");

            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String urlParameters = "{\"path\": \"/testapp/" + fileid + "\"}";
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            if (responseCode == 200) return 0;
//			Object obj1 = parser.parse(response.toString());

//			JSONObject jsonObject = (JSONObject) obj1;
//			String filelink =	(String) jsonObject.get("webContentLink");
//			System.out.println("Link: " + filelink);
//			return filelink;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
}
