package com.project.ws.common;

import com.mysql.jdbc.CommunicationsException;
import org.jboss.resteasy.util.MethodHashing;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.sun.activation.registries.LogSupport.log;

public class Utils {

    public static Connection makeJDBCConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            log("Congrats - Seems your MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            log("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
            e.printStackTrace();
            return null;
        }

        try {
            // DriverManager: The basic service for managing a set of JDBC drivers.
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/drivecloud", "root", "");
            if (conn != null) {
                log("Connection Successful! Enjoy. Now it's time to push data");
                return conn;
            } else {
                log("Failed to make connection!");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
            return null;
        } catch(Exception e){
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * header sample
     * {
     * 	Content-Type=[image/png],
     * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    //get uploaded filename, is there a easy way in RESTEasy?
    public static String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    public static String sha256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public static JSONObject mapGoogleDriveAndDropbox(JSONArray googleDriveItems, JSONArray dropboxItems){
        JSONObject list_items = new JSONObject();

        for ( Object item: googleDriveItems) {
            JSONObject itemObj = (JSONObject) item;
            if(itemObj.containsKey("title")){
                String itemTitle = (String) itemObj.get("title");
                String mimeType = (String) itemObj.get("mimeType");

                mimeType = (mimeType.indexOf("folder") > -1)? "folder" : "file";
                Boolean still_trying_to_add = true;
                int original =0;
                while(still_trying_to_add){
                    String itemKey= mimeType + "_" + itemTitle;
                    if(list_items.containsKey(itemKey)){
                        JSONObject existedItem = (JSONObject) list_items.get(itemKey);
                        // already have an item with the same name in list
                        if(mimeType.equals("folder")){
                            //if it's an folder
                            // just add id in list google drive id, and stop trying to add in list
                            JSONArray driveIds = new JSONArray();
                            if(existedItem.containsKey("googleDriveIds")){
                                driveIds = (JSONArray) existedItem.get("googleDriveIds");
                            }
                            driveIds.add(itemObj.get("id"));
                            existedItem.put("googleDriveIds", driveIds);
                            still_trying_to_add = false;
                        } else if (existedItem.containsKey("size") && itemObj.containsKey("fileSize")){
                            //if it's a file
                            //compare the size of the file
                            //if match, suppose that is the same file, add the id in list ids, and stop trying to add in list
                            //of course it might not be, but for now we could not check that
                            long fileSize = Long.valueOf((String) itemObj.get("fileSize"));
                            long existedItemFileSize = (long) existedItem.get("size");
                            if(fileSize == existedItemFileSize){
                                JSONArray driveIds = new JSONArray();
                                if(existedItem.containsKey("googleDriveIds")){
                                    driveIds = (JSONArray) existedItem.get("googleDriveIds");
                                }
                                driveIds.add(itemObj.get("id"));
                                existedItem.put("googleDriveIds", driveIds);
                                still_trying_to_add = false;
                            } else{
                                if(original == 0){
                                    original++;
                                    itemTitle += "_" + original;
                                } else{
                                    if(original<10){
                                        itemTitle = itemTitle.substring(itemTitle.length()-1);
                                    } else{
                                        itemTitle = itemTitle.substring(itemTitle.length()-2);
                                    }
                                    original++;
                                    itemTitle += "_" + original;
                                }
                            }

                        } else{
                            //if not folder with same name, not same file either
                            //change the title of the file then try again
                            if(original == 0){
                                original++;
                                itemTitle += "_" + original;
                            } else{
                                if(original<10){
                                    itemTitle = itemTitle.substring(itemTitle.length()-1);
                                } else{
                                    itemTitle = itemTitle.substring(itemTitle.length()-2);
                                }
                                original++;
                                itemTitle += "_" + original;
                            }
                        }
                    } else{
                        // don't have any other file or folder with that same name in there
                        // add the file in list item
                        //stop the loop
                        JSONObject new_item_with_format = new JSONObject();
                        new_item_with_format.put("title", itemObj.get("title"));
                        if(itemObj.containsKey("fileSize")){
                            long size = Long.valueOf((String) itemObj.get("fileSize"));
                            new_item_with_format.put("size", size);
                        }

                        JSONArray driveIds = new JSONArray();
                        driveIds.add(itemObj.get("id"));
                        new_item_with_format.put("googleDriveIds", driveIds);

                        new_item_with_format.put("mimeType", mimeType);
                        if(itemObj.containsKey("iconLink")){
                            new_item_with_format.put("icon", itemObj.get("iconLink"));
                        }
                        list_items.put(itemKey, new_item_with_format);
                        still_trying_to_add = false;

                    }
                }
            }

        }


        for ( Object item: dropboxItems) {
            JSONObject itemObj = (JSONObject) item;
            if(itemObj.containsKey("name")){
                String itemTitle = (String) itemObj.get("name");
                String mimeType = (String) itemObj.get(".tag");

                mimeType = (mimeType.indexOf("folder") > -1)? "folder" : "file";
                Boolean still_trying_to_add = true;
                int original =0;
                while(still_trying_to_add){
                    String itemKey= mimeType + "_" + itemTitle;
                    if(list_items.containsKey(itemKey)){
                        JSONObject existedItem = (JSONObject) list_items.get(itemKey);
                        // already have an item with the same name in list
                        if(mimeType.equals("folder")){
                            //if it's an folder
                            // just add id in list google drive id, and stop trying to add in list
                            JSONArray dropboxIds = new JSONArray();
                            if(existedItem.containsKey("dropboxIds")){
                                dropboxIds = (JSONArray) existedItem.get("dropboxIds");
                            }
                            dropboxIds.add(itemObj.get("id"));
                            existedItem.put("dropboxIds", dropboxIds);

                            JSONArray dropboxPaths = new JSONArray();
                            if(existedItem.containsKey("dropboxPaths")){
                                dropboxPaths = (JSONArray) existedItem.get("dropboxPaths");
                            }
                            dropboxPaths.add(itemObj.get("path_display"));
                            existedItem.put("dropboxPaths", dropboxPaths);
                            still_trying_to_add = false;
                        } else if (existedItem.containsKey("size") && itemObj.containsKey("size")){
                            //if it's a file
                            //compare the size of the file
                            //if match, suppose that is the same file, add the id in list ids, and stop trying to add in list
                            //of course it might not be, but for now we could not check that

                            long fileSize = (long) itemObj.get("size");
                            long existedItemFileSize = (long) existedItem.get("size");
                            if(fileSize == existedItemFileSize){
                                JSONArray dropboxIds = new JSONArray();
                                if(existedItem.containsKey("dropboxIds")){
                                    dropboxIds = (JSONArray) existedItem.get("dropboxIds");
                                }
                                dropboxIds.add(itemObj.get("id"));
                                existedItem.put("dropboxIds", dropboxIds);

                                JSONArray dropboxPaths = new JSONArray();
                                if(existedItem.containsKey("dropboxPaths")){
                                    dropboxPaths = (JSONArray) existedItem.get("dropboxPaths");
                                }
                                dropboxPaths.add(itemObj.get("path_display"));
                                existedItem.put("dropboxPaths", dropboxPaths);
                                still_trying_to_add = false;
                            } else{
                                //if not folder with same name, not same file either
                                //change the title of the file then try again
                                if(original == 0){
                                    original++;
                                    itemTitle += "_" + original;
                                } else{
                                    if(original<10){
                                        itemTitle = itemTitle.substring(itemTitle.length()-1);
                                    } else{
                                        itemTitle = itemTitle.substring(itemTitle.length()-2);
                                    }
                                    original++;
                                    itemTitle += "_" + original;
                                }
                            }

                        } else{
                            //if not folder with same name, not same file either
                            //change the title of the file then try again
                            if(original == 0){
                                original++;
                                itemTitle += "_" + original;
                            } else{
                                if(original<10){
                                    itemTitle = itemTitle.substring(itemTitle.length()-1);
                                } else{
                                    itemTitle = itemTitle.substring(itemTitle.length()-2);
                                }
                                original++;
                                itemTitle += "_" + original;
                            }
                        }
                    } else{
                        // don't have any other file or folder with that same name in there
                        // add the file in list item
                        //stop the loop
                        JSONObject new_item_with_format = new JSONObject();
                        new_item_with_format.put("title", itemObj.get("name"));
                        if(itemObj.containsKey("size")){
                            long size = (long) itemObj.get("size");
                            new_item_with_format.put("size", size);
                        }

                        JSONArray dropboxIds = new JSONArray();
                        dropboxIds.add(itemObj.get("path_display"));
                        new_item_with_format.put("dropboxIds", dropboxIds);

                        JSONArray dropboxPaths = new JSONArray();
                        dropboxPaths.add(itemObj.get("path_display"));
                        new_item_with_format.put("dropboxPaths", dropboxPaths);

                        new_item_with_format.put("mimeType", mimeType);
                        list_items.put(itemKey, new_item_with_format);
                        still_trying_to_add = false;

                    }
                }
            }

        }
        return list_items;
    }
}
