/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.Date;
import java.sql.Time;
import javax.faces.context.FacesContext;
import util.MathKintai;
import util.Utility;

/**
 *
 * @author 佐藤孝史
 */
public class KintaiData {
    
    private int ym = 0;
    private int day = 0;
    private Time start = null;
    private Time end = null;
    private Time rest = null;
    private Time total = null;
    private Time over = null;
    private Time real = null;
    private int kbn_cd = 0;
    
    private boolean dbFlag = false;

    
    public KintaiData(int ym, int day) {

        this.ym = ym;
        this.day = day;
        this.start = new Time(Time.valueOf("09:00:00").getTime());
        this.end = new Time(Time.valueOf("18:00:00").getTime());
        this.total = new Time(Time.valueOf("00:00:00").getTime());
        this.rest = new Time(Time.valueOf("01:00:00").getTime());
        this.over = new Time(Time.valueOf("00:00:00").getTime());
        this.real = new Time(Time.valueOf("00:00:00").getTime());
        this.kbn_cd = 1;
        
        this.dbFlag = false;
    }
    
    public void setKintaiData(Time start, Time end, Time rest, Time total, Time over, Time real, int kbn_cd) {
        this.start = start;
        this.end = end;
        this.total = total;
        this.rest = rest;
        this.over = over;
        this.real = real;
        this.kbn_cd = kbn_cd;
        
        this.dbFlag = true;
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
        
        return String.valueOf(ym) + " " + String.valueOf(day)+" "+Utility.conversionDayOfWeek(ym, day);
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getKbnCd() {
        return kbn_cd;
    }

    public void setKbnCd(int kbn_cd) {
        this.kbn_cd = kbn_cd;
    }

    public String getStartToStringKintai() {
        return (dbFlag) ? start.toString() : "";
    }
    
    public String getStartToStringEdit() {
        return start.toString();
    }
    
    public void setStartToStringEdit(String start) {
        this.start = Time.valueOf(start);
    }
    
    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public String getEndToStringKintai() {
        return (dbFlag) ? end.toString() : "";
    }
    
    public String getEndToStringEdit() {
        return end.toString();
    }
    
    public void setEndToStringEdit(String end) {
        this.end = Time.valueOf(end);
    }
    
    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public Time getTotal() {
        //return total;
        return MathKintai.resultTotal(this.start, this.end, this.rest);
    }

    public void setTotal(Time total) {
        this.total = total;
    }
    
    public String getRestToString() {
        return (dbFlag) ? rest.toString() : "";
        //return end;
    }
    
    public void setRestToString(String rest) {
        this.rest = Time.valueOf(rest);
    }

    public Time getRest() {
        return rest;
    }

    public void setRest(Time rest) {
        this.rest = rest;
    }

    public Time getOver() {
        //return over;
        return MathKintai.resultOver(this.start, this.end, this.rest);
    }

    public void setOver(Time over) {
        this.over = over;
    }

    public Time getReal() {
        return real;
    }

    public void setReal(Time real) {
        this.real = real;
    }
}
