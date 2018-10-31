/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import database.DBController;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
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
    
    @ManagedProperty(value="#{kbnData}")
    private KbnData kbnData;
    
    private ArrayList<SelectItem> timeTable = null;
    private ArrayList<SelectItem> restTable = null;
    private ArrayList<SelectItem> kbnTable = null;
    
    // ログ生成
    private static final Logger LOG = Log.getLog();

    
    public void setKbnData(KbnData kbnData) {
        this.kbnData = kbnData;
    }

    @PostConstruct
    public void init() {
        
        // 出退勤タイムテーブルを初期化
        initTimeTable();
        
        // 休憩時間テーブルを初期化
        initRestTable();
        
        // 区分テーブルを初期化
        initKbnTable();
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
    
    private void initKbnTable() {
        
        kbnTable = new ArrayList<SelectItem>();
        
        int index = 0;
        
        for (String kbnName: kbnData.getKbnList()) {
            
            kbnTable.add(new SelectItem(index, kbnName));
            index++;
        }
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
