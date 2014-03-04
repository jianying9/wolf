package com.wolf.framework.utils;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http中request,response操作辅助类
 *
 * @author zoe
 */
public class HttpUtils {

    private HttpUtils() {
    }

    /**
     * 将JSON字符串输出到前台
     *
     * @param request
     * @param response
     * @param jsonStr
     */
    public static void toWrite(HttpServletRequest request, HttpServletResponse response, String jsonStr) {
        //获取跨域请求标志，并对输出内容做跨域处理
        String jsoncallback = request.getParameter("callback");
        if (jsoncallback == null || jsoncallback.equals("?")) {
            toWrite(response, jsonStr);
        } else {
            StringBuilder stringBuilder = new StringBuilder(jsoncallback.length() + jsonStr.length() + 2);
            stringBuilder.append(jsoncallback).append('(').append(jsonStr).append(')');
            toWrite(response, stringBuilder.toString());
        }
    }

    /**
     * 将JSON字符串输出到前台
     *
     * @param request
     * @param response
     * @param jsonStr
     */
    private static void toWrite(HttpServletResponse response, String jsonStr) {
        response.setContentType("application/x-javascript");
        response.setCharacterEncoding("utf-8");
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write(jsonStr);
            printWriter.flush();
        } catch (IOException e) {
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 输出CSS
     *
     * @param request
     * @param response      *
     * @param cssString
     */
    public static void toWirteCss(HttpServletRequest request, HttpServletResponse response, String cssString) {
        response.setContentType("text/css");
        response.setCharacterEncoding("utf-8");
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write(cssString);
            printWriter.flush();
        } catch (IOException e) {
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }
}
