/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
public class UserTableController {
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    public boolean selectOnly(Connection connection, UserData userData) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT id,name,password,workptn_cd FROM user WHERE mail = ?");
            stmt.setString(1, userData.getMail());
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            if( rs.next() ) {
                if (userData.getPassword().equals(rs.getString("password"))) {
                    // ユーザーデータを作成
                    //this.userData.setId(rs.getString("id"));
                    //userData = new UserData(rs.getString("id"),rs.getString("name"),this.address);
                    userData.setId(rs.getString("id"));
                    userData.setName(rs.getString("name"));
                    userData.setWorkptn_cd(rs.getInt("workptn_cd"));

                    return true;
                }
            }
        
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
        }
        
        return false;
    }
}
