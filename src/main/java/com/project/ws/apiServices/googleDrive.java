package com.project.ws.apiServices;

import com.project.ws.Model.ResponseMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;

public class googleDrive {

    private String CLIENT_ID = "";
    private String CLIENT_SECRET = "";
    private String REFRESH_TOKEN = "";
    private String TOKEN = "";
    private static googleDrive instance;

    private googleDrive(){
        getAuthorizeInfo();
    }
    public static googleDrive getInstance() {
        if(instance==null){
            instance = new googleDrive();
        }
        return instance;
    }

    public void getAuthorizeInfo(){
        JSONParser parser = new JSONParser();
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream("/googledrive.json" )));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
//                System.out.println(line);
                sb.append(line);
            }
            JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
            CLIENT_ID = (String) jsonObject.get("client_id");
            CLIENT_SECRET = (String) jsonObject.get("client_secret");
            TOKEN = (String) jsonObject.get("token");
            REFRESH_TOKEN = (String) jsonObject.get("refeshToken");


            System.out.println("Client_id: " + CLIENT_ID);
            System.out.println("CLIENT_SECRET: " + CLIENT_SECRET);
            System.out.println("TOKEN: " + TOKEN);
            System.out.println("REFRESH_TOKEN: " + REFRESH_TOKEN);
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

    public void RefreshToken() {
        JSONParser parser = new JSONParser();
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            getAuthorizeInfo();
        }
        String url = "https://www.googleapis.com/oauth2/v4/token";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            String urlParameters = "client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&refresh_token=" + REFRESH_TOKEN +
                    "&grant_type=refresh_token";
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            System.out.println( con.getResponseMessage() );
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
            TOKEN = (String) jsonObject.get("access_token");
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

    public ResponseMessage GetListFile(String fileId) {
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");

        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }
        String url = "https://www.googleapis.com/drive/v2/files?access_token=" + TOKEN;
        if(fileId!=null){
            url = "https://www.googleapis.com/drive/v2/files/" + fileId + "/children?access_token=" + TOKEN;
        }
        URL obj;
        JSONParser parser = new JSONParser();
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
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

            return new ResponseMessage(con.getResponseCode(), jsonObject);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        }
    }

    public ResponseMessage getFileInfo(String fileId) {
        if(fileId==null){
            JSONObject parameterError = new JSONObject();
            parameterError.put("message", "Missing fileId");
            return new ResponseMessage(400, parameterError);
        }

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }
        String url = "https://www.googleapis.com/drive/v2/files/" + fileId + "?access_token=" + TOKEN;
        URL obj;
        JSONParser parser = new JSONParser();
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
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
            return new ResponseMessage(con.getResponseCode(), jsonObject);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        }
    }

    public ResponseMessage getFileChildren(String fileId) {
        if(fileId==null){
            JSONObject parameterError = new JSONObject();
            parameterError.put("message", "Missing fileId");
            return new ResponseMessage(400, parameterError);
        }

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }
        String url = "https://www.googleapis.com/drive/v2/files/" + fileId + "/children?access_token=" + TOKEN;
        URL obj;
        JSONParser parser = new JSONParser();
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
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
            return new ResponseMessage(con.getResponseCode(), jsonObject);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        }
    }

    public ResponseMessage uploadFile(String filepath, String folderId) {

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }

        JSONParser parser = new JSONParser();
//        filepath = rootPath + File.separator + "Desktop" + File.separator + "Capture.PNG";

        String url = "https://www.googleapis.com/drive/v2/files";
        URL obj;
        String charset ="UTF-8";

        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            JSONObject body = new JSONObject();
            File fileinput = new File(filepath);
            String filename = fileinput.getName();
            body.put("title", filename);
            if(folderId!=null && folderId.length()>0){
                JSONArray parentsArray = new JSONArray();
                JSONObject parent = new JSONObject();
                parent.put("id", folderId);
                parentsArray.add(parent);
                body.put("parents", parentsArray);
            }

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body.toString());
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + body.toString());
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
            String fileid = (String) jsonObject.get("id");
            System.out.println("ID: " + fileid);
            return uploadFileContent(filepath, fileid);
//            return new ResponseMessage(200, jsonObject);

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
        return new ResponseMessage(502, serverErr);
    }

    private ResponseMessage uploadFileContent(String filepath, String fileId){
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }

        JSONParser parser = new JSONParser();
        String rootPath = System.getProperty("user.home");
        System.out.println(rootPath);
//        filepath = rootPath + File.separator + "Desktop" + File.separator + "Capture.PNG";

//        String url = (folderId==null)?
//                "https://www.googleapis.com/upload/drive/v2/files?uploadType=media" :
//                "https://www.googleapis.com/upload/drive/v2/files/" + folderId + "/children?uploadType=media";

        String url = "https://www.googleapis.com/upload/drive/v2/files/"+ fileId+"?uploadType=media";
        URL obj;
        String charset ="UTF-8";
        try {
            File fileinput = new File(filepath);
            String filename = fileinput.getName();
            System.out.println("File name: " + filename);
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
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
            String fileid = (String) jsonObject.get("id");
            System.out.println("ID: " + fileid);
            return new ResponseMessage(200, jsonObject);
//            return renameFile(fileid, filename, folderId);
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
        return new ResponseMessage(502, serverErr);
    }

    public ResponseMessage renameFile(String fileid, String filename, String folderId) throws ParseException {
        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");

        String url = "https://www.googleapis.com/drive/v3/files/" + fileid;
        URL obj;
        JSONParser parser = new JSONParser();

        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String urlParameters = "{\"name\":\"" + filename + "\"}";

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
            Object obj1 = parser.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj1;
            fileid = (String) jsonObject.get("id");
            System.out.println("Link: " + fileid);

            return new ResponseMessage(200, jsonObject);

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
        return new ResponseMessage(502, serverErr);
    }

    public ResponseMessage deleteFile(String fileId) {
        if(fileId==null){
            JSONObject parameterError = new JSONObject();
            parameterError.put("message", "Missing fileId");
            return new ResponseMessage(400, parameterError);
        }

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");

        String url = "https://www.googleapis.com/drive/v3/files/" + fileId;
        URL obj;
        JSONParser parser = new JSONParser();

        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            //con.setRequestProperty("Content-Type","application/json");
            //con.setDoOutput(true);
            //String urlParameters = "{\"name\":\""+FILENAME+"\"}";
            //con.setDoOutput(true);
            //DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //wr.writeBytes(urlParameters);
            //wr.flush();
            //wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'DELETE' request to URL : " + url);
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

            //print result
            System.out.println(response.toString());
            if(response.toString().length()>0){
                Object obj1 = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj1;
                return new ResponseMessage(con.getResponseCode(),jsonObject);
            } else{
                JSONObject okay = new JSONObject();
                okay.put("message", "Delete success");
                return new ResponseMessage(con.getResponseCode(),okay);
            }

//			Object obj1 = parser.parse(response.toString());

//			JSONObject jsonObject = (JSONObject) obj1;
//			String filelink =	(String) jsonObject.get("webContentLink");
//			System.out.println("Link: " + filelink);
//			return filelink;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseMessage(502, serverErr);
        }

    }

    public ResponseMessage createFolder(String parentFolderId, String folderName) {

        if(folderName==null){
            JSONObject parameterError = new JSONObject();
            parameterError.put("message", "Missing folderName");
            return new ResponseMessage(400, parameterError);
        }

        JSONObject serverErr = new JSONObject();
        serverErr.put("message", "Internal server error");
        if(CLIENT_ID==null || CLIENT_ID==""
                || CLIENT_SECRET==null || CLIENT_SECRET==""
                || REFRESH_TOKEN==null || REFRESH_TOKEN==""){
            RefreshToken();
        }

        JSONParser parser = new JSONParser();
//        filepath = rootPath + File.separator + "Desktop" + File.separator + "Capture.PNG";

        String url = "https://www.googleapis.com/drive/v2/files";
        URL obj;
        String charset ="UTF-8";

        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            JSONObject body = new JSONObject();
            body.put("title", folderName);
            body.put("mimeType", "application/vnd.google-apps.folder");
//            String urlParameters = "{\"name\":\"" + filename + "\"}";
            if(parentFolderId!=null && parentFolderId.length()>0){
                JSONArray parentsArray = new JSONArray();
                JSONObject parent = new JSONObject();
                parent.put("id", parentFolderId);
                parentsArray.add(parent);
                body.put("parents", parentsArray);
            }
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body.toString());
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
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

            return new ResponseMessage(200, jsonObject);

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
        return new ResponseMessage(502, serverErr);

    }

    public int Authorization() {

        return 0;
    }
}
