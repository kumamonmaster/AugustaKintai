/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.KintaiData;
import data.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
public class AttendanceTableController {
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    public void selectAll(Connection connection, int nowYearMonth, String user_id, ArrayList<KintaiData> dataList) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rsAttendance = null;
        ResultSet rsKbn = null;
        
        try {
            
            // attendanceテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM attendance WHERE ym = ? AND user_id = ?");
            stmt.setInt(1, nowYearMonth);
            stmt.setString(2, user_id);
            rsAttendance = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            while (rsAttendance.next()) {
                
                // 区分テーブルからデータ取得
                stmt = connection.prepareStatement("SELECT * FROM kbn WHERE kbn_cd = ?");
                stmt.setInt(1, rsAttendance.getInt("kbn_cd"));
                rsKbn = stmt.executeQuery();
                
                rsKbn.next();
                
                dataList.get(rsAttendance.getInt("day")-1).setData(
                                rsAttendance.getTime("start_time"), rsAttendance.getTime("end_time"), 
                                rsAttendance.getTime("rest_time"), rsAttendance.getTime("total_time"), rsAttendance.getTime("over_time"), 
                                rsAttendance.getTime("real_time"), rsAttendance.getInt("kbn_cd"), rsKbn.getString("name") );
                
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
            throw new SQLException();
        } finally {
            
            // クローズ
            try {
                if (rsAttendance != null)
                    rsAttendance.close();
                rsAttendance = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "ResultSetクローズ失敗", ex);
                ex.printStackTrace();
            }
            
            try {
                if (rsKbn != null)
                    rsKbn.close();
                rsKbn = null;
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
    
    
    public void selectOnly(Connection connection, int nowYearMonth, String user_id, int day, KintaiData data) throws SQLException {
    
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM attendance WHERE ym = ? AND user_id = ? AND day = ?");
            stmt.setInt(1, nowYearMonth);
            stmt.setString(2, user_id);
            stmt.setInt(3, day);
            rs = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            if (rs.next()) {
                data.setData(
                                rs.getTime("start_time"), rs.getTime("end_time"), 
                                rs.getTime("rest_time"), rs.getTime("total_time"), rs.getTime("over_time"), 
                                rs.getTime("real_time"), rs.getInt("kbn_cd"), "");
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
    
    public void replaceOnly(Connection connection, KintaiData kintaiData, UserData userData) throws SQLException {
        
        PreparedStatement stmt = null;
        
        try {
            
            // attendanceテーブルに入力データをセット
            stmt = connection.prepareStatement("REPLACE INTO attendance (ym,user_id,day,start_time,end_time,rest_time,total_time,real_time,over_time,kbn_cd) VALUES(?,?,?,?,?,?,?,?,?,?)");
            stmt.setInt(1, kintaiData.getYm());
            stmt.setString(2, userData.getId());
            stmt.setInt(3, kintaiData.getDay());
            stmt.setTime(4, kintaiData.getStart());
            stmt.setTime(5, kintaiData.getEnd());
            stmt.setTime(6, kintaiData.getRest());
            stmt.setTime(7, kintaiData.getTotal());
            stmt.setTime(8, kintaiData.getReal());
            stmt.setTime(9, kintaiData.getOver());
            stmt.setInt(10, kintaiData.getKbnCd());
            
            stmt.executeQuery();
        
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
            throw new SQLException();
        } finally {
            
            // クローズ
            
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
