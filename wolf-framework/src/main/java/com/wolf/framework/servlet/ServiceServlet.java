package com.wolf.framework.servlet;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.session.Session;
import com.wolf.framework.utils.HttpUtils;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalMessageContextImpl;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
@WebServlet(name = "service.io", loadOnStartup = 1)
public class ServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 2005719241528799747L;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final Map<String, Session> sessionMap = new HashMap<String, Session>(4096, 1);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result;
        String act = request.getParameter("act");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
        if (serviceWorker == null) {
            this.logger.error("invalid act value:{}", act);
            //无效的act
            result = "{\"flag\":\"INVALID\",\"error\":\"act not exist\"}";
        } else {
            Enumeration<String> names = request.getParameterNames();
            Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
            String name;
            String value;
            while (names.hasMoreElements()) {
                name = names.nextElement();
                value = request.getParameter(name);
                value = StringUtils.trim(value);
                parameterMap.put(name, value);
            }
            String sid = parameterMap.get("sid");
            Session session = this.sessionMap.get(sid);
            LocalMessageContextImpl localMessageContextImpl = new LocalMessageContextImpl(session, act, parameterMap);
            serviceWorker.doWork(localMessageContextImpl);
            result = localMessageContextImpl.getResponseMessage();
        }
        HttpUtils.toWrite(request, response, result);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "service.io";
    }
}
