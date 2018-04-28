package com.project.ws.DAO;


import com.project.ws.Model.ResponseMessage;
import com.project.ws.Model.User;

import java.sql.SQLException;

public interface UserDAO {

    ResponseMessage createNewUser(User user) throws SQLException;

    ResponseMessage signin(User user) throws SQLException;

    ResponseMessage getUserByToken(User user) throws SQLException;
}
