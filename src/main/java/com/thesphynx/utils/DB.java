package com.thesphynx.utils;

import java.sql.*;

public class DB {

    final String URL="jdbc:mysql://localhost:3306/eventmanager";

    final String USERNAME="root";
    final String PASSWORD="";
    Connection connection;

    static DB instance;

    private DB(){
        try {
            connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DB getInstance(){
        if (instance==null){
            instance= new DB();
        }
      return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
