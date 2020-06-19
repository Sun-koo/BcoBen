package kr.co.bcoben.ftp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.util.CommonUtil;

import static kr.co.bcoben.util.CommonUtil.getFilePath;

public class ConnectFTP {
    private static ConnectFTP connectFTP;
    private final String TAG = "Connect FTP";
    private FTPClient ftpClient;
    private String host = "211.218.126.222";
    private int port = 21;
    private String userName = "bcobenftp";
    private String password = "ftp_123!@#";
    public String filePath = "/data/bcoben/1/App Upload/drawings_detail.png";

    public static ConnectFTP getInstance() {
        if (connectFTP == null) {
            connectFTP = new ConnectFTP();
        }
        return connectFTP;
    }

    private ConnectFTP() {
        ftpClient = new FTPClient();
    }

    public boolean ftpConnect() {
        boolean result = false;
        if (!ftpClient.isConnected()) {
            try {
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.connect(host, port);

                if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    result = ftpClient.login(userName, password);
                    ftpClient.enterLocalPassiveMode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Couldn't connect to host");
            }
        } else {
            result = true;
        }
        return result;
    }

    public boolean ftpDisconnect() {
        boolean result = false;
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
                result = true;
            } catch (Exception e) {
                Log.d(TAG, "Failed to disconnect with server");
            }
        } else {
            result = true;
        }
        return result;
    }

    public String ftpGetDirectory() {
        String directory = null;
        try {
            directory = ftpClient.printWorkingDirectory();
        } catch (Exception e) {
            Log.d(TAG, "Couldn't get current directory");
        }
        return directory;
    }

    public boolean ftpChangeDirectory(String directory) {
        try {
            ftpClient.changeWorkingDirectory(directory);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Couldn't change the directory");
        }
        return false;
    }

    public String[] ftpGetFileList(String directory) {
        String[] fileList = null;
        int i = 0;
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(directory);
            fileList = new String[ftpFiles.length];

            for (FTPFile file : ftpFiles) {
                String fileName = file.getName();

                if (file.isFile()) {
                    fileList[i] = "(File)" + fileName;
                } else {
                    fileList[i] = "(Directory)" + fileName;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public Bitmap ftpImageFile(String srcFilePath) {
        Bitmap bitmap = null;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream is = new BufferedInputStream(ftpClient.retrieveFileStream(srcFilePath));
            bitmap = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public List<Bitmap> ftpImageFile(List<String> filePathList) {
        List<Bitmap> list = new ArrayList<>();
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            for (String path : filePathList) {
                BufferedInputStream is = new BufferedInputStream(ftpClient.retrieveFileStream(path));
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap != null) {
                    list.add(bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> ftpDownloadFileList(List<String> srcFilePathList) {
        List<String> list = new ArrayList<>();
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            for (String srcFilePath : srcFilePathList) {
                String[] strArr = srcFilePath.split("/bcoben/");
                String lastPath = strArr[1].substring(strArr[1].lastIndexOf("/") + 1);
                String desFilePath = getFilePath().getAbsolutePath() + lastPath;
                list.add(desFilePath);

                FileOutputStream fos = new FileOutputStream(desFilePath);
                ftpClient.retrieveFile(srcFilePath, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ftpDownloadFile(String srcFilePath, String desFilePath) {
        boolean result = false;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            FileOutputStream fos = new FileOutputStream(desFilePath);
            result = ftpClient.retrieveFile(srcFilePath, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Download failed");
        }
        return result;
    }

    public boolean ftpUploadFile(String srcFilePath, String desFileName, String desDirectory) {
        boolean result = false;
        try {
            FileInputStream fis = new FileInputStream(srcFilePath);
            if (ftpChangeDirectory(desDirectory)) {
                result = ftpClient.storeFile(desFileName, fis);
            }
            fis.close();
        } catch (Exception e) {
            Log.d(TAG, "Couldn't upload the file");
        }
        return result;
    }
}
