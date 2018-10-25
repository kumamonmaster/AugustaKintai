/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import database.DBController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import util.Log;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@SessionScoped
public class KbnData {
    
    ArrayList<String> kbnList = null;
    
    
    public KbnData() {
        
        kbnList = new ArrayList<String>();
    }

    public ArrayList<String> getKbnList() {
        return kbnList;
    }

    public void setKbnList(ArrayList<String> kbnList) {
        this.kbnList = kbnList;
    }
}
