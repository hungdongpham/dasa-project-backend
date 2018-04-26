package com.project.ws.apiServices;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.project.ws.Model.ResponseMessage;
import org.json.simple.JSONArray;
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

    public ResponseMessage getListFile(String path){
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
            String urlParameters;
            if(path==null)
                urlParameters = "{\"path\":\"\"}";
            else
                urlParameters = "{\"path\":\""+ path + "\"}";

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

    public ResponseMessage deleteFile(String filePath) {
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
            String urlParameters = "{\"path\": \"" + filePath + "\"}";
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
            Object obj1 = parser.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj1;

            System.out.println(response.toString());
            return new ResponseMessage(con.getResponseCode(), jsonObject);
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        return new ResponseMessage(502, serverErr);
    }

    public ResponseMessage uploadFile(String filepath, String folderPath) {
        JSONParser parser = new JSONParser();

        URL obj;
        try {
            File fileinput = new File(filepath);

            String filename = fileinput.getName();
            String url = "https://content.dropboxapi.com/2/files/upload";

            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);

            if(folderPath!=null && folderPath.length()>0){
                con.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"" + folderPath +"/" +filename + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}");
            } else{
                con.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"/" + filename + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}");
            }

            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            FileInputStream fin = new FileInputStream(filepath);
            int c = 0;
            byte[] buf = new byte[8192];
            while ((c = fin.read(buf, 0, buf.length)) > 0) {
                wr.write(buf, 0, c);
                wr.flush();
            }
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            //System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            Object obj1 = parser.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj1;

            return new ResponseMessage(con.getResponseCode(), jsonObject);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
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

    public ResponseMessage createFolder(String parentFolderPath, String path) throws Exception {
        JSONParser parser = new JSONParser();
        try {
            if(parentFolderPath==null)
                parentFolderPath="";
            URL url = new URL("https://api.dropboxapi.com/2/files/create_folder");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            String parameters = "{\"path\": \"" +parentFolderPath + "/" + path + "\"}";

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestMethod("POST");


            con.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(con.getOutputStream());
            writer.writeBytes(parameters);
            writer.flush();

            if (writer != null)
                writer.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            //System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in;
            if(con.getResponseCode() >= 400){
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            } else{
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            Object obj1 = parser.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj1;
            System.out.println(jsonObject.toJSONString());

            return new ResponseMessage(con.getResponseCode(), jsonObject);

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        return new ResponseMessage(502, serverErr);
    }

    public ResponseMessage renameFile(String id, String newName) throws Exception {
        JSONParser parser = new JSONParser();

        if(id==null || id.length()<=0){
            JSONObject err =  new JSONObject();
            err.put("message", "Empty id");
            return new ResponseMessage(400, err);
        }

        if(newName==null || newName.length()<=0){
            JSONObject err =  new JSONObject();
            err.put("message", "Empty name");
            return new ResponseMessage(400, err);
        }

        try {

            String parentFolderPath = id.substring(0, id.lastIndexOf('/'));

            URL url = new URL("https://api.dropboxapi.com/2/files/move_v2");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            String parameters = "{ \"from_path\":\"" +id + "\","+
                                    "\"to_path\": \"" +parentFolderPath + "/" + newName + "\"}";
            System.out.println(parameters);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestMethod("POST");


            con.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(con.getOutputStream());
            writer.writeBytes(parameters);
            writer.flush();

            if (writer != null)
                writer.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            //System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in;
            if(con.getResponseCode() >= 400){
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            } else{
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            Object obj1 = parser.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj1;
            System.out.println(jsonObject.toJSONString());

            return new ResponseMessage(con.getResponseCode(), jsonObject);

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        return new ResponseMessage(502, serverErr);
    }


}
