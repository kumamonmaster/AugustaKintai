/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.KintaiData;
import data.UserData;
import database.DBController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.naming.NamingException;
import util.Conversion;
import util.Log;

/**
 *
 * @author 佐藤孝史
 * 
 * 勤怠データのBeanクラス
 * 
 */
@ManagedBean
@RequestScoped
public class KintaiBean {

    @ManagedProperty(value="#{userData}")
    private UserData userData;
    
    private ArrayList<KintaiData> kintaiDataList = null;
    // ログ生成
    private Log log = new Log(LoginBean.class.getName(), "test.log");
    // 今月度
    private ArrayList<String> monthList = null;
    
    /*
    KintaiBeanコンストラクタ
    @PostConstructでコンストラクタ呼ばないと@ManagedPropertyがnullのままでエラーとなる
    ここで初期化処理
    kintaiDataListに日付を設定し、データベースに存在する勤怠データを一致する日付のkintaiDataListに設定
    */
    @PostConstruct
    public void init() {
        
        try {
            // rowData初期化
            initKintaiData();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "Naming例外です", ex);
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
        // 今月度を設定 0から始まるため+1する
        monthList = setMonth();
        
        // kintaiDataListの日付部分を設定
        for (int i = 1; i <= lastDay; i++) {
            
            // Stringで日付と曜日を設定
            kintaiDataList.add(new KintaiData(c.getTime()));
            // 日付を1日ずらす
            c.add(Calendar.DAY_OF_MONTH, +1);
        }
        
        try {
            // kintaiDataListにデータベースの勤怠データを設定
            setKintaiData();
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
        }
    }
    
    private ArrayList<String> setMonth() {
        
        int range = 12;
        ArrayList<String> monthList = new ArrayList<String>();
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -range/2);
        
        for (int i = 0; i < range; i++) {
            monthList.add(String.valueOf(c.get(Calendar.MONTH)+1));
            c.add(Calendar.MONTH, +1);
        }
        
        return monthList;
    }
    
    /*
    setKintaiData
    データベースに存在する勤怠データを設定
    */
    private void setKintaiData() throws SQLException, NamingException {
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Calendar c_sql = new GregorianCalendar();
        Calendar c_kintai = new GregorianCalendar();
        
        try {
            
            // データベース接続
            connection = DBController.open();
            
            // userテーブルからデータを取得
            stmt = connection.prepareStatement("SELECT * FROM attendance WHERE kintai_key LIKE ?");
            stmt.setString(1, "%"+this.userData.getId()+"%");
            rs = stmt.executeQuery();

            // 今まで登録されているデータを取得し設定
            while (rs.next()) {
                // SQL_DATEをUTIL_DATEに変換
                Date date = Conversion.conversionSQLDateToDate(rs.getDate("date"));
                // カレンダーにデータベースから取得したDateをセット
                c_sql.setTime(date);
                
                kintaiDataList.get(c_sql.get(Calendar.DAY_OF_MONTH)-1).setKintaiData(rs.getTime("start"), rs.getTime("end"), 
                                rs.getDouble("rest"), rs.getDouble("total"), rs.getDouble("over"), 
                                rs.getDouble("real"), rs.getInt("kbn_id"));
            }
        
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "Naming例外です", ex);
            ex.printStackTrace();
            throw new NamingException();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "SQL例外です", ex);
            ex.printStackTrace();
            throw new SQLException();
        } finally {
            
            // クローズ
            try {
                if (rs != null)
                    rs.close();
                rs = null;
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "ResultSetクローズ失敗", ex);
                ex.printStackTrace();
            }
            
            try {
                if (stmt != null)
                    stmt.close();
                stmt = null;
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Statementクローズ失敗", ex);
                ex.printStackTrace();
            }
            
            try {
                if (connection != null)
                    connection.close();
                connection = null;
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Connectionクローズ失敗", ex);
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<KintaiData> getKintaiDataList() {
        return kintaiDataList;
    }
    
    public void setUserData(UserData userData) {
        
        this.userData = userData;
    }
    
    public ArrayList<String> getMonthList() {
        return monthList;
    }
    
    public String getMonth() {
        return monthList.get(6);
    }
}
