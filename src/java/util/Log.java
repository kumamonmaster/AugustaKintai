/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.logging.Logger;

/**
 *
 * @author 佐藤孝史
 */
public final class Log {

    public static Logger getLog() {
        StackTraceElement[] stackTraces = new Throwable().getStackTrace();
        String sourceClassName = stackTraces[1].getClassName();
        
        return Logger.getLogger(sourceClassName);
    }
}
