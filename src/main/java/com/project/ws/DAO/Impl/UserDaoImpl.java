package com.project.ws.DAO.Impl;

import com.mysql.jdbc.PreparedStatement;
import com.project.ws.DAO.UserDAO;
import com.project.ws.Model.ErrorMessage;
import com.project.ws.Model.User;
import com.project.ws.MyRESTApplication;
import com.project.ws.common.Utils;
import com.project.ws.services.DatabaseConnection;

import javax.sql.DataSource;
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
    public Object createNewUser(User user) throws SQLException {
        System.out.println("create new user");
        if(user.getUsername()==null){
            return new ErrorMessage(400, "Missing username");
        }
        if(user.getPassword()==null){
            return new ErrorMessage(400, "Missing password");
        }
//        return user;
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
                return new ErrorMessage(400, "Username already exist");
            } else {

                query = "INSERT  INTO  USER (user_name, password, token)  VALUES  (?,?,?)";

                pstmt = (PreparedStatement) conn.prepareStatement(query);
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getToken());
                pstmt.executeUpdate();
                return user;

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
                return new ErrorMessage(400, "Username already exist");
            } else{
                listUser.put(token, user);
                return user;
            }
        }

    }

    @Override
    public Object signin(User user) throws SQLException {
        System.out.println("signin");
        if(user.getUsername()==null){
            return new ErrorMessage(400, "Missing username");
        }
        if(user.getPassword()==null){
            return new ErrorMessage(400, "Missing password");
        }

        if(conn!=null){
            //can create an connect to the mysql database
            //use database
            String query = "Select * from USER where USER_NAME = ?";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getUsername());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()){
//            return (resultSet.getInt(1) > 0);
                System.out.println(resultSet.getString("password"));
                System.out.println(user.getPassword());
                if(!resultSet.getString("password").equals(user.getPassword())){
                    return new ErrorMessage(401, "Password incorrect");
                }

                return new User(user.getUsername(),
                        user.getPassword(),
                        resultSet.getString("token"));
            }
            else{
                return new ErrorMessage(401, "Username not existed");
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
                return new ErrorMessage(401, "Username not existed");
            } else{
                if(existedUser!=null && existedUser.getPassword().equals(user.getPassword())){
                    return existedUser;
                }
                return new ErrorMessage(401, "Password incorrect");
            }
        }

    }

    @Override
    public Object getUserByToken(User user) throws SQLException {
        System.out.println("get user by token");
        if(user.getToken()==null){
            return new ErrorMessage(400, "Missing authorization");
        }
        if(conn!=null){
            //can create an connect to the mysql database
            //use database
            String query = "Select * from USER where token = ?";
            PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getToken());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()){
//            return (resultSet.getInt(1) > 0);
                return new User(resultSet.getString("user_name"),
                        resultSet.getString("password"),
                        resultSet.getString("token"));
            }
            else{
                return new ErrorMessage(401, "No authorized");
            }
        } else{
            //use the fake database
            if(listUser==null){
                createFakeDatabase();
            }
            User existedUser= listUser.get(user.getToken());

            if(existedUser==null){
                return new ErrorMessage(401, "No authorized");
            } else{
                return existedUser;
            }
        }

    }


}
