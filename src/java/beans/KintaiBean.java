/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.DakokuMessage;
import data.KbnData;
import data.KintaiData;
import data.KintaiKey;
import data.KintaiYearMonth;
import data.UserData;
import database.DBController;
import database.KintaiBeanDataAccess;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import util.Log;
import util.MathKintai;
import session.SessionTimeOutFilter;
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
public class KintaiBean implements Serializable {

    @ManagedProperty(value="#{userData}")
    private UserData userData;
    @ManagedProperty(value="#{kbnData}")
    private KbnData kbnData;
    @ManagedProperty(value="#{kintaiKey}")
    private KintaiKey kintaiKey;
    @ManagedProperty(value="#{kintaiYearMonth}")
    private KintaiYearMonth kintaiYearMonth;
    @ManagedProperty(value="#{dakokuMessage}")
    private DakokuMessage dakokuMessage;
    
    // データベースのテーブルコントローラー
//    private AttendanceTableController attendanceTC = null;
//    private KbnTableController kbnTC = null;
//    private WorkPatternTableController workingPatternTC = null;
    private KintaiBeanDataAccess kintaibeanDA = null;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();
    
    // 勤怠データリスト
    private ArrayList<KintaiData> kintaiDataList = null;
    // 選択月度リスト
    private ArrayList<SelectItem> yearMonthList = null;
    // 今月度有休取得日数
    private double yukyuDays = 0.0;
    // 有休残数
    private double yukyuRemainingDay = 0.0;
    
    

    /*
    KintaiBeanコンストラクタ
    @PostConstructでコンストラクタ呼ばないと@ManagedPropertyがnullのままでエラーとなる
    ここで初期化処理
    kintaiDataListに日付を設定し、データベースに存在する勤怠データを一致する日付のkintaiDataListに設定
    */
    @PostConstruct
    public void init() throws ViewExpiredException {
        
        kintaibeanDA = new KintaiBeanDataAccess();
        
        // 打刻画面メッセージを初期化
        dakokuMessage.setResultMessage("");
        
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
        if (kintaiYearMonth.getYear() == 0)
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        else
            c.set(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth()-1, 1);
        
        kintaiYearMonth.setYear(c.get(Calendar.YEAR));
        kintaiYearMonth.setMonth(c.get(Calendar.MONTH)+1);
        // 月度の最終日を取得
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 今月度を設定
        yearMonthList = createYearMonthList(kintaiYearMonth);
        
        // kintaiDataListの日付部分を設定
        for (int i = 1; i <= lastDay; i++) {
            
            // Stringで日付と曜日を設定
            kintaiDataList.add(new KintaiData(Utility.unionYearMonth(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth()), c.get(Calendar.DAY_OF_MONTH)));
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
            Time start = null;
            Time end = null;
            kintaibeanDA.getWorkPatternData(connection, userData.getWorkptn_cd(), start, end);
            for (KintaiData kintaiData :kintaiDataList) {
                
                kintaiData.setStart_default(start);
                kintaiData.setEnd_default(end);
            }
        
            // 勤怠実績を読込
            kintaibeanDA.getAttendanceData(connection, Utility.unionYearMonth(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth()), this.userData, kintaiDataList);
            
            // 勤務区分を読込
            for (KintaiData kintaiData :kintaiDataList) {
                
                kintaiData.setKbnName(kbnData.getKbnList().get(kintaiData.getKbnCd()));
            }
            
            yukyuDays = kintaibeanDA.getYukyuMonthData(connection, Utility.unionYearMonth(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth()), userData, kbnData);
            yukyuRemainingDay = kintaibeanDA.getYukyuRemainingData(connection, Utility.unionYearMonthDay(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth(),31), userData);
                    
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
    
    
    /*
    createYearMonthList
    YearMonthListを設定
    */
    private ArrayList<SelectItem> createYearMonthList(KintaiYearMonth kintaiYearMonth) {
        
        int range = 12;
        ArrayList<SelectItem> list = new ArrayList<SelectItem>();
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, kintaiYearMonth.getYear());
        c.set(Calendar.MONTH, kintaiYearMonth.getMonth()-1);
        // 現在からrange/2カ月前を頭に設定
        c.add(Calendar.MONTH, -range/2);

        // range分リストに追加
        for (int i = 0; i < range; i++) {
            list.add(new SelectItem(Utility.unionYearMonth(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1), c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONTH)+1)+"月"));
            c.add(Calendar.MONTH, +1);
        }
        
        return list;
    }
    
    
    /************************** getter,setter *************************/
    public ArrayList<KintaiData> getKintaiDataList() {
        return kintaiDataList;
    }
    
    public void setUserData(UserData userData) {
        
        this.userData = userData;
    }

    public void setKbnData(KbnData kbnData) {
        this.kbnData = kbnData;
    }

    public void setKintaiKey(KintaiKey kintaiKey) {
        this.kintaiKey = kintaiKey;
    }
    
    public void setKintaiYearMonth(KintaiYearMonth kintaiYearMonth) {
        this.kintaiYearMonth = kintaiYearMonth;
    }

    public void setDakokuMessage(DakokuMessage dakokuMessage) {
        this.dakokuMessage = dakokuMessage;
    }
    
    public void setYearMonth(int ym) {
        
//        kintaiYearMonth.setYear(ym/100);
//        if (String.valueOf(ym).length() > 5)
//            kintaiYearMonth.setMonth(ym%100);
//        else
//            kintaiYearMonth.setMonth(ym%10);
    }
    
    public void setYearMonthChanged(ValueChangeEvent e) {
        
        // 以前の入力値と現在の入力値が変わっているか
        if (e.getOldValue() != e.getNewValue()) {
            
            // 現在の入力値を取得
            int ym = (int)e.getNewValue();

            // 年月の文字数を調べる（201810、20189で長さが違うため）
            if (String.valueOf(ym).length() > 5) {
                
                // 100で割って年と月で分ける
                kintaiYearMonth.setYear(ym/100);
                kintaiYearMonth.setMonth(ym%100);
            }
            else {
                // 10で割って年と月で分ける
                kintaiYearMonth.setYear(ym/10);
                kintaiYearMonth.setMonth(ym%10);
            }
            
            // 入力された年月で初期化
            init();
        }
    }
    
    public int getYearMonth() {
        
        return Utility.unionYearMonth(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth());
    }
    
    public ArrayList<SelectItem> getYearMonthList() {
        
        return yearMonthList;
    }
    
    public void setYearMonthList(ArrayList<SelectItem> yearMonthList) {
        
        this.yearMonthList = yearMonthList;
    }
    /******************************************************************/
    
    
    /********************** Viewが参照するメソッド ********************/
    
    public void setViewYearMonth(int ym) {

        // 年月の文字数を調べる（201810、20189で長さが違うため）
        if (String.valueOf(ym).length() > 5) {

            // 100で割って年と月で分ける
            kintaiYearMonth.setYear(ym/100);
            kintaiYearMonth.setMonth(ym%100);
        }
        else {
            // 10で割って年と月で分ける
            kintaiYearMonth.setYear(ym/10);
            kintaiYearMonth.setMonth(ym%10);
        }

        // 入力された年月で初期化
        init();
    }
    
    /*
    viewYearMonth
    戻り値：String
    年月
    */
    public ArrayList<KintaiData> getViewKintaiDataList() {
        
        return kintaiDataList;
    }
    
    /*
    viewYearMonth
    戻り値：String
    年月
    */
    public int getViewYearMonth() {
        
        return Utility.unionYearMonth(kintaiYearMonth.getYear(), kintaiYearMonth.getMonth());
    }
    
    /*
    getViewYearMonthList
    戻り値：String
    年月
    */
    public ArrayList<SelectItem> getViewYearMonthList() {
        
        return yearMonthList;
    }
    
    /*
    viewDate
    戻り値：String
    日付
    */
    public String getViewDay(int ym, int day) {
        
        return String.valueOf(day)+"日 ("+Utility.conversionDayOfWeek(ym, day) + ")";
    }
    
    /*
    viewKbn
    戻り値：String
    勤怠区分
    */
    public String getViewKbn(KintaiData kintaiData) {
        
        return kintaiData.getKbnName();
    }
    
    /*
    viewStart
    戻り値：String
    出勤時間
    */
    public String getViewStart(KintaiData kintaiData) {
        
        if (kintaiData.getStart() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getStart().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewEnd
    戻り値：String
    退勤時間
    */
    public String getViewEnd(KintaiData kintaiData) {
        
        if (kintaiData.getEnd() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getEnd().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewTotal
    戻り値：String
    総労働時間
    */
    public String getViewTotal(KintaiData kintaiData) {
        
        if (kintaiData.getTotal() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getTotal().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewRest
    戻り値：String
    休憩時間
    */
    public String getViewRest(KintaiData kintaiData) {
        
        if (kintaiData.getRest() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getRest().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewOver
    戻り値：String
    残業時間
    */
    public String getViewOver(KintaiData kintaiData) {
        
        if (kintaiData.getOver() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getOver().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewReal
    戻り値：String
    実労働時間
    */
    public String getViewReal(KintaiData kintaiData) {
        
        if (kintaiData.getReal() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getReal().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewLate
    戻り値：String
    遅刻時間
    */
    public String getViewLate(KintaiData kintaiData) {
        
        if (kintaiData.getLate() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getLate().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewLeave
    戻り値：String
    早退時間
    */
    public String getViewLeave(KintaiData kintaiData) {
        
        if (kintaiData.getLeave() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String sTime = fmt.format(kintaiData.getLeave().getTime());
            return sTime;
        }
        else {
            return "";
        }
    }
    
    /*
    viewRemarks
    戻り値：String
    備考
    */
    public String getViewRemarks(KintaiData kintaiData) {
        
        return kintaiData.getRemarks();
    }
    
    /*
    getViewTotalSum
    戻り値：String
    備考
    */
    public String getViewSumTotal() {
        
        return String.valueOf(MathKintai.resultSumTotal(kintaiDataList));
    }
    
    /*
    getViewOverSum
    戻り値：String
    備考
    */
    public String getViewSumOver() {
        
        return String.valueOf(MathKintai.resultSumOver(kintaiDataList));
    }
    
    /*
    getViewRealSum
    戻り値：String
    備考
    */
    public String getViewSumReal() {
        
        return String.valueOf(MathKintai.resultSumReal(kintaiDataList));
    }
    
    /*
    getViewLateSum
    戻り値：String
    備考
    */
    public String getViewSumLate() {
        
        return String.valueOf(MathKintai.resultSumLate(kintaiDataList));
    }
    
    /*
    getViewSumLeave
    戻り値：String
    備考
    */
    public String getViewSumLeave() {
        
        return String.valueOf(MathKintai.resultSumLeave(kintaiDataList));
    }
    
    /*
    getViewSumLeave
    戻り値：String
    備考
    */
    public double getViewSumYukyu() {
        
        return yukyuDays;
    }
    
    /*
    getViewSumLeave
    戻り値：String
    備考
    */
    public double getViewRemainingYukyu() {
        
        return yukyuRemainingDay;
    }
    /**************************************************************/
    
    
    /************************* ページ遷移 *************************/
    /*
    transitionEditPage
    戻り値:String
    編集画面へページ遷移
    */
    public String goEditPage(int ym, String user_id, int day) {
        
        // データベースへアクセスするためのキーを登録
        this.kintaiKey.setKey(ym, user_id, day);
        
        return "edit.xhtml?faces-redirect=true";
    }
    
    /*
    transitionDakokuPage
    戻り値:String
    打刻画面へページ遷移
    */
    public String goDakokuPage() {
        return "dakoku.xhtml?faces-redirect=true";
    }
    
    /*
    transitionKintaiPage
    戻り値:String
    勤怠画面へページ遷移
    */
    public String goKintaiPage() {
        return "kintai.xhtml?faces-redirect=true";
    }
    /***************************************************************/
}
