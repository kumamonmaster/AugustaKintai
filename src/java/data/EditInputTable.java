/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import database.DBController;
import database.KbnTableController;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@SessionScoped
public class EditInputTable implements Serializable {
    
    private KbnTableController ktc = null;
    
    private ArrayList<SelectItem> timeTable = null;
    private ArrayList<SelectItem> restTable = null;
    private ArrayList<SelectItem> kbnTable = null;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();

    public EditInputTable() {
        
        ktc = new KbnTableController();
        
        // 出退勤タイムテーブルを初期化
        initTimeTable();
        
        // 休憩時間テーブルを初期化
        initRestTable();
        
        try {
            // 区分テーブルを初期化
            initKbnTable();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
    private void initTimeTable() {
        
        timeTable = new ArrayList<SelectItem>();
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        
        Time time = Time.valueOf("00:00:00");
        LocalTime localTime = time.toLocalTime();
        
        for (int i = 0; i < 4*24+1; i++) {
            
            timeTable.add(new SelectItem(localTime.toString()+":00", localTime.toString()));
            
            localTime = localTime.plusMinutes(15);
        }
    }
    
    private void initRestTable() {
        
        restTable = new ArrayList<SelectItem>();
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        
        Time time = Time.valueOf("00:00:00");
        LocalTime localTime = time.toLocalTime();
        
        for (int i = 0; i < 4*3; i++) {
            
            localTime = localTime.plusMinutes(15);
            
            restTable.add(new SelectItem(localTime.toString()+":00", localTime.toString()));
        }
    }
    
    private void initKbnTable() throws SQLException, NamingException {
        
        Connection connection = null;
        kbnTable = new ArrayList<SelectItem>();
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 区分テーブルからデータを取得しkbnTableにセット
            ktc.getTableUseEditInput(connection, kbnTable);
            
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
//        Connection connection = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        String kbnName = null;
//        kbnTable = new ArrayList<SelectItem>();
//        
//        try {
//            
//            // データベース接続
//            connection = DBController.open();
//            
//            // attendanceテーブルからデータを取得
//            stmt = connection.prepareStatement("SELECT * FROM kbn");
//            rs = stmt.executeQuery();
//
//            // 今まで登録されているデータを取得し設定
//            while (rs.next()) {
//                
//                kbnTable.add(new SelectItem(rs.getInt("kbn_cd"),rs.getString("name")));
//            }
//        
//        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, "Naming例外です", ex);
//            ex.printStackTrace();
//            throw new NamingException();
//        } catch (SQLException ex) {
//            LOG.log(Level.SEVERE, "SQL例外です", ex);
//            ex.printStackTrace();
//            throw new SQLException();
//        } finally {
//            
//            // クローズ
//            try {
//                if (rs != null)
//                    rs.close();
//                rs = null;
//            } catch (SQLException ex) {
//                LOG.log(Level.SEVERE, "ResultSetクローズ失敗", ex);
//                ex.printStackTrace();
//            }
//            
//            try {
//                if (stmt != null)
//                    stmt.close();
//                stmt = null;
//            } catch (SQLException ex) {
//                LOG.log(Level.SEVERE, "Statementクローズ失敗", ex);
//                ex.printStackTrace();
//            }
//            
//            try {
//                if (connection != null)
//                    connection.close();
//                connection = null;
//            } catch (SQLException ex) {
//                LOG.log(Level.SEVERE, "Connectionクローズ失敗", ex);
//                ex.printStackTrace();
//            }
//        }
        
    }

    public ArrayList<SelectItem> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(ArrayList<SelectItem> timeList) {
        this.timeTable = timeList;
    }

    public ArrayList<SelectItem> getRestTable() {
        return restTable;
    }

    public void setRestTable(ArrayList<SelectItem> restTable) {
        this.restTable = restTable;
    }

    public ArrayList<SelectItem> getKbnTable() {
        return kbnTable;
    }

    public void setKbnTable(ArrayList<SelectItem> kbnTable) {
        this.kbnTable = kbnTable;
    }
    
}
