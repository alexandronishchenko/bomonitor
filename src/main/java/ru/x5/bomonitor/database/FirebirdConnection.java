package ru.x5.bomonitor.database;

import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.database.Entity.ItemPrice;

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
        DB_PATH= bomonitor.properties.getProperty("fb_path");
    }
    public FirebirdConnection(String posName, String path){
        this.posName=posName;
        DB_URL=posName;
        DB_PATH=path;
    }
    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection("jdbc:firebirdsql:localhost/3052:/home/alexandr/stamm/standard_stamm.2.gdb?charSet=utf-8","SYSDBA","masterkey");
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
