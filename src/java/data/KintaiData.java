/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private String kbnName = null;
    
    private boolean dbFlag = false;

    
    public KintaiData(int ym, int day) {

        this.ym = ym;
        this.day = day;
        this.start = null;
        this.end = null;
        this.total = null;
        this.rest = null;
        this.over = null;
        this.real = null;
        this.kbn_cd = 1;
        this.kbnName = "";
        
        this.dbFlag = false;
    }
    
    public void setData(Time start, Time end, Time rest, Time total, Time over, Time real, int kbn_cd, String kbnName) {
        this.start = start;
        this.end = end;
        this.total = total;
        this.rest = rest;
        this.over = over;
        this.real = real;
        this.kbn_cd = kbn_cd;
        this.kbnName = kbnName;
        
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

    public String getKbnName() {
        return kbnName;
    }

    public void setKbnName(String kbnName) {
        this.kbnName = kbnName;
    }

    public String getStartToStringKintai() {
        return (dbFlag) ? start.toString() : "";
    }
    
    public String getStartToStringEdit() {
        return start.toString();
    }
    
    public void setStartToStringEdit(String start) {
        if (start != "")
            this.start = Time.valueOf(start);
        else
            this.start = Time.valueOf("00:00:00");
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
        if (end != "")
            this.end = Time.valueOf(end);
        else
            this.end = Time.valueOf("00:00:00");
    }
    
    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public Time getTotal() {
        if (dbFlag)
            return MathKintai.resultTotal(this.start, this.end, this.rest);
        else
            return total;
    }

    public void setTotal(Time total) {
        this.total = total;
    }
    
    public String getRestToString() {
        return (dbFlag) ? rest.toString() : "";
    }
    
    public void setRestToString(String rest) {
        if (rest != "")
            this.rest = Time.valueOf(rest);
        else
            this.rest = Time.valueOf("00:00:00");
    }

    public Time getRest() {
        return rest;
    }

    public void setRest(Time rest) {
        this.rest = rest;
    }

    public Time getOver() {
        if (dbFlag)
            return MathKintai.resultOver(this.start, this.end, this.rest);
        else
            return over;
    }

    public void setOver(Time over) {
        this.over = over;
    }

    public Time getReal() {
        if (dbFlag)
            return MathKintai.resultReal(this.start, this.end, this.rest, this.kbn_cd);
        else
            return real;
    }

    public void setReal(Time real) {
        this.real = real;
    }
}
