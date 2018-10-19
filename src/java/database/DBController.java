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
    
    private static Connection connection = null;
    private static final Logger LOG = Log.getLog();
    
    //private static Log log = new Log(DBController.class.getName(),"test.log");
    
    
    /*
    データベースをオープン
    */
    public static Connection open() throws SQLException, NamingException {
        
        String jndi = "java:comp/env/jdbc/MySQLAWS";
        //String jndi = "java:comp/env/jdbc/MySQL";
        
        InitialContext context = null;
        
        try {
            
                context = new InitialContext();
                // lookupでデータソースオブジェクトを取得
                DataSource dataSource = (DataSource) context.lookup(jndi);

                // データソースでコネクション取得
                connection = dataSource.getConnection();
                
                //DriverManager;
                
        } catch (NamingException ex) {
            
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            throw new NamingException();
            
        } catch (SQLException ex) {
            
            try {
                if (connection != null)
                    connection.close();
                connection = null;
            } catch(SQLException ex2) {
                ex2.printStackTrace();
                LOG.log(Level.SEVERE, "SQL例外です", ex2);
                throw new SQLException();
            }
            
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            throw new SQLException();
        }
        
        return connection;
    }
}
