/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import data.KbnData;
import data.KintaiData;
import data.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
public class KintaiBeanDataAccess {
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    public void getAttendanceData(Connection connection, int yearMonth, UserData userData, ArrayList<KintaiData> dataList) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // attendanceテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM attendance WHERE ym = ? AND user_id = ?");
            stmt.setInt(1, yearMonth);
            stmt.setString(2, userData.getId());
            rs = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            while (rs.next()) {
                
                dataList.get(rs.getInt("day")-1).setData(
                                rs.getTime("start_time"), rs.getTime("end_time"), 
                                rs.getTime("rest_time"), rs.getTime("total_time"), rs.getTime("over_time"), 
                                rs.getTime("real_time"), rs.getInt("kbn_cd"), "",
                                rs.getInt("workptn_cd"), rs.getTime("late_time"), rs.getTime("leave_time"),
                                rs.getString("remarks"));
                
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
    
    public void getWorkPatternData(Connection connection, int workPtn_cd, Time start, Time end) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM work_pattern WHERE ptn_cd = ?");
            stmt.setInt(1, workPtn_cd);
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            if( rs.next() ) {
                
                start = rs.getTime("start_time");
                end = rs.getTime("end_time");
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
    
    public double getYukyuMonthData(Connection connection, int ym, UserData userData, KbnData kbnData) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double yukyuDays = 0;
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT kbn_cd FROM attendance WHERE ym = ? AND user_id = ? AND (kbn_cd = (SELECT kbn_cd FROM kbn WHERE name = \"有休\") OR kbn_cd = (SELECT kbn_cd FROM kbn WHERE name = \"午前有休\") OR kbn_cd = (SELECT kbn_cd FROM kbn WHERE name = \"午後有休\"))");
            stmt.setInt(1, ym);
            stmt.setString(2, userData.getId());
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            while( rs.next() ) {
                
                if (kbnData.getKbnList().get(rs.getInt("kbn_cd")).equals("有休"))
                    yukyuDays += 1;
                else if (kbnData.getKbnList().get(rs.getInt("kbn_cd")).equals("午前有休") || kbnData.getKbnList().get(rs.getInt("kbn_cd")).equals("午後有休"))
                    yukyuDays += 0.5;
            }
            
            return yukyuDays;
        
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
    
    public double getYukyuRemainingData(Connection connection, UserData userData) throws SQLException {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT remaining_day FROM yukyu WHERE user_id = ? ");
            stmt.setString(1, userData.getId());
            rs = stmt.executeQuery();

            // パスワード一致していたらページ遷移
            if( rs.next() ) {
                
                return rs.getDouble("remaining_day");
            } else {
                return 0.0;
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
}
