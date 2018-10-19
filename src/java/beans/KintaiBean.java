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
import database.KbnTableController;
import database.WorkingPatternTableController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;
import util.Log;
import util.MathKintai;
import util.Utility;

/**
 *
 * @author 佐藤孝史
 * 
 * 勤怠データのBeanクラス
 * 
 */
@ManagedBean
@ViewScoped
public class KintaiBean {

    @ManagedProperty(value="#{userData}")
    private UserData userData;
    @ManagedProperty(value="#{kintaiKey}")
    private KintaiKey kintaiKey;
    
    private AttendanceTableController attendanceTC = null;
    private KbnTableController kbnTC = null;
    private WorkingPatternTableController workingPatternTC = null;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    private ArrayList<KintaiData> kintaiDataList = null;
    // 選択月度リスト
    private ArrayList<String> yearMonthList = null;
    // 今月度
    private String nowYearMonth = null;
    
    
    /*
    KintaiBeanコンストラクタ
    @PostConstructでコンストラクタ呼ばないと@ManagedPropertyがnullのままでエラーとなる
    ここで初期化処理
    kintaiDataListに日付を設定し、データベースに存在する勤怠データを一致する日付のkintaiDataListに設定
    */
    @PostConstruct
    public void init() {
        
        attendanceTC = new AttendanceTableController();
        kbnTC = new KbnTableController();
        workingPatternTC = new WorkingPatternTableController();
        
        try {
            // rowData初期化
            initKintaiData();
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
    日付の設定
    */
    private void initKintaiData() throws SQLException, NamingException {

        // kintaiDataList初期化
        kintaiDataList = new ArrayList<KintaiData>();
        
        // カレンダー生成
        Calendar c = new GregorianCalendar();
        // 1日を設定
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        // 月度の最終日を取得
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 今月度を設定
        yearMonthList = setYearMonth();
        
        // kintaiDataListの日付部分を設定
        for (int i = 1; i <= lastDay; i++) {
            
            // Stringで日付と曜日を設定
            kintaiDataList.add(new KintaiData(Utility.unionInt(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH)));
            // 日付を1日ずらす
            c.add(Calendar.DAY_OF_MONTH, +1);
        }
        
        try {
            // kintaiDataListにデータベースの勤怠データを設定
            readKintaiData();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
        }
    }
    
    /*
    setKintaiData
    データベースに存在する勤怠データを設定
    */
    private void readKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rsAttendance = null;
        ResultSet rsKbn = null;
        
        try {
            // データベース接続
            connection = DBController.open();
            
            // 勤務パターンを読込
            for (KintaiData kintaiData :kintaiDataList) {
                workingPatternTC.getTableUseEdit(connection, userData.getWorkptn_cd(), kintaiData);
            }
        
            // 勤怠実績を読込
            attendanceTC.getTableUseKintai(connection, Integer.parseInt(nowYearMonth), this.userData, kintaiDataList);
            
            // 勤務区分を読込
            for (KintaiData kintaiData :kintaiDataList) {
                kbnTC.getTableUseKintai(connection, kintaiData.getKbnCd(), kintaiData);
            }
            
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
    
    
    
    private ArrayList<String> setYearMonth() {
        
        int range = 12;
        ArrayList<String> list = new ArrayList<String>();
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -range/2);
        
        for (int i = 0; i < range; i++) {
            list.add(String.valueOf(c.get(Calendar.YEAR))+"年"+String.valueOf(c.get(Calendar.MONTH)+1+i)+"月");
            setNowYearMonth(c.get(Calendar.MONTH)+1+i);
        }
        
        return list;
    }
    
    private void setNowYearMonth(int month) {
        
        Calendar c = new GregorianCalendar();
        
        if (c.get(Calendar.MONTH)+1 == month) {
            nowYearMonth = String.valueOf(c.get(Calendar.YEAR)) + String.valueOf(c.get(Calendar.MONTH)+1);
        }
    }
    
    public String getNowYearMonth() {
        return nowYearMonth;
    }
    
    public ArrayList<KintaiData> getKintaiDataList() {
        return kintaiDataList;
    }
    
    public void setUserData(UserData userData) {
        
        this.userData = userData;
    }

    public void setKintaiKey(KintaiKey kintaiKey) {
        this.kintaiKey = kintaiKey;
    }
    
    public void setYearMonth(String s) {
        // ここは修正する
        //yearMonthList.get(0);
    }
    
    public String getYearMonth() {
        // ここは修正する
        return yearMonthList.get(6);
    }
    
    public ArrayList<String> getYearMonthList() {
        return yearMonthList;
    }
    
    public void setYearMonthList(ArrayList<String> yearMonthList) {
        this.yearMonthList = yearMonthList;
    }

    public void setNowYearMonth(String nowYearMonth) {
        this.nowYearMonth = nowYearMonth;
    }
    
    public String viewDate(int ym, int day) {
        
        return String.valueOf(ym) + " " + String.valueOf(day)+" "+Utility.conversionDayOfWeek(ym, day);
    }
    
    public String viewKbn(KintaiData kintaiData) {
        
        return kintaiData.getKbnName();
    }
    
    public String viewStart(KintaiData kintaiData) {
        
        return (kintaiData.isDbFlag()) ? kintaiData.getStart().toString() : "";
    }
    
    public String viewEnd(KintaiData kintaiData) {
        
        return (kintaiData.isDbFlag()) ? kintaiData.getEnd().toString() : "";
    }
    
    public String viewTotal(KintaiData kintaiData) {

        if (kintaiData.getStart() != null &&
                kintaiData.getEnd() != null &&
                kintaiData.getRest() != null) {
            kintaiData.setTotal(MathKintai.resultTotal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
        }
        
        if (kintaiData.getTotal() != null)
            return kintaiData.getTotal().toString();
        else
            return "";
    }
    
    public String viewRest(KintaiData kintaiData) {
        
        return (kintaiData.isDbFlag()) ? kintaiData.getRest().toString() : "";
    }
    
    public String viewOver(KintaiData kintaiData) {
        
        if (kintaiData.getStart() != null &&
                kintaiData.getEnd() != null &&
                kintaiData.getRest() != null) {
            kintaiData.setOver(MathKintai.resultOver(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest()));
        }
        
        if (kintaiData.getOver() != null)
            return kintaiData.getOver().toString();
        else
            return "";
    }
    
    public String viewReal(KintaiData kintaiData) {
        
        if (kintaiData.getStart() != null &&
                kintaiData.getEnd() != null &&
                kintaiData.getRest() != null) {
            kintaiData.setReal(MathKintai.resultReal(kintaiData.getStart(), kintaiData.getEnd(), kintaiData.getRest(), kintaiData.getKbnCd()));
        }
        
        if (kintaiData.getReal() != null)
            return kintaiData.getReal().toString();
        else
            return "";
    }
    
    public String viewLate(KintaiData kintaiData) {
        
        if (kintaiData.getStart() != null &&
                kintaiData.getStart_default()!= null)
            kintaiData.setLate(MathKintai.resultLate(kintaiData.getStart(), kintaiData.getStart_default()));
        
        if (kintaiData.getLate() != null)
            return kintaiData.getLate().toString();
        else
            return "";
    }
    
    public String viewLeave(KintaiData kintaiData) {
        
        if (kintaiData.getEnd() != null &&
                kintaiData.getEnd_default()!= null)
            kintaiData.setLeave(MathKintai.resultLeave(kintaiData.getEnd(), kintaiData.getEnd_default()));
        
        
        if (kintaiData.getLeave() != null)
            return kintaiData.getLeave().toString();
        else
            return "";
    }
    
    public String transitionEditPage(int ym, String user_id, int day) {
        
        // データベースへアクセスするためのキーを登録
        this.kintaiKey.setKey(ym, user_id, day);
        
        return "edit.xhtml";
    }
}
