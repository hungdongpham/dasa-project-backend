package com.project.ws.services;

import com.project.ws.DAO.Impl.UserDaoImpl;
import com.project.ws.Model.ErrorMessage;
import com.project.ws.Model.ResponseMessage;
import com.project.ws.Model.User;
import com.project.ws.apiServices.googleDrive;
import com.project.ws.common.Utils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/user")
public class userService {

    private UserDaoImpl userDao;
    private String googleDriveAPIEndPoint = "https://accounts.google.com/o/oauth2/v2/auth";
    @GET
    @Produces("application/json")
    public Response getUserByToken(@Context HttpHeaders httpHeaders) {
        if(httpHeaders.getRequestHeader("dasa-token").isEmpty()){
            return Response.status(401).entity(new ErrorMessage(401, "Missing authorization")).build();
        }
        String token = httpHeaders.getRequestHeader("dasa-token").get(0);
        System.out.println(token);
        User user = new User();
        user.setToken(token);
        if(userDao==null){
            userDao = new UserDaoImpl();
        }

        try {
            Object response = userDao.getUserByToken(user);
            if(response.getClass() == User.class){
                return Response.ok().entity(response).build();
            } else{
                ErrorMessage errorResponse = (ErrorMessage) response;
                return Response.status(errorResponse.getStatus()).entity(errorResponse).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502).entity(new ErrorMessage(502, "SQL query error")).build();
        }
//        InputStream in =
//                userService.class.getResourceAsStream("/client_secret.json");
//        System.out.println(userService.class.getClassLoader().getResourceAsStream("client_secret.json"));
//
//        googleDrive drive = new googleDrive();
//        drive.getAuthorizeInfo();
//        drive.RefreshToken();
//        drive.GetListFile();
//        return user;
//        String result = "Product created : " + product;
//        return Response.status(201).entity(result).build();

    }

    @POST
    @Path("/signup")
    @Consumes("application/json")
    @Produces("application/json")
    public Response signup(User user) {
        if(userDao==null){
            userDao = new UserDaoImpl();
        }
        System.out.println(user);
        try {
            Object response = userDao.createNewUser(user);
            if(response.getClass() == User.class){
                return Response.ok().entity(response).build();
            } else{
                ErrorMessage errorResponse = (ErrorMessage) response;
                return Response.status(errorResponse.getStatus()).entity(errorResponse).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502).entity(new ErrorMessage(502, "SQL query error")).build();
        }
//        String result = "User created : " + user;
//        return user;


    }

    @POST
    @Path("/signin")
    @Consumes("application/json")
    @Produces("application/json")
    public Response signin(User user) {
        if(userDao==null){
            userDao = new UserDaoImpl();
        }
        System.out.println(user.getUsername());
        try {
            Object response = userDao.signin(user);
            if(response.getClass() == User.class){
                return Response.ok().entity(response).build();
            } else{
                ErrorMessage errorResponse = (ErrorMessage) response;
                return Response.status(errorResponse.getStatus()).entity(errorResponse).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502).entity(new ErrorMessage(502, "SQL query error")).build();
        }
//        String result = "User created : " + user;
//        return user;


    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public User createProductInJSON(User user) {

        String result = "User created : " + user;
        return user;
//        return Response.status(201).entity(result).build();

    }

}

