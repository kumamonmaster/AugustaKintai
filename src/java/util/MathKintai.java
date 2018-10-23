/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import data.KintaiData;
import java.sql.Time;
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
    
    private static int[] mathTotal(Time start, Time end, Time rest) {
        
        int[] hm = new int[2];
                
        if (!start.equals(end)) {
            
            // 出勤時間のhhとmmをintへ
            int hhStart = Integer.parseInt(start.toString().substring(0, 2));
            int mmStart = Integer.parseInt(start.toString().substring(3, 5));
            // 退勤時間のhhとmmをintへ
            int hhEnd = Integer.parseInt(end.toString().substring(0, 2));
            int mmEnd = Integer.parseInt(end.toString().substring(3, 5));
            // 休憩時間のhhとmmをintへ
            int hhRest = Integer.parseInt(rest.toString().substring(0, 2));
            int mmRest = Integer.parseInt(rest.toString().substring(3, 5));

            // 出勤時間、退勤時間、休憩時間をそれぞれ分に直し、
            // 退勤時間 - 出勤時間 - 休憩時間 を計算し、総労働時間を算出する
            // / 60 で時間を % 60 で分を計算
            int hh = ((hhEnd*60+mmEnd) - (hhStart*60+mmStart) - (hhRest*60+mmRest)) / 60;
            int mm = ((hhEnd*60+mmEnd) - (hhStart*60+mmStart) - (hhRest*60+mmRest)) % 60;
            hm[0] = hh;
            hm[1] = mm;
        } else {
            hm[0] = 0;
            hm[1] = 0;
        }
        
        return hm;
    }
    
    public static Time resultTotal(Time start, Time end, Time rest) {
        
        int[] hm = mathTotal(start, end, rest);
        
        Time total = new Time(Time.valueOf(String.valueOf(hm[HOUR])+":"+String.valueOf(hm[MINUTE])+":00").getTime());
        
        return total;
    }
    
    public static Time resultOver(Time start, Time end, Time rest) {
        
        int[] hm = mathTotal(start, end, rest);
        
        Time over = null;
        if (hm[HOUR] >= 8) {
            over = new Time(Time.valueOf(String.valueOf(hm[HOUR]-8)+":"+String.valueOf(hm[MINUTE])+":00").getTime());
        } else {
            over = new Time(Time.valueOf("00:00:00").getTime());
        }
        
        return over;
    }
    
    public static Time resultReal(Time start, Time end, Time rest, int kbn_cd) {
        
        int[] hm = mathTotal(start, end, rest);
        
        Time real = null;
        if (hm[0] == 0 && hm[1] == 0) {
            
            real = new Time(Time.valueOf("00:00:00").getTime());
        } else {
            
            // 有休分はマイナスする
            if (kbn_cd == 4) {
                real = new Time(Time.valueOf(String.valueOf(hm[HOUR]-8)+":"+String.valueOf(hm[MINUTE])+":00").getTime());
            } else if (kbn_cd == 5 || kbn_cd == 6) {
                real = new Time(Time.valueOf(String.valueOf(hm[HOUR]-4)+":"+String.valueOf(hm[MINUTE])+":00").getTime());
            } else {
                real = new Time(Time.valueOf(String.valueOf(hm[HOUR])+":"+String.valueOf(hm[MINUTE])+":00").getTime());
            }
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
