package com.project.ws.services;

import com.project.ws.DAO.Impl.UserDaoImpl;
import com.project.ws.Model.ResponseMessage;
import com.project.ws.Model.User;
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
        JSONObject errorMessage = new JSONObject();
        if(httpHeaders.getRequestHeader("dasa-token").isEmpty()){

            errorMessage.put("message", "Missing authorization");
            return Response.status(401).entity(errorMessage).build();
        }
        String token = httpHeaders.getRequestHeader("dasa-token").get(0);
        System.out.println(token);
        User user = new User();
        user.setToken(token);
        if(userDao==null){
            userDao = new UserDaoImpl();
        }

        try {
            ResponseMessage response = userDao.getUserByToken(user);
            return  Response.status(response.getStatus()).entity(response.getResponse()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage.put("message", "SQL query error");
            return Response.status(502).entity(errorMessage).build();
        }

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
            ResponseMessage response = userDao.createNewUser(user);
            return  Response.status(response.getStatus()).entity(response.getResponse()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject errorMessage = new JSONObject();
            errorMessage.put("message", "SQL query error");
            return Response.status(502).entity(errorMessage).build();
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
            ResponseMessage response = userDao.signin(user);
            return  Response.status(response.getStatus()).entity(response.getResponse()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject errorMessage = new JSONObject();
            errorMessage.put("message", "SQL query error");
            return Response.status(502).entity(errorMessage).build();
        }
//        String result = "User created : " + user;
//        return user;


    }


}

