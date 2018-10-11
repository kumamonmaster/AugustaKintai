package database;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 * 
 * データベースへのアクセスを管理
 */
public class DBController {
    
    private Connection connection = null;
    
    private String url = "jdbc:mysql://localhost:3306/mynumberdb";
    private String user = "TakafumiSato";
    private String password = "1234567";
    
    Log log = new Log(DBController.class.getName(),"test.log");
    
    
    /*
    データベースをオープン
    */
    public Connection open() throws SQLException, NamingException {
        
        String jndi = "java:comp/env/jdbc/MySQL";
        
        InitialContext context = null;
        
        try {
            
                context = new InitialContext();
                // lookupでデータソースオブジェクトを取得
                DataSource dataSource = (DataSource) context.lookup(jndi);

                // データソースでコネクション取得
                connection = dataSource.getConnection();
        } catch (NamingException ex) {
            
            ex.printStackTrace();
            log.log(Level.SEVERE, "Naming例外です", ex);
            throw new NamingException();
        } catch (SQLException ex) {
            
            close();
            ex.printStackTrace();
            log.log(Level.SEVERE, "SQL例外です", ex);
            throw new SQLException();
        }
        
        return connection;
    }
    
    /*
    データベースをクローズ
    */
    public void close() {
        
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }
}
