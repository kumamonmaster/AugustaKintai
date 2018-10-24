/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.KintaiData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
public class KbnTableController {
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    
    public void getTableUseEditInput(Connection connection, ArrayList<SelectItem> kbnTable) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String kbnName = null;
        
        try {
            
            // kbnテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM kbn");
            rs = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            while (rs.next()) {
                
                kbnTable.add(new SelectItem(rs.getInt("kbn_cd"),rs.getString("name")));
            }
        
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
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
    }
    
    public void getTableUseKintai(Connection connection, int kbn_cd, KintaiData kintaiData) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // 区分テーブルからデータ取得
            stmt = connection.prepareStatement("SELECT * FROM kbn WHERE kbn_cd = ?");
            stmt.setInt(1, kbn_cd);
            rs = stmt.executeQuery();

            if (rs.next()) {
                kintaiData.setKbnName(rs.getString("name"));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "区分テーブルでエラーです");
            ex.printStackTrace();
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
    }
}
