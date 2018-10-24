/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import data.KintaiData;
import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

/**
 *
 * @author 佐藤孝史
 * 
 * Math
 * 勤怠データに関わる計算クラス
 */
public class MathKintai {
    
    private static final int HOUR = 0;
    private static final int MINUTE = 1;
    
    private static Time mathTotal(Time start, Time end, Time rest) {
        
        LocalTime total;

        total = end.toLocalTime().minusHours(start.toLocalTime().get(ChronoField.HOUR_OF_DAY));
        total = total.minusHours(rest.toLocalTime().get(ChronoField.HOUR_OF_DAY));
        total = total.minusMinutes(start.toLocalTime().get(ChronoField.MINUTE_OF_HOUR));
        total = total.minusMinutes(rest.toLocalTime().get(ChronoField.MINUTE_OF_HOUR));
        
        return Time.valueOf(total.toString()+":00");
    }
    
    public static Time resultTotal(Time start, Time end, Time rest) {
        
        return mathTotal(start, end, rest);
    }
    
    public static Time resultOver(Time start, Time end, Time rest) {
        
        LocalTime total = mathTotal(start, end, rest).toLocalTime();
        Time over = null;
        
        if (total.get(ChronoField.HOUR_OF_DAY) >= 8) {
            over = Time.valueOf(total.minusHours(8)+":00");
        } else {
            over = new Time(Time.valueOf("00:00:00").getTime());
        }
        
        return over;
    }
    
    public static Time resultReal(Time start, Time end, Time start_default, Time end_default, Time rest, int kbn_cd) {
        
        Time real = null;
        
        // 有休
        if (kbn_cd == 4) {
            real = Time.valueOf(mathTotal(start, end, rest).toLocalTime().minusHours(8).toString()+":00");
        } else if (kbn_cd == 5) {
            
            if (start.toLocalTime().compareTo(start_default.toLocalTime().plusHours(4)) < 0) {
                real = mathTotal(start, end, rest);
            } else {
                real = mathTotal(Time.valueOf(start_default.toLocalTime().plusHours(4)+":00"), end, rest);
            }
        } else if (kbn_cd == 6) {
            
            if (end.toLocalTime().compareTo(end_default.toLocalTime().minusHours(4)) > 0) {
                real = mathTotal(start, end, rest);
            } else {
                real = mathTotal(start, Time.valueOf(end_default.toLocalTime().minusHours(4)+":00"), rest);
            }
        } else {
            real = mathTotal(start, end, rest);
        }
        
        return real;
    }
    
    public static Time resultLate(Time start, Time start_default) {
        
        int hhStart = Integer.parseInt(start.toString().substring(0, 2));
        int mmStart = Integer.parseInt(start.toString().substring(3, 5));
        int hhStart_default = Integer.parseInt(start_default.toString().substring(0, 2));
        int mmStart_default = Integer.parseInt(start_default.toString().substring(3, 5));
        
        int mStart = (hhStart*60+mmStart);
        int mStart_default = (hhStart_default*60+mmStart_default);
        
        Time late = null;
        if (mStart_default < mStart) {
            int hh = (mStart - mStart_default) / 60;
            int mm = (mStart - mStart_default) % 60;
            late = new Time(Time.valueOf(String.valueOf(hh)+":"+String.valueOf(mm)+":00").getTime());
        } else {
            late = new Time(Time.valueOf("00:00:00").getTime());
        }
        
        return late;
    }
    
    public static Time resultLeave(Time end, Time end_default) {
        
        int hhEnd = Integer.parseInt(end.toString().substring(0, 2));
        int mmEnd = Integer.parseInt(end.toString().substring(3, 5));
        int hhEnd_default = Integer.parseInt(end_default.toString().substring(0, 2));
        int mmEnd_default = Integer.parseInt(end_default.toString().substring(3, 5));
        
        int mEnd = (hhEnd*60+mmEnd);
        int mEnd_default = (hhEnd_default*60+mmEnd_default);
        
        Time leave = null;
        if (mEnd_default > mEnd) {
            int hh = (mEnd_default - mEnd) / 60;
            int mm = (mEnd_default - mEnd) % 60;
            leave = new Time(Time.valueOf(String.valueOf(hh)+":"+String.valueOf(mm)+":00").getTime());
        } else {
            leave = new Time(Time.valueOf("00:00:00").getTime());
        }
        
        return leave;
    }
    
    public static double resultSumTotal(ArrayList<KintaiData> kintaiDataList) {
        
        double sumTime = 0.0;
        
        for (KintaiData kintaiData: kintaiDataList) {
            
            Time total = kintaiData.getTotal();
            if (total != null) {
                sumTime += (double)total.toLocalTime().get(ChronoField.HOUR_OF_DAY);
                sumTime += (double)total.toLocalTime().get(ChronoField.MINUTE_OF_HOUR)/60.0;
            }
        }
        
        return sumTime;
    }
    
    public static double resultSumOver(ArrayList<KintaiData> kintaiDataList) {
        
        double sumTime = 0.0;
        
        for (KintaiData kintaiData: kintaiDataList) {
            
            Time over = kintaiData.getOver();
            if (over != null) {
                sumTime += (double)over.toLocalTime().get(ChronoField.HOUR_OF_DAY);
                sumTime += (double)over.toLocalTime().get(ChronoField.MINUTE_OF_HOUR)/60.0;
            }
        }
        
        return sumTime;
    }
    
    public static double resultSumReal(ArrayList<KintaiData> kintaiDataList) {
        
        double sumTime = 0.0;
        
        for (KintaiData kintaiData: kintaiDataList) {
            
            Time real = kintaiData.getReal();
            if (real != null) {
                sumTime += (double)real.toLocalTime().get(ChronoField.HOUR_OF_DAY);
                sumTime += (double)real.toLocalTime().get(ChronoField.MINUTE_OF_HOUR)/60.0;
            }
        }
        
        return sumTime;
    }
    
    public static double resultSumLate(ArrayList<KintaiData> kintaiDataList) {
        
        double sumTime = 0.0;
        
        for (KintaiData kintaiData: kintaiDataList) {
            
            Time late = kintaiData.getLate();
            if (late != null) {
                sumTime += (double)late.toLocalTime().get(ChronoField.HOUR_OF_DAY);
                sumTime += (double)late.toLocalTime().get(ChronoField.MINUTE_OF_HOUR)/60.0;
            }
        }
        
        return sumTime;
    }
    
    public static double resultSumLeave(ArrayList<KintaiData> kintaiDataList) {
        
        double sumTime = 0.0;
        
        for (KintaiData kintaiData: kintaiDataList) {
            
            Time leave = kintaiData.getLeave();
            if (leave != null) {
                sumTime += (double)leave.toLocalTime().get(ChronoField.HOUR_OF_DAY);
                sumTime += (double)leave.toLocalTime().get(ChronoField.MINUTE_OF_HOUR)/60.0;
            }
        }
        
        return sumTime;
    }
}
