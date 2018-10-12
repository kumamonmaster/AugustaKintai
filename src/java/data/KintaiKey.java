/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@SessionScoped
public class KintaiKey implements Serializable {
    
    String kintaiKey = null;

    public String getKintaiKey() {
        return kintaiKey;
    }

    public void setKintaiKey(String kintaiKey) {
        this.kintaiKey = kintaiKey;
    }
}
