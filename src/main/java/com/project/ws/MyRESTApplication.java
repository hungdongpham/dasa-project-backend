package com.project.ws;

import com.project.ws.apiServices.googleDrive;
import com.project.ws.common.Utils;
import com.project.ws.services.DatabaseConnection;
import com.project.ws.services.RESTEasyHelloWorldService;
import com.project.ws.services.fileService;
import com.project.ws.services.userService;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
//import javax.ws.rs.Application;

public class MyRESTApplication  extends Application {
    private Set<Object> singletons = new HashSet<Object>();

    public MyRESTApplication() {
        CorsFilter filter = new CorsFilter();
        singletons.add(filter);
        singletons.add(DatabaseConnection.getInstance());
        singletons.add(googleDrive.getInstance());
        singletons.add(new RESTEasyHelloWorldService());
        singletons.add(new userService());
        singletons.add(new fileService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
