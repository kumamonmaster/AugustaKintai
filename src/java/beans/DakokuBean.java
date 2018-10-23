/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.KintaiData;
import data.UserData;
import database.AttendanceTableController;
import database.DBController;
import database.WorkingPatternTableController;
import java.sql.Connection;
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
@RequestScoped
public class DakokuBean {

    @ManagedProperty(value="#{userData}")
    private UserData userData;
    
    private KintaiData kintaiData = null;
    
    private AttendanceTableController attendanceTC = null;
    private WorkingPatternTableController workinpatternTC = null;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();

    
    
    /************************ getter,setter ****************************/
    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    /*******************************************************************/
    
    /*
    init
    初期化
    */
    @PostConstruct
    public void init() {
        attendanceTC = new AttendanceTableController();
        workinpatternTC = new WorkingPatternTableController();
        
        
        try {
            // kintaiData初期化
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
    kintaiDataを初期化
    */
    private void initKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        
        Calendar c = new GregorianCalendar();
        
        // 現在の年月日をkintaiDataにセット
        kintaiData = new KintaiData(Utility.unionInt(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH) );
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 勤怠データの初期値をユーザーの勤務パターンに合わせる
            workinpatternTC.getTableUseEdit(connection, userData.getWorkptn_cd(), kintaiData);
            
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
    データベースに登録される勤怠データを読込
    */
    private void readKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        
        Calendar c = new GregorianCalendar();
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 現在の年月、ユーザーID、日、でデータ検索
            attendanceTC.getTableUseEdit(connection, Utility.unionInt(c.get(Calendar.YEAR), c.get(Calendar.MONTH)), this.userData.getId(), c.get(Calendar.DAY_OF_MONTH), kintaiData);
            
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
    workStart
    出勤ボタンが押されたら、出勤時刻をデータベースに登録
    */
    public String workStart() throws SQLException, NamingException {
        
        Connection connection = null;
        
        // すでに本日の出勤が打刻されている場合は以下を処理しない
        if (kintaiData.getStart() != null)
            return null;
        
        // 一旦、現在の出勤時刻を保持
        Calendar c = new GregorianCalendar();
        Time workStart = Time.valueOf(String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(c.get(Calendar.MINUTE)) + ":00");
        
        // 出勤時刻を調整
        workStart = adjustStartTime(workStart);
        
        // 出勤時刻を設定
        kintaiData.setStart(workStart);
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 出勤時刻を勤怠データに書込
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
        
        return "dakoku.xhtml";
    }
    
    /*
    adjustStartTime
    出勤時刻を調整
    */
    private Time adjustStartTime(Time start) {
        
        // StringBuilderにセット
        StringBuilder sb = new StringBuilder(start.toString());
        // 出勤開始時刻の'分'をintとして抽出
        int mm = Integer.parseInt(sb.substring(3, 5));
        // 15で割って余り0なら修正なしで返す
        if (mm % 15 == 0)
            return Time.valueOf(sb.toString());
        // 分を15で割り整数部分の0～3の区分に分ける
        int caseNumber =mm / 15;
        
        // 上記で出した区分によって時間を繰り上げる
        switch (caseNumber) {
            // 01～14分なら15分に
            case 0:
                sb.replace(3, 5, "15");
                break;
            // 16～29分なら30分に
            case 1:
                sb.replace(3, 5, "30");
                break;
            // 31～44分なら45分に
            case 2:
                sb.replace(3, 5, "45");
                break;
            // 46～59分なら00分に
            case 3:
                sb.replace(3, 5, "00");
                // 時をひとつ繰り上げる
                sb.replace(0, 2, String.valueOf((Integer.parseInt(sb.substring(0, 2)) + 1)));
                break;
            default:
                break;
        }
        
        // 調整した出勤時刻を返す
        return Time.valueOf(sb.toString());
    }
    
    /*
    workStart
    退勤ボタンが押されたら、退勤時刻をデータベースに登録
    */
    public String workEnd() throws SQLException, NamingException {
        
        Connection connection = null;
        
        // すでに本日の退勤が打刻されている場合は以下を処理しない
        if (kintaiData.getEnd() != null)
            return null;
        // 出勤が押される前に退勤は押せない
        if (kintaiData.getStart() == null)
            return null;
        
        // 一旦、現在の退勤時刻を保持
        Calendar c = new GregorianCalendar();
        Time workEnd = Time.valueOf(String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(c.get(Calendar.MINUTE)) + ":00");
        
        // 退勤時刻を調整
        workEnd = adjustEndTime(workEnd);
        
        // 退勤時刻設定
        kintaiData.setEnd(workEnd);
        kintaiData.setRest(Time.valueOf("01:00:00"));
        
        // 勤務計算
        kintaiData.setTotal(MathKintai.resultTotal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
        kintaiData.setOver(MathKintai.resultOver(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
        kintaiData.setReal(MathKintai.resultReal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest(), kintaiData.getKbnCd()));
        kintaiData.setLate(MathKintai.resultLate(kintaiData.getStart(), kintaiData.getStart_default()));
        kintaiData.setLeave(MathKintai.resultLeave(kintaiData.getEnd(), kintaiData.getEnd_default()));
        kintaiData.setRemarks("");
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 入力値を勤怠データに書込
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
        
        return "dakoku.xhtml";
    }
    
    /*
    adjustStartTime
    出勤時刻を調整
    */
    private Time adjustEndTime(Time end) {
        
        // StringBuilderにセット
        StringBuilder sb = new StringBuilder(end.toString());
        // 出勤開始時刻の'分'をintとして抽出
        int mm = Integer.parseInt(sb.substring(3, 5));
        // 15で割って余り0なら修正なしで返す
        if (mm % 15 == 0)
            return Time.valueOf(sb.toString());
        // 分を15で割り整数部分の0～3の区分に分ける
        int caseNumber =mm / 15;
        
        // 上記で出した区分によって時間を繰り上げる
        switch (caseNumber) {
            // 01～14分なら15分に
            case 0:
                sb.replace(3, 5, "00");
                break;
            // 16～29分なら30分に
            case 1:
                sb.replace(3, 5, "15");
                break;
            // 31～44分なら45分に
            case 2:
                sb.replace(3, 5, "30");
                break;
            // 46～59分なら00分に
            case 3:
                sb.replace(3, 5, "45");
                break;
            default:
                break;
        }
        
        // 調整した出勤時刻を返す
        return Time.valueOf(sb.toString());
    }
    
    public String goDakokuPage() {
        
        return "dakoku.xhtml";
    }
    
    public String goKintaiPage() {
        
        return "kintai.xhtml";
    }
}
