/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@RequestScoped
public class EditInput {
    
    private ArrayList<SelectItem> timeTable = null;
    private ArrayList<SelectItem> restTable = null;

    public EditInput() {
        // 出退勤タイムテーブルを初期化
        initTimeTable();
        
        // 休憩時間テーブルを初期化
        initRestTable();
    }
    
    private void initTimeTable() {
        
        timeTable = new ArrayList<SelectItem>();
        int m = 0;
        
        for (int h = 0; h < 24; m += 15) {
            if (m >= 60) {
                m = 0;
                h++;
            }
            
            String hs = null;
            String ms = null;
            
            if (h < 10) {
                hs = "0";
            } else {
                hs = "";
            }
            if (m < 10) {
                ms = "0";
            } else {
                ms = "";
            }
            hs += String.valueOf(h)+":";
            ms += String.valueOf(m);
            
            timeTable.add(new SelectItem(hs+ms+":00", hs+ms));
        }
    }
    
    private void initRestTable() {
        
        restTable = new ArrayList<SelectItem>();
        int m = 0;
        
        for (int h = 1; h < 5; m += 15) {
            if (m >= 60) {
                m = 0;
                h++;
            }
            
            String hs = null;
            String ms = null;
            
            if (h < 10) {
                hs = "0";
            } else {
                hs = "";
            }
            if (m < 10) {
                ms = "0";
            } else {
                ms = "";
            }
            //double d = (double)h + ((double)m/60.0);
            hs += String.valueOf(h)+":";
            ms += String.valueOf(m);
            
            restTable.add(new SelectItem(hs+ms+":00", hs+ms));
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
    
}
