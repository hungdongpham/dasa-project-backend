package com.project.ws.DAO;


import com.project.ws.Model.User;

import java.sql.SQLException;

public interface UserDAO {

    public Object createNewUser(User user) throws SQLException;

    public Object signin(User user) throws SQLException;

    public Object getUserByToken(User user) throws SQLException;
}
