/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.Date;
import java.sql.Time;
import util.Utility;

/**
 *
 * @author 佐藤孝史
 */
public class KintaiData {
    
    private int ym;
    private int day;
    private Time start;
    private Time end;
    private double rest;
    private double total;
    private double over;
    private double real;
    private String kbn;

    
    public KintaiData(int ym, int day) {
        this.ym = ym;
        this.day = day;
        this.start = null;
        this.end = null;
        this.total = 0.0;
        this.rest = 0.0;
        this.over = 0.0;
        this.real = 0.0;
        this.kbn = "";
    }
    
    public void setKintaiData(Time start, Time end, double total, double rest, double over, double real, String kbn) {
        this.start = start;
        this.end = end;
        this.total = total;
        this.rest = rest;
        this.over = over;
        this.real = real;
        this.kbn = kbn;
    }
    

    public int getYm() {
        return ym;
    }

    public void setYm(int ym) {
        this.ym = ym;
    }

    public int getDay() {
        return day;
    }
    
    public String getConversionDate() {
        
        return String.valueOf(day)+" "+Utility.conversionDayOfWeek(ym, day);
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getKbn() {
        return kbn;
    }

    public void setKbn(String kbn) {
        this.kbn = kbn;
    }

    public String getStart() {
        return (start != null) ? start.toLocalTime().toString() : "";
        //return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public String getEnd() {
        return (end != null) ? end.toLocalTime().toString() : "";
        //return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getRest() {
        return rest;
    }

    public void setRest(double rest) {
        this.rest = rest;
    }

    public double getOver() {
        return over;
    }

    public void setOver(double over) {
        this.over = over;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }
    
}
