/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.Date;
import java.sql.Time;
import util.Conversion;

/**
 *
 * @author 佐藤孝史
 */
public class KintaiData {
    
    private int id;
    private Date date;
    private Time start;
    private Time end;
    private double rest;
    private double total;
    private double over;
    private double real;
    private int kbnId;

    
    public KintaiData(Date date) {
        this.date = date;
        this.start = null;
        this.end = null;
        this.total = 0.0;
        this.rest = 0.0;
        this.over = 0.0;
        this.real = 0.0;
        this.kbnId = 0;
    }
    
    public void setKintaiData(Time start, Time end, double total, double rest, double over, double real, int kbnId) {
        this.start = start;
        this.end = end;
        this.total = total;
        this.rest = rest;
        this.over = over;
        this.real = real;
        this.kbnId = kbnId;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }
    
    public String getConversionDate() {
        
        return Conversion.conversionDateToString(date)+" "+Conversion.conversionDayOfWeek(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getKbnId() {
        return kbnId;
    }

    public void setKbnId(int kbnId) {
        this.kbnId = kbnId;
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
