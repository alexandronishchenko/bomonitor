package ru.x5.bomonitor.database;

import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.database.Entity.ItemPrice;

import java.io.File;
import java.sql.*;

public class FirebirdConnection {
    private String posName;
    //Creditionals
    private String DB_URL;
    private static final String DB_USER= bomonitor.properties.getProperty("fb_user");
    private static final String DB_PASSWORD=bomonitor.properties.getProperty("fb_password");
    private String DB_PATH;

    public FirebirdConnection(String posName){
        this.posName=posName;
        DB_URL=posName;
        DB_PATH= bomonitor.properties.getProperty("fb_path")+"standard_stamm.";

    }
    public FirebirdConnection(String posName, String path){
        this.posName=posName;
        DB_URL=posName;
        DB_PATH=path;
    }
    public Connection getConnection()  {
        Connection con=null;
        int dbIterator=0;
        boolean success=false;
        while(dbIterator<10 && !success){
            try{
                String connection="jdbc:firebirdsql://"+DB_URL+"/3052:/"+DB_PATH+dbIterator+".gdb";
                //connection="jdbc:firebirdsql://"+"192.168.224.49"+"/3052:/"+DB_PATH+dbIterator+".gdb?charSet=utf-8";
                con = DriverManager.getConnection(connection,DB_USER,DB_PASSWORD);
                System.out.println("connecting to -> "+ connection);
                success=true;
                ++dbIterator;
                return con;
            }catch (SQLException s){
                ++dbIterator;
                s.printStackTrace();
            }
        }
        return con;
    }

    public Table executeTableSelectPrices(String sql) throws SQLException {
        Table<ItemPrice> table= new Table<>();
        Connection connection = getConnection();
        Statement st = connection.createStatement();
        ResultSet result=st.executeQuery(sql);
        //ResultSet result1 = st.getResultSet();
        ResultSetMetaData rsmd = result.getMetaData();
        while (result.next()){
            String subres = result.getString(1);
            if(!subres.equals("null") && !subres.equals("")){
                try {
                    table.put(
                            new ItemPrice(
                                    Integer.parseInt(result.getString(1)),
                                    Integer.parseInt(result.getString(2)),
                                    Double.parseDouble(result.getString(3))));
                }catch (NumberFormatException e){
                    System.out.println("failed");
                }
            }
        }
        return table;
    }
}
