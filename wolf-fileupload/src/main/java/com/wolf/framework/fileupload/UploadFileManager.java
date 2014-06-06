package com.wolf.framework.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jianying9
 */
public class UploadFileManager {

    public static final UploadFileManager MANAGER = new UploadFileManager();
    private String tempPath = null;
    private String mainPath = null;

    public void init(String rootPath) {
        File tempFileDir = new File(rootPath, "temp");
        if (tempFileDir.exists() == false) {
            tempFileDir.mkdirs();
        }
        this.tempPath = tempFileDir.getPath();
        File mainFileDir = new File(rootPath, "main");
        if (mainFileDir.exists() == false) {
            mainFileDir.mkdirs();
        }
        this.mainPath = mainFileDir.getPath();
    }

    public String getSuffix(String fileId) {
        String suffix = "";
        int pos = fileId.lastIndexOf('.');
        if (pos > 0 && pos < fileId.length() - 1) {
            suffix = fileId.substring(pos + 1);
        }
        return suffix;
    }

    public boolean isImageBySuffix(String suffix) {
        boolean result = false;
        if (suffix.isEmpty() == false) {
            suffix = suffix.toLowerCase();
            if (suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("gif")) {
                result = true;
            }
        }
        return result;
    }

    public boolean isImage(String fileId) {
        String suffix = this.getSuffix(fileId);
        return this.isImageBySuffix(suffix);
    }

    public File createTempFile(String suffix) {
        String fileId = UUID.randomUUID().toString();
        if (suffix.length() > 0) {
            StringBuilder nameBuilder = new StringBuilder(fileId.length() + 6);
            nameBuilder.append(fileId).append('.').append(suffix);
            fileId = nameBuilder.toString();
        }
        File tempFile = new File(this.tempPath, fileId);
        return tempFile;
    }
    
    public boolean isTempExist(String fileId) {
        File tempFile = new File(this.tempPath, fileId);
        return tempFile.exists();
    }

    public void clearTempFile() {
        File tempDir = new File(this.tempPath);
        if (tempDir.exists()) {
            tempDir.delete();
        }
        tempDir.mkdir();
    }

    public File moveTempToMain(String sid, String fileId) {
        File mainFile = null;
        File tempFile = new File(this.tempPath, fileId);
        if (tempFile.exists()) {
            File userDir = new File(this.mainPath, sid);
            if (userDir.exists() == false) {
                userDir.mkdir();
            }
            mainFile = new File(userDir.getPath(), fileId);
            try {
                FileUtils.moveFile(tempFile, mainFile);
            } catch (IOException ex) {
            }
        }
        return mainFile;
    }

    public File getMainFile(String sid, String fileId) {
        File mainFile = null;
        File userDir = new File(this.mainPath, sid);
        if (userDir.exists()) {
            mainFile = new File(userDir.getPath(), fileId);
            if (mainFile.exists() == false) {
                mainFile = null;
            }
        }
        return mainFile;
    }

    public void deleteMainFile(String sid, String fileId) {
        File userDir = new File(this.mainPath, sid);
        if (userDir.exists()) {
            File mainFile = new File(userDir.getPath(), fileId);
            if (mainFile.exists()) {
                mainFile.delete();
            }
        }
    }
}
