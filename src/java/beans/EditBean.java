/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.KintaiData;
import data.KintaiKey;
import data.UserData;
import database.AttendanceTableController;
import database.DBController;
import database.WorkingPatternTableController;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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
    
    private AttendanceTableController atc = null;
    private WorkingPatternTableController wtc = null;
    
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
        atc = new AttendanceTableController();
        wtc = new WorkingPatternTableController();
        
        try {
            initKintaiData();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
        }
        
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
    
    private void initKintaiData() throws SQLException, NamingException {
        
        kintaiData = new KintaiData(kintaiKey.getYm(),kintaiKey.getDay());
        
        Connection connection = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 勤怠データの初期値をユーザーの勤務パターンに合わせる
            wtc.getTableUseEdit(connection, userData.getWorkptn_cd(), kintaiData);
            
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
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
    }
    
    private void readKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // キーが一致する勤怠データを読込
            atc.getTableUseEdit(connection, this.kintaiKey.getYm(), this.kintaiKey.getUserId(), this.kintaiKey.getDay(), kintaiData);
            
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
    }
    
    public String entry() throws SQLException, NamingException {
        
        Connection connection = null;
        
        // disableフラグがtrueか
        // trueの場合、出退勤時間、休憩時間の設定は無効
        if (this.disabled) {
            this.kintaiData.setStartToStringEdit("00:00:00");
            this.kintaiData.setEndToStringEdit("00:00:00");
            this.kintaiData.setRestToString("00:00:00");
        }
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 入力値を勤怠データに書込
            atc.setTableUseEdit(connection, this.kintaiData, this.userData);
            
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
        
        return "kintai.xhtml";
    }
    
    public String back() {
        return "kintai.xhtml";
    }
    

    public void setViewKbn(int kbnCd) {
        kintaiData.setKbnCd(kbnCd);
    }
    
    public void setViewStart(String start) {
        kintaiData.setStart(Time.valueOf(start));
    }
    
    public void setViewEnd(String end) {
        kintaiData.setEnd(Time.valueOf(end));
    }
    
    public void setViewRest(String rest) {
        kintaiData.setRest(Time.valueOf(rest));
    }
    
    public int getViewKbn() {
        return kintaiData.getKbnCd();
    }
    
    public String getViewStart() {
        return kintaiData.getStart().toString();
    }
    
    public String getViewEnd() {
        return kintaiData.getEnd().toString();
    }
    
    public String getViewRest() {
        return kintaiData.getRest().toString();
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
