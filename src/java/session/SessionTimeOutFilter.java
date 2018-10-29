/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.faces.application.ViewExpiredException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author 佐藤孝史
 * 
 * セッション管理クラス
 */
//public class SessionHandler implements HttpSessionListener {
//    
//    /*
//    sessionCreated
//    セッションの生成を検知
//    */
//    @Override
//    public void sessionCreated(HttpSessionEvent event){
//        
//    }
//    
//    /*
//    sessionDestroyed
//    セッションの破棄を検知
//    */
//    @Override
//    public void sessionDestroyed(HttpSessionEvent event) {
//        System.out.println("破棄しました");
//        try {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
//        } catch (IOException ex) {
//            Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }
//    }
//    
//    public static boolean isSession() {
//        HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//        if (session == null)
//            return true;
//        
//        return false;
//    }
//}

public class SessionTimeOutFilter implements Filter {
    // This should be your default Home or Login page 
    // “login.seam” if you use Jboss Seam otherwise “login.jsf” / “login.xhtml” or whatever
    private String timeoutPage = "error.xhtml";
    
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        // 型をチェック
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            
            // キャスト
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            
            // ページ遷移先がloginではない
            if (isSessionControlRequiredForThisResource(httpServletRequest)) {
                
                // セッションが破棄されているか
                if (isSessionInvalid(httpServletRequest)) {
                    
                    // URLを設定
                    String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage(); 
                    // リダイレクト
                    httpServletResponse.sendRedirect(timeoutUrl);
                    
                    return ;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    
    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
        
        String requestPath = httpServletRequest.getRequestURI();
        boolean controlRequired = !requestPath.contains(getTimeoutPage());
        
        return controlRequired;
    }
    
    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null )
        && !httpServletRequest.isRequestedSessionIdValid();
        
        return sessionInValid;
    }
    
    public void destroy() {
    }
    
    public String getTimeoutPage() {
        return timeoutPage ;
    }
    
    public void setTimeoutPage(String timeoutPage) {
        this . timeoutPage = timeoutPage;
    }
    
}
