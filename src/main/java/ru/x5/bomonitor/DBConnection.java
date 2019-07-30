package ru.x5.bomonitor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBConnection {
    //Creditionals
    private static final String DB_URL="localhost";
    private static final String DB_USER="gkretail";
    private static final String DB_PASSWORD="gkretail";
    private static final String DB_NAME=bomonitor.properties.getProperty("db_name");
    private static final String DB_ADDITIONALS="";

    private static Connection connection;

    private DBConnection() {
        //Connection= DriverManager.getConnection();
    }
    public static Connection getConnection() throws SQLException {
        if(null==connection||connection.isClosed()){
            try {
                connection=DriverManager.getConnection("jdbc:postgresql://"+DB_URL+":5432/"+DB_NAME+DB_ADDITIONALS,DB_USER,DB_PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static HashMap<String,String> executeSelect(String s) throws SQLException {
        //String queryAndParams = s.split("")
        HashMap<String,String> map = new HashMap<>();
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        st.executeQuery(s);


        ResultSet result = st.getResultSet();
        ResultSetMetaData rsmd = result.getMetaData();
        int i =1;
        while(result.next()){
            map.put(rsmd.getColumnName(i),result.getString(i));
            i++;
        }
        i=0;
        result.close();
        st.close();
        con.close();
        return map;
    }
}
