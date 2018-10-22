/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@SessionScoped
public class KintaiYearMonth implements Serializable {
    
    private int ym;

    public int getYm() {
        return ym;
    }

    public void setYm(int ym) {
        this.ym = ym;
    }
    
}
