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
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
public class WorkPatternTableController {
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    public void getTableUseEdit(Connection connection, int workPtn_cd, KintaiData kintaiData) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM work_pattern WHERE ptn_cd = ?");
            stmt.setInt(1, workPtn_cd);
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            if( rs.next() ) {
//                kintaiData.setStart(rs.getTime("start_time"));
//                kintaiData.setEnd(rs.getTime("end_time"));
                kintaiData.setStart_default(rs.getTime("start_time"));
                kintaiData.setEnd_default(rs.getTime("end_time"));
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
    }
}
