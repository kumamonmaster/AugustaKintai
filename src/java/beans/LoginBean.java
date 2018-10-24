/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import database.DBController;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import data.UserData;
import database.UserTableController;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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
    
    private UserTableController userTC = null;

    private static final Logger LOG = Log.getLog();

    
    public LoginBean() {
        
        userTC = new UserTableController();
    }
    
    public void setUserData(UserData userData) {
        
        this.userData = userData;
    }

    public String login() throws SQLException, NamingException {
        
        Connection connection = null;
        String nextPage = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            if (userTC.selectOnly(connection, this.userData))
                return "kintai.xhtml?faces-redirect=true";
            
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw new SQLException();
        } finally {
            try {
            if (connection != null)
                    connection.close();
                connection = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Connectionクローズ失敗", ex);
                ex.printStackTrace();
            }
        }

        return null;
    }
}
