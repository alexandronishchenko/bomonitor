package ru.x5.bomonitor.database;

import ru.x5.bomonitor.bomonitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FirebirdConnection {
    private String posName;
    //Creditionals
    private String DB_URL;
    private static final String DB_USER= bomonitor.properties.getProperty("fb_user");
    private static final String DB_PASSWORD=bomonitor.properties.getProperty("fb_password");
    private static final String DB_PATH= bomonitor.properties.getProperty("fb_path");

    public FirebirdConnection(String posName){
        this.posName=posName;
        DB_URL=posName;
    }
    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection("jdbc:firebirdsql://"+DB_URL+":3052/"+DB_PATH,DB_USER,DB_PASSWORD);
    }

    public Table executeTableSelectPrices(String sql){

        return null;
    }
}
