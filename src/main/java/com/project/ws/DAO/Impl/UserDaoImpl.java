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

public class UserDaoImpl implements UserDAO {

    public UserDaoImpl() {
    }


    @Override
    public Object createNewUser(User user) throws SQLException {
        System.out.println("create new user");
        if(user.getUsername()==null){
            return new ErrorMessage(400, "Thiếu tên đăng nhập");
        }
        if(user.getPassword()==null){
            return new ErrorMessage(400, "Thiếu mật khẩu");
        }
//        return user;
        Connection conn = DatabaseConnection.getInstance().getConn();
        String query = "Select * from USER where USER_NAME = ?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
        pstmt.setString(1, user.getUsername());
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()){
            return new ErrorMessage(400, "Tên đăng nhập đã tồn tại");
        } else {

            query = "INSERT  INTO  USER (user_name, password, token)  VALUES  (?,?,?)";

            pstmt = (PreparedStatement) conn.prepareStatement(query);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            String token;
            try {
                token = Utils.sha256(user.getUsername());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                token = user.getUsername();
            }
            user.setToken(token);
            pstmt.setString(3, user.getToken());
            pstmt.executeUpdate();
            return user;

        }
    }

    @Override
    public Object signin(User user) throws SQLException {
        System.out.println("signin");
        if(user.getUsername()==null){
            return new ErrorMessage(400, "Thiếu tên đăng nhập");
        }
        if(user.getPassword()==null){
            return new ErrorMessage(400, "Thiếu mật khẩu");
        }

        Connection conn = DatabaseConnection.getInstance().getConn();
        String query = "Select * from USER where USER_NAME = ?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
        pstmt.setString(1, user.getUsername());
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()){
//            return (resultSet.getInt(1) > 0);
            System.out.println(resultSet.getString("password"));
            System.out.println(user.getPassword());
            if(!resultSet.getString("password").equals(user.getPassword())){
                return new ErrorMessage(401, "Mật khẩu không chính xác");
            }

            return new User(user.getUsername(),
                    user.getPassword(),
                    resultSet.getString("google_drive_email_address"),
                    resultSet.getString("drop_box_email_address"),
                    resultSet.getString("token"));
        }
        else{
            return new ErrorMessage(401, "Tên đăng nhập không tồn tại");
        }
    }

    @Override
    public Object getUserByToken(User user) throws SQLException {
        System.out.println("get user by token");
        if(user.getToken()==null){
            return new ErrorMessage(400, "Missing authorization");
        }
        Connection conn = DatabaseConnection.getInstance().getConn();
        String query = "Select * from USER where token = ?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
        pstmt.setString(1, user.getToken());
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()){
//            return (resultSet.getInt(1) > 0);
            return new User(resultSet.getString("user_name"),
                    resultSet.getString("password"),
                    resultSet.getString("google_drive_email_address"),
                    resultSet.getString("drop_box_email_address"),
                    resultSet.getString("token"));
        }
        else{
            return new ErrorMessage(401, "No authorized");
        }
    }


    public int GetUserID(String username) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConn();
        System.out.println("GetUserID Username " + username);
        String query = "Select * from USER where NAME = ?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            System.out.println("ID2 " + resultSet.getInt("ID"));
            return (resultSet.getInt("ID"));
        } else
            return -1;
    }

}
