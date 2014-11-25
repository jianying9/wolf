package com.wolf.framework.fileupload;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author jianying9
 */
@WebServlet(name = "upload.io", loadOnStartup = 2, urlPatterns = {"/upload.io"})
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = -2314654410966538905L;
    private final MimetypesFileTypeMap mtMap = new MimetypesFileTypeMap();

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String fileId = request.getParameter("fileId");
        String fileName = request.getParameter("fileName");
        String sid = request.getParameter("sid");
        String download = request.getParameter("download");
        if (fileId != null && fileId.isEmpty() == false && fileName != null && fileName.isEmpty() == false && sid != null && sid.isEmpty() == false) {
            //
            File uploadFile = UploadFileManager.MANAGER.getMainFile(sid, fileId);
            if (uploadFile != null) {
                //请求类型
                String dType = "inline";
                if(download != null && download.equals("true")) {
                    dType = "attachment";
                }
                //判断文件类型
                String mimeType = this.getMimeType(uploadFile);
                String suffix = UploadFileManager.MANAGER.getSuffix(fileId);
                if (UploadFileManager.MANAGER.isImageBySuffix(suffix)) {
                    //图片,获取图片宽度和高度
                    String targetHeightStr = request.getParameter("targetHeight");
                    int targetHeight = Integer.MAX_VALUE;
                    if (targetHeightStr != null) {
                        try {
                            targetHeight = Integer.parseInt(targetHeightStr);
                        } catch (NumberFormatException e) {
                        }
                    }
                    int targetWidth = Integer.MAX_VALUE;
                    String targetWidthStr = request.getParameter("targetWidth");
                    if (targetWidthStr != null) {
                        try {
                            targetWidth = Integer.parseInt(targetWidthStr);
                        } catch (NumberFormatException e) {
                        }
                    }
                    BufferedImage imageBuff = ImageIO.read(uploadFile);
                    //根据要求压缩图片
                    BufferedImage thumb = UploadFileManager.MANAGER.resize(imageBuff, targetHeight, targetWidth);
                    //输出
                    response.setContentType(mimeType);
                    response.setHeader("Content-Disposition", dType + "; filename=\"" + fileName + "\"");
                    ServletOutputStream op = response.getOutputStream();
                    ImageIO.write(thumb, suffix, op);
                    op.flush();
                    op.close();
                } else {
                    //其它类型文件
                    int bytes;
                    ServletOutputStream op = response.getOutputStream();
                    response.setContentType(mimeType);
//                    response.setContentLength((int) uploadFile.length());
                    response.setHeader("Content-Disposition", dType + "; filename=\"" + fileName + "\"");
                    byte[] bbuf = new byte[1024];
                    DataInputStream in = new DataInputStream(new FileInputStream(uploadFile));
                    while ((bytes = in.read(bbuf)) != -1) {
                        op.write(bbuf, 0, bytes);
                    }
                    op.flush();
                    op.close();
                    in.close();
                }
            }
        } else {
            String result = "{\"flag\":\"INVALID\",\"error\":\"no file info\"}";
            response.setContentType("application/x-javascript");
            response.setCharacterEncoding("utf-8");
            PrintWriter printWriter = null;
            try {
                printWriter = response.getWriter();
                printWriter.write(result);
                printWriter.flush();
            } catch (IOException e) {
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }
    }
    
    private String doEscapteFilter(final String value) {
        String result = "";
        if (!value.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(value.length() * 2);
            char cht;
            for (int index = 0; index < value.length(); index++) {
                cht = value.charAt(index);
                switch (cht) {
                    case '\"':
                        stringBuilder.append("\\\"");
                        break;
                    case '\\':
                        stringBuilder.append("\\\\");
                        break;
                    case '\b':
                        stringBuilder.append("\\b");
                        break;
                    case '\n':
                        stringBuilder.append("\\n");
                        break;
                    case '/':
                        stringBuilder.append("\\/");
                        break;
                    case '\f':
                        stringBuilder.append("\\f");
                        break;
                    case '\r':
                        stringBuilder.append("\\r");
                        break;
                    case '\t':
                        stringBuilder.append("\\t");
                        break;
                    default:
                        stringBuilder.append(cht);
                }
            }
            result = stringBuilder.toString();
        }
        return result;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        if (ServletFileUpload.isMultipartContent(request) == false) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        StringBuilder jsonBuilder = new StringBuilder(256);
        String redirectUrl = "";
        try {
            List<FileItem> items = uploadHandler.parseRequest(request);
            jsonBuilder.append("{\"files\":[");
            String suffix;
            String fileName;
            File file;
            boolean isImage;
            for (FileItem item : items) {
                if (item.isFormField() == false) {
                    //文件
                    fileName = item.getName();
                    fileName = this.doEscapteFilter(fileName);
                    suffix = UploadFileManager.MANAGER.getSuffix(fileName);
                    isImage = UploadFileManager.MANAGER.isImageBySuffix(suffix);
                    file = UploadFileManager.MANAGER.createTempFile(suffix);
                    item.write(file);
                    jsonBuilder.append('{');
                    jsonBuilder.append("\"fileId\":\"").append(file.getName()).append("\",");
                    jsonBuilder.append("\"name\":\"").append(fileName).append("\",");
                    jsonBuilder.append("\"size\":\"").append(item.getSize()).append("\",");
                    jsonBuilder.append("\"isImage\":\"").append(isImage).append("\"");
                    jsonBuilder.append("},");
                } else {
                    //判断是否需要重定向
                    if (item.getFieldName().equals("redirect")) {
                        redirectUrl = item.getString();
                    }
                }
            }
            if (items.isEmpty() == false) {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("]}");
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (redirectUrl.length() > 0) {
            //重将结果重定向
            String result = URLEncoder.encode(jsonBuilder.toString(), "utf-8");
            redirectUrl = redirectUrl.replaceFirst("%s", result);
            response.sendRedirect(redirectUrl);
        } else {
            //正常返回
            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            PrintWriter printWriter = null;
            try {
                printWriter = response.getWriter();
                printWriter.write(jsonBuilder.toString());
                printWriter.flush();
            } catch (IOException e) {
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }
    }

    private String getMimeType(File file) {
        String mimetype;
        String suffix = UploadFileManager.MANAGER.getSuffix(file.getName());
        if (suffix.equalsIgnoreCase("png")) {
            mimetype = "image/png";
        } else if (suffix.equalsIgnoreCase("jpg")) {
            mimetype = "image/jpg";
        } else if (suffix.equalsIgnoreCase("jpeg")) {
            mimetype = "image/jpeg";
        } else if (suffix.equalsIgnoreCase("gif")) {
            mimetype = "image/gif";
        } else {
            mimetype = this.mtMap.getContentType(file);
        }
        return mimetype;
    }
}
