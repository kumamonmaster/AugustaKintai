/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import database.DBController;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import data.UserData;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@RequestScoped
public class LoginBean {

    @ManagedProperty(value="#{userData}")
    private UserData userData;

    private static final Logger LOG = Log.getLog();

    /**
     * Creates a new instance of Login
     */
    
    
    public void setUserData(UserData userData) {
        
        this.userData = userData;
    }

    public String login() throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //log = new Log(LoginBean.class.getName(), "test.log");
        
        try {
            
            // データベース接続
            connection = DBController.open();
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT id,name,password FROM user WHERE mail = ?");
            stmt.setString(1, this.userData.getMail());
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            if( rs.next() ) {
                if (this.userData.getPassword().equals(rs.getString("password"))) {
                    // ユーザーデータを作成
                    //this.userData.setId(rs.getString("id"));
                    //userData = new UserData(rs.getString("id"),rs.getString("name"),this.address);
                    this.userData.setId(rs.getString("id"));
                    this.userData.setName(rs.getString("name"));

                    return "kintai.xhtml";
                }
            }
        
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            throw new SQLException();
        } finally {
            
            // クローズ
            try {
                if (rs != null)
                    rs.close();
                rs = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "ResultSetクローズ失敗", ex);
                ex.printStackTrace();
            }
            
            try {
                if (stmt != null)
                    stmt.close();
                stmt = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Statementクローズ失敗", ex);
                ex.printStackTrace();
            }
            
            try {
                if (connection != null)
                    connection.close();
                connection = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Connectionクローズ失敗", ex);
                ex.printStackTrace();
            }
        }
        
        // 一致しなかったらとりあえずページ遷移させない
        return null;
    }
}
