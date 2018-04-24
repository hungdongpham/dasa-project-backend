package com.project.ws.services;

import com.project.ws.Model.ResponseMessage;
import com.project.ws.apiServices.dropbox;
import com.project.ws.apiServices.googleDrive;
import com.project.ws.common.Utils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

@Path("/file")
public class fileService {

    @GET
    @Path("/all")
    @Produces("application/json")
    public Response getAllUserFiles() {


        googleDrive.getInstance().RefreshToken();
        ResponseMessage listFilesGoogleDriveResponse = googleDrive.getInstance().GetListFile(null);
        ResponseMessage lisFilesDropboxResponse = dropbox.getInstance().getListFile();
        int status = (listFilesGoogleDriveResponse.getStatus()< 400 || lisFilesDropboxResponse.getStatus() < 400 )?
                200 : listFilesGoogleDriveResponse.getStatus();

        JSONObject response = new JSONObject();
        //response.put("")
        return Response.status(status).entity(listFilesGoogleDriveResponse.getResponse()).build();

//        String result = "Product created : " + product;
//        return Response.status(201).entity(result).build();

    }

    @GET
    @Path("/{fileid}")
    @Produces("application/json")
    public Response getFileInfo(@PathParam("fileid") String id) {
        googleDrive.getInstance().RefreshToken();
        ResponseMessage fileInfoGoogleDriveResponse = googleDrive.getInstance().getFileInfo(id);
        return Response.status(fileInfoGoogleDriveResponse.getStatus()).entity(fileInfoGoogleDriveResponse.getResponse()).build();


    }

    @GET
    @Path("/{fileid}/children")
    @Produces("application/json")
    public Response getFileChildren(@PathParam("fileid") String id) {
        googleDrive.getInstance().RefreshToken();
        ResponseMessage listFilesGoogleDriveResponse = googleDrive.getInstance().getFileChildren(id);
        return Response.status(listFilesGoogleDriveResponse.getStatus()).entity(listFilesGoogleDriveResponse.getResponse()).build();
    }

    @GET
    @Path("/{fileid}/delete")
    @Produces("application/json")
    public Response deleteFile(@PathParam("fileid") String id) {
        googleDrive.getInstance().RefreshToken();
        ResponseMessage response = googleDrive.getInstance().deleteFile(id);
        return Response.status(response.getStatus()).entity(response.getResponse()).build();

    }


    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response uploadFile(MultipartFormDataInput input) {

        String rootPath = System.getProperty("user.home");
        File dir = new File(rootPath + File.separator + "tmpFiles");
        if (!dir.exists())
            dir.mkdirs();

        String fileName="";
        String folderId;
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        try {
            folderId = input.getFormDataPart("folderId", String.class, null);

        } catch (IOException e) {
            e.printStackTrace();
            folderId =null;
        }
        List<InputPart> inputParts = uploadForm.get("file");
        for (InputPart inputPart : inputParts) {
            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = Utils.getFileName(header);
                System.out.println(fileName);
                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);

                byte [] bytes = IOUtils.toByteArray(inputStream);

                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + fileName);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));

                System.out.println("Server File Location =" + serverFile.getAbsolutePath());
                stream.write(bytes);
                stream.close();

                googleDrive.getInstance().RefreshToken();
                ResponseMessage response;
                if(folderId == null) {
                    response = googleDrive.getInstance().uploadFile(serverFile.getAbsolutePath(), null);
                } else{
                    response = googleDrive.getInstance().uploadFile(serverFile.getAbsolutePath(), folderId);
                }
                //constructs upload file path
                serverFile.delete();
                System.out.println("Done");
                return Response.status(response.getStatus()).entity(response.getResponse()).build();



            } catch (IOException e) {
                e.printStackTrace();
                JSONObject serverErr = new JSONObject();
                serverErr.put("message", "Internal server error");
                return Response.status(502).entity(serverErr).build();
            }

        }


        String result = "Product created : " ;
//        googleDrive.getInstance().GuploadFile(null);
//        System.out.println(filename);
        return Response.status(201).entity(result).build();

    }

    @POST
    @Path("/createFolder")
    @Produces("application/json")
    public Response createFolder(String json) {
        System.out.println(json);

        org.json.simple.parser.JSONParser parser  = new org.json.simple.parser.JSONParser();
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String folderName = (String) jsonObject.get("folderName");
            System.out.println("Create folder: " + folderName);
            googleDrive.getInstance().RefreshToken();

            ResponseMessage response = googleDrive.getInstance().createFolder(null, folderName);
        return Response.status(response.getStatus()).entity(response.getResponse()).build();
        } catch(org.json.simple.parser.ParseException e){
            JSONObject parseError = new JSONObject();
            parseError.put("message","Malformed parameter");
            return Response.status(400).entity(parseError).build();
        }

    }

    @POST
    @Path("/createFolder/{folderid}")
    @Produces("application/json")
    public Response createFolderInFolder(String json, @PathParam("folderid") String folder_id) {
        System.out.println(json);

        org.json.simple.parser.JSONParser parser  = new org.json.simple.parser.JSONParser();
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String folderName = (String) jsonObject.get("folderName");
            System.out.println("Create folder: " + folderName);
            googleDrive.getInstance().RefreshToken();

            ResponseMessage response = googleDrive.getInstance().createFolder(folder_id, folderName);
            return Response.status(response.getStatus()).entity(response.getResponse()).build();
        } catch(org.json.simple.parser.ParseException e){
            JSONObject parseError = new JSONObject();
            parseError.put("message","Malformed parameter");
            return Response.status(400).entity(parseError).build();
        }

    }
}
