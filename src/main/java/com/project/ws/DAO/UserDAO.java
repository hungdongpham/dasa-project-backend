package com.project.ws.DAO;


import com.project.ws.Model.User;

import java.sql.SQLException;

public interface UserDAO {

    Object createNewUser(User user) throws SQLException;

    Object signin(User user) throws SQLException;

    Object getUserByToken(User user) throws SQLException;
}
