/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Time;

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
        int[] hm = {hh, mm};
        
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
        // 有休分はマイナスする
        if (kbn_cd == 4) {
            real = new Time(Time.valueOf("08:00:00").getTime());
        } else if (kbn_cd == 5 || kbn_cd == 6) {
            real = new Time(Time.valueOf(String.valueOf(hm[HOUR]-4)+":"+String.valueOf(hm[MINUTE])+":00").getTime());
        } else {
            real = new Time(Time.valueOf(String.valueOf(hm[HOUR])+":"+String.valueOf(hm[MINUTE])+":00").getTime());
        }
        
        return real;
    }
}
