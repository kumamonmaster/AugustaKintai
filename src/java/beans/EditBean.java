/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.KintaiData;
import data.KintaiKey;
import data.UserData;
import database.DBController;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;
import util.Log;
import util.Utility;


/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@ViewScoped
public class EditBean {
    
    @ManagedProperty(value="#{kintaiKey}")
    private KintaiKey kintaiKey;
    @ManagedProperty(value="#{userData}")
    private UserData userData;
    
    private KintaiData kintaiData = null;
    
    private boolean disabled = false;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();

    /**
     * Creates a new instance of EditBean
     */
    public EditBean() {
    }
    
    @PostConstruct
    public void init() {
         kintaiData = new KintaiData(kintaiKey.getYm(),kintaiKey.getDay());
        
        try {
            // 該当の勤怠データを取得
            readKintaiData();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
        }
    }
    
    private void readKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            // データベース接続
            connection = DBController.open();
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM attendance WHERE ym = ? AND user_id = ? AND day = ?");
            stmt.setInt(1, this.kintaiKey.getYm());
            stmt.setString(2, this.kintaiKey.getUserId());
            stmt.setInt(3, this.kintaiKey.getDay());
            rs = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            if (rs.next()) {
                kintaiData.setData(
                                rs.getTime("start_time"), rs.getTime("end_time"), 
                                rs.getTime("rest_time"), rs.getTime("total_time"), rs.getTime("over_time"), 
                                rs.getTime("real_time"), rs.getInt("kbn_cd"), "");
            }
        
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
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
            
            try {
                if (connection != null)
                    connection.close();
                connection = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Connectionクローズ失敗", ex);
                ex.printStackTrace();
            }
        }
    }
    
    public String entry() throws SQLException, NamingException {
        
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            
            // データベース接続
            connection = DBController.open();
            
            // attendanceテーブルに入力データをセット
            stmt = connection.prepareStatement("REPLACE INTO attendance (ym,user_id,day,start_time,end_time,rest_time,total_time,real_time,over_time,kbn_cd) VALUES(?,?,?,?,?,?,?,?,?,?)");
            stmt.setInt(1, this.kintaiData.getYm());
            stmt.setString(2, this.userData.getId());
            stmt.setInt(3, this.kintaiData.getDay());
            stmt.setTime(4, this.kintaiData.getStart());
            stmt.setTime(5, this.kintaiData.getEnd());
            stmt.setTime(6, this.kintaiData.getRest());
            stmt.setTime(7, this.kintaiData.getTotal());
            stmt.setTime(8, this.kintaiData.getReal());
            stmt.setTime(9, this.kintaiData.getOver());
            stmt.setInt(10, this.kintaiData.getKbnCd());
            
            stmt.executeQuery();
        
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
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
            
            try {
                if (connection != null)
                    connection.close();
                connection = null;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Connectionクローズ失敗", ex);
                ex.printStackTrace();
            }
        }
        
        return "kintai.xhtml";
    }
    
    public String back() {
        return "kintai.xhtml";
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    
    public void setKintaiKey(KintaiKey kintaiKey) {
        this.kintaiKey = kintaiKey;
    }

    public KintaiData getKintaiData() {
        return kintaiData;
    }

    public void setKintaiData(KintaiData kintaiData) {
        this.kintaiData = kintaiData;
    }

    public boolean isDisabled() {
        
        int cd = kintaiData.getKbnCd();
        
        // 区分をチェックして入力が必要な項目かを返す
        if (cd != 1 &&
                cd != 5 &&
                cd != 6) {
            disabled = true;
        } else {
            disabled = false;
        }
        
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
}
