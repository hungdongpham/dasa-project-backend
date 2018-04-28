package com.project.ws.DAO.Impl;

import com.mysql.jdbc.PreparedStatement;
import com.project.ws.DAO.UserDAO;
import com.project.ws.Model.ResponseMessage;
import com.project.ws.Model.User;
import com.project.ws.common.Utils;
import com.project.ws.services.DatabaseConnection;
import org.json.simple.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDaoImpl implements UserDAO {

    private HashMap<String, User> listUser;
    Connection conn;
    public UserDaoImpl() {
        conn = DatabaseConnection.getInstance().getConn();
        createFakeDatabase();
    }

    private void createFakeDatabase(){
        if(listUser==null){
            listUser = new HashMap<String, User>();

            //create user tam
            User tam = new User();
            tam.setUsername("thanhtam");
            tam.setPassword("aaaaaa");
            String token;
            try {
                token = Utils.sha256(tam.getUsername());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                token = tam.getUsername();
            }
            tam.setToken(token);
            listUser.put(token, tam);

            //create user hung
            User hung = new User();
            hung.setUsername("hungpham");
            hung.setPassword("aaaaaa");
            try {
                token = Utils.sha256(hung.getUsername());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                token = hung.getUsername();
            }
            hung.setToken(token);
            listUser.put(token, hung);


        }
    }
    @Override
    public ResponseMessage createNewUser(User user) throws SQLException {
        System.out.println("create new user");
        JSONObject errorMessage = new JSONObject();
        if(user.getUsername()==null){
            errorMessage.put("message", "Missing username");
            return new ResponseMessage(400, errorMessage);
        }
        if(user.getPassword()==null){
            errorMessage.put("message", "Missing password");
            return new ResponseMessage(400, errorMessage);
        }
        String token;
        try {
            token = Utils.sha256(user.getUsername());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            token = user.getUsername();
        }
        user.setToken(token);

        if(conn!=null){
            //can create an connect to the mysql database
            //use database
            String query = "Select * from USER where USER_NAME = ?";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getUsername());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()){
                errorMessage.put("message", "Username already exist");
                return new ResponseMessage(400, errorMessage);
            } else {

                query = "INSERT  INTO  USER (user_name, password, token)  VALUES  (?,?,?)";

                pstmt = (PreparedStatement) conn.prepareStatement(query);
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getToken());
                pstmt.executeUpdate();
                return new ResponseMessage(200, user.convertUserToJSONResponse());

            }
        } else{
            //use the fake database
            if(listUser==null) {
                System.out.println("create fake databse");
                createFakeDatabase();
            }
            Boolean usernameExisted = false;
            for (Map.Entry<String, User> entry : listUser.entrySet()) {
                User existedUser = entry.getValue();
                if(existedUser.getUsername().equals(user.getUsername())){
                    usernameExisted=true;
                }
            }
            if(usernameExisted){
                errorMessage.put("message", "Username already exist");
                return new ResponseMessage(400, errorMessage);
            } else{
                listUser.put(token, user);
                return new ResponseMessage(200, user.convertUserToJSONResponse());
            }
        }

    }

    @Override
    public ResponseMessage signin(User user) throws SQLException {

        System.out.println("signin");
        JSONObject errorMessage = new JSONObject();
        if(user.getUsername()==null){
            errorMessage.put("message", "Missing username");
            return new ResponseMessage(400, errorMessage);
        }
        if(user.getPassword()==null){
            errorMessage.put("message", "Missing password");
            return new ResponseMessage(400, errorMessage);
        }

        if(conn!=null){
            //can create an connect to the mysql database
            //use database
            String query = "Select * from USER where USER_NAME = ?";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getUsername());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()){
                System.out.println(resultSet.getString("password"));
                System.out.println(user.getPassword());
                if(!resultSet.getString("password").equals(user.getPassword())){
                    errorMessage.put("message", "Password incorrect");
                    return new ResponseMessage(401, errorMessage);
                }

                user.setToken(resultSet.getString("token"));

                return new ResponseMessage(200, user.convertUserToJSONResponse());
            }
            else{
                errorMessage.put("message", "Username not existed");
                return new ResponseMessage(401, errorMessage);
            }
        } else{
            //use the fake database
            if(listUser==null){
                createFakeDatabase();
            }
            Boolean usernameExisted = false;
            User existedUser=null;
            for (Map.Entry<String, User> entry : listUser.entrySet()) {
                existedUser = entry.getValue();
                if(existedUser.getUsername().equals(user.getUsername())){
                    usernameExisted=true;
                    break;
                }
            }
            if(!usernameExisted){
                errorMessage.put("message", "Username not existed");
                return new ResponseMessage(401, errorMessage);
            } else{
                if(existedUser!=null && existedUser.getPassword().equals(user.getPassword())){
                    return new ResponseMessage(200, existedUser.convertUserToJSONResponse());
                }
                errorMessage.put("message", "Password incorrect");
                return new ResponseMessage(401, errorMessage);
            }
        }

    }

    @Override
    public ResponseMessage getUserByToken(User user) throws SQLException {
        System.out.println("get user by token");
        JSONObject errorMessage = new JSONObject();
        if(user.getToken()==null){
            errorMessage.put("message", "Missing authorization");
            return new ResponseMessage(400, errorMessage);
        }
        if(conn!=null){
            //can create an connect to the mysql database
            //use database
            String query = "Select * from USER where token = ?";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getToken());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()){
                JSONObject userObj = new JSONObject();
                user.setUsername(resultSet.getString("user_name"));
                user.setToken(resultSet.getString("token"));
                return new ResponseMessage(200, user.convertUserToJSONResponse());
            }
            else{
                errorMessage.put("message", "No authorized");
                return new ResponseMessage(401, errorMessage);
            }
        } else{
            //use the fake database
            if(listUser==null){
                createFakeDatabase();
            }
            User existedUser= listUser.get(user.getToken());

            if(existedUser==null){
                errorMessage.put("message","No authorized");
                return new ResponseMessage(401, errorMessage);
            } else{
                return new ResponseMessage(200, existedUser.convertUserToJSONResponse());
            }
        }

    }


}
