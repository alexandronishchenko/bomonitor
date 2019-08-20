package ru.x5.bomonitor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Простое подключение к БД.
 */
public class DBConnection {
    //Creditionals
    private static final String DB_URL="localhost";
    private static final String DB_USER="gkretail";
    private static final String DB_PASSWORD="gkretail";
    private static final String DB_NAME=bomonitor.properties.getProperty("db_name");
    private static final String DB_ADDITIONALS="";

    private static Connection connection;

    /**
     * Конструктор по умолчанию в привате для реализации паттерна синглтон.
     */
    private DBConnection() {
        //Connection= DriverManager.getConnection();
    }

    /**
     * Метод получения коннекта к БД.
     * @return Единственное подключение к БД.
     * @throws SQLException
     */
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

    /**
     * Выполняет простую выборку без переменных и возвращает мап.
     * @param s запрос полный
     * @return
     * @throws SQLException
     */
    public static HashMap<String,String> executeSelect(String s) throws SQLException {
        //String queryAndParams = s.split("")
        HashMap<String,String> map = new HashMap<>();
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        st.executeQuery(s);
        //System.out.println(st.toString());

        ResultSet result = st.getResultSet();
        ResultSetMetaData rsmd = result.getMetaData();
        int i =1;
        //System.out.println(map.size());

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

    /**
     * Позволяет выполнить выборку с параметрами из массива. Здесь даты.
     * @param s запрос с ?
     * @param requiredColumn имя колонки для проверки на налл
     * @param dates массив дат в формате строки SQL.
     * @return Возвращает мап.
     * @throws SQLException
     */
    public static HashMap<String,String> executeSelect(String s,String requiredColumn,String[] dates) throws SQLException {
        //String queryAndParams = s.split("")
        HashMap<String,String> map = new HashMap<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement st = con.prepareStatement(s);
        //st.setString(1,requiredColumn);
        //System.out.println(dates.length);
        if(dates.length>0) {
            for (int i = 0; i < dates.length; i++) {
                st.setString(i + 1, dates[i]);
            }
        }
       //System.out.println(st.toString());
        st.executeQuery();
        ResultSet result = st.getResultSet();
        ResultSetMetaData rsmd = result.getMetaData();
        int i =1;

        if(result.next()){
            //result.previous();
            //System.out.println(rsmd.getColumnName(i)+"->"+result.getString(i));
            map.put(rsmd.getColumnName(i),result.getString(i));
            i++;
            while(result.next()){
                //System.out.println(rsmd.getColumnName(i)+"->"+result.getString(i));
                map.put(rsmd.getColumnName(i),result.getString(i));
                i++;
            }
        }else {
            map.put(requiredColumn,"");
        }

        i=0;
        result.close();
        st.close();
        con.close();

        return map;
    }

    /**
     * Возвращает набор строк, а не одну в одной записи мап, чем иммитирует функцию arr_agg
     * @param s
     * @return
     * @throws SQLException
     */
    public static HashMap<String,String> getNote(String s) throws SQLException {
        //String queryAndParams = s.split("")
        HashMap<String,String> map = new HashMap<>();
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        st.executeQuery(s);


        ResultSet result = st.getResultSet();
        ResultSetMetaData rsmd = result.getMetaData();
        int i =1;
        //map.put(rsmd.getColumnName(i),null);
        while(result.next()){
            map.put(rsmd.getColumnName(i),map.get(rsmd.getColumnName(i))+";"+result.getString(i));
            i++;
        }
        i=0;
        result.close();
        st.close();
        con.close();
        return map;
    }
}
