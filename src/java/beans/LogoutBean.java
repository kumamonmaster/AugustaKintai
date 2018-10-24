/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import data.UserData;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author 佐藤孝史
 */
@ManagedBean
@RequestScoped
public class LogoutBean {
    
    @ManagedProperty(value="#{userData}")
    private UserData userData;

    /**
     * Creates a new instance of LogoutBean
     */
    public LogoutBean() {
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    
    public String logout() {
        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        return "login.xhtml?faces-redirect=true";
    }
}
