/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author 佐藤孝史
 */
public class Log {
    
    Logger logger;
    FileHandler fHandler;
    
    public Log(String className, String filePath) {
        logger = Logger.getLogger(className);
        
        try {
            fHandler = new FileHandler(filePath, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        fHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fHandler);
        logger.setLevel(Level.SEVERE);
        logger.severe("重大");
    }
    
    public void log(Level level, String text, Exception ex) {
        logger.log(level, text, ex);
    }
}
