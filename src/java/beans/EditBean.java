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
import util.MathKintai;
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
    
    // データベースのテーブルコントローラー
    private AttendanceTableController attendanceTC = null;
    private WorkingPatternTableController workingpatternTC = null;
    
    // 勤怠データ
    private KintaiData kintaiData = null;
    
    // 入力ロックフラグ
    private boolean disabled = false;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();

    /**
     * Creates a new instance of EditBean
     */
    public EditBean() {
    }
    
    /*
    init
    初期化
    */
    @PostConstruct
    public void init() {
        attendanceTC = new AttendanceTableController();
        workingpatternTC = new WorkingPatternTableController();
        
        try {
            // 勤怠データ初期化
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
    
    /*
    initKintaiData
    勤怠データの初期化
    */
    private void initKintaiData() throws SQLException, NamingException {
        
        // 勤怠データを初期化
        kintaiData = new KintaiData(kintaiKey.getYm(),kintaiKey.getDay());
        
        Connection connection = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 勤怠データの初期値をユーザーの勤務パターンに合わせる
            workingpatternTC.getTableUseEdit(connection, userData.getWorkptn_cd(), kintaiData);
            
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
    
    /*
    readKintaiData
    勤怠データの読込
    */
    private void readKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // キーが一致する勤怠データを読込
            attendanceTC.getTableUseEdit(connection, this.kintaiKey.getYm(), this.kintaiKey.getUserId(), this.kintaiKey.getDay(), kintaiData);
            
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
    
    /*
    entry
    戻り値：String
    入力された値をデータベースに登録し、勤怠画面へとページ遷移
    */
    public String entry() throws SQLException, NamingException {
        
        Connection connection = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 入力値を勤怠データに書込
            kintaiDataCalculation();
            kintaiDataDisabled();
            attendanceTC.setTableUseEditDakoku(connection, this.kintaiData, this.userData);
            
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
    
    /*
    kintaiDataCalculation
    総労働時間、残業時間、実労働時間、遅刻、早退を算出
    */
    private void kintaiDataCalculation() {
        
        // すべての入力値が0になる場合（特別休暇、公休、代休、欠勤）
        if (kintaiData.getKbnCd() == 2 ||
                kintaiData.getKbnCd() == 3 ||
                kintaiData.getKbnCd() == 7 ||
                kintaiData.getKbnCd() == 8)
            return;
        
        // nullチェック
        if (kintaiData.getStart() != null &&
                kintaiData.getEnd() != null &&
                kintaiData.getRest() != null) {
            
            // 総労働時間算出
            kintaiData.setTotal(MathKintai.resultTotal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
            // 残業時間算出
            kintaiData.setOver(MathKintai.resultOver(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
            // 実労働時間算出
            kintaiData.setReal(MathKintai.resultReal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest(), kintaiData.getKbnCd()));
        }
        
        // nullチェック
        if (kintaiData.getStart() != null &&
                kintaiData.getStart_default()!= null)
        {
            // 遅刻算出
            kintaiData.setLate(MathKintai.resultLate(kintaiData.getStart(), kintaiData.getStart_default()));
        }
        
        // nullチェック
        if (kintaiData.getEnd() != null &&
                kintaiData.getEnd_default()!= null)
        {
            // 早退算出
            kintaiData.setLeave(MathKintai.resultLeave(kintaiData.getEnd(), kintaiData.getEnd_default()));
        }
    }
    
    /*
    kintaiDataDisabled
    入力ロックの場合の値設定
    */
    private void kintaiDataDisabled() {
        
        // 入力ロックフラグ
        if (disabled) {
            
            // 出退勤、休憩、残業、遅刻早退を0に
            kintaiData.setStart(new Time(Time.valueOf("00:00:00").getTime()));
            kintaiData.setEnd(new Time(Time.valueOf("00:00:00").getTime()));
            kintaiData.setRest(new Time(Time.valueOf("00:00:00").getTime()));
            kintaiData.setOver(new Time(Time.valueOf("00:00:00").getTime()));
            kintaiData.setLate(new Time(Time.valueOf("00:00:00").getTime()));
            kintaiData.setLeave(new Time(Time.valueOf("00:00:00").getTime()));
            
            // 有休でない場合は総労働、実労働も0に
            if (kintaiData.getKbnCd() != 4){
                kintaiData.setTotal(new Time(Time.valueOf("00:00:00").getTime()));
                kintaiData.setReal(new Time(Time.valueOf("00:00:00").getTime()));
            }
        }
    }
    
    /*
    goKintaiPage
    戻り値：String
    勤怠ページ画面遷移
    */
    public String goKintaiPage() {
        return "kintai.xhtml";
    }
    
    
    /********************** Viewが参照するメソッド ********************/
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
    
    public void setViewRemarks(String remarks) {
        kintaiData.setRemarks(remarks);
    }
    
    public int getViewYM() {
        return kintaiData.getYm();
    }
    
    public int getViewDay() {
        return kintaiData.getDay();
    }
    
    public int getViewKbn() {
        return kintaiData.getKbnCd();
    }
    
    public String getViewStart() {
        
        if (kintaiData.getStart() != null)
            return kintaiData.getStart().toString();
        else
            return kintaiData.getStart_default().toString();
    }
    
    public String getViewEnd() {
        
        if (kintaiData.getStart() != null)
            return kintaiData.getEnd().toString();
        else
            return kintaiData.getEnd_default().toString();
    }
    
    public String getViewRest() {
        
        if (kintaiData.getRest() != null)
            return kintaiData.getRest().toString();
        else
            return new Time(Time.valueOf("01:00:00").getTime()).toString();
    }
    
    public String getViewRemarks() {
        
        if (kintaiData.getRemarks() != null)
            return kintaiData.getRemarks();
        else
            return "";
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
