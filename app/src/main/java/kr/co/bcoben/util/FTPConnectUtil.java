package kr.co.bcoben.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.AppUpdateDialog;
import kr.co.bcoben.model.PlanDataList;

import static kr.co.bcoben.util.CommonUtil.getCachePath;
import static kr.co.bcoben.util.CommonUtil.getFilePath;

public class FTPConnectUtil {
    private final String TAG = "Connect FTP";
    private final String HOST = "211.218.126.222";
    private final int PORT = 21;
    private final String USER_NAME = "bcobenftp";
    private final String PASSWORD = "ftp_123!@#";

    private FTPClient client;

    private static class LazyHolder {
        public static final FTPConnectUtil INSTANCE = new FTPConnectUtil();
    }
    public static FTPConnectUtil getInstance() {
        return LazyHolder.INSTANCE;
    }
    private FTPConnectUtil() {}

    private boolean ftpConnect() {
        try {
            if (client == null) {
                client = new FTPClient();
                client.setControlEncoding("UTF-8");
                client.connect(HOST, PORT);

                if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
                    if (client.login(USER_NAME, PASSWORD)) {
                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        client.enterLocalPassiveMode();
                        return true;
                    }
                } else {
                    client.disconnect();
                }
            } else {
                return client.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void ftpDisconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
                client = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap ftpImageBitmap(String path) {
        Bitmap bitmap = null;
        if (ftpConnect()) {
            try {
                if (path != null && !path.equals("")) {
                    BufferedInputStream is = new BufferedInputStream(client.retrieveFileStream(path));
                    bitmap = BitmapFactory.decodeStream(is);

                    is.close();
                    client.completePendingCommand();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpDisconnect();
        }
        return bitmap;
    }

    public List<Bitmap> ftpImageBitmap(List<String> pathList) {
        List<Bitmap> bitmapList = new ArrayList<>();
        if (ftpConnect()) {
            try {
                for (String path : pathList) {
                    if (path != null && !path.equals("")) {
                        String folder = path.substring(0, path.lastIndexOf("/"));
                        String name = path.substring(path.lastIndexOf("/") + 1);
                        boolean exist = false;
                        for (int i = 0; i < 5; i++) {
                            for (FTPFile f : client.listFiles(folder)) {
                                if (f.getName().equals(name)) {
                                    exist = true;
                                    break;
                                }
                            }
                            if (exist) {
                                break;
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        BufferedInputStream is = new BufferedInputStream(client.retrieveFileStream(path));
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmapList.add(bitmap);

                        is.close();
                        client.completePendingCommand();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpDisconnect();
        }
        return bitmapList;
    }

    public boolean ftpPlanDownload(int position, PlanDataList.PlanData planData, @Nullable DrawingsListAdapter adapter) {
        boolean result = false;
        if (ftpConnect()) {
            try {
                long size = ftpFileSize(planData.getPlan_img());

                int index = planData.getPlan_img_file().lastIndexOf("/") + 1;
                String pathname = planData.getPlan_img_file().substring(0, index);
                String filename = planData.getPlan_img_file().substring(index);
                File folder = new File(pathname);
                if (!folder.exists()) {
                    folder.mkdir();
                }

                FileOutputStream output = new FileOutputStream(new File(pathname, filename));
                InputStream input = client.retrieveFileStream(planData.getPlan_img());
                byte[] data = new byte[4096];

                double download = 0;
                int len;
                if (adapter != null) {
                    adapter.setData(position, planData, DrawingsListAdapter.START_DOWNLOAD);
                }
                while ((len = input.read(data)) != -1) {
                    output.write(data, 0, len);
                    if (adapter != null) {
                        download += len;
                        int percent = (int) (download / size * 100);
                        planData.setDownPercent(percent);
                        adapter.setData(position, planData, DrawingsListAdapter.DOWNLOADING);
                    }
                }
                output.close();
                input.close();
                result = client.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpDisconnect();
        }
        return result;
    }

    public boolean ftpApkDownload(String path, AppUpdateDialog dialog) {
        boolean result = false;
        if (ftpConnect()) {
            try {
                long size = ftpFileSize(path);
                String filename = path.substring(path.lastIndexOf("/") + 1);

                File file = new File(getFilePath(), filename);
                boolean isCreate = file.createNewFile();
                Log.e(TAG, "isCreate : " + isCreate);
                FileOutputStream output = new FileOutputStream(file);

                InputStream input = client.retrieveFileStream(path);
                byte[] data = new byte[4096];

                double download = 0;
                int len;
                while ((len = input.read(data)) != -1) {
                    output.write(data, 0, len);
                    if (dialog != null) {
                        download += len;
                        int percent = (int) (download / size * 100);
                        dialog.setDownloadProgress(percent);
                    }
                }

                output.close();
                input.close();
                result = client.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpDisconnect();
        }
        return result;
    }

    public List<File> ftpFileTempDownload(List<String> pathList) {
        List<File> fileList = new ArrayList<>();
        if (ftpConnect()) {
            try {
                for (String path : pathList) {
                    String filename = path.substring(path.lastIndexOf("/") + 1);
                    File saveFile = new File(getCachePath(), filename);
                    if (!saveFile.exists()) {
                        if (saveFile.createNewFile()) {
                            FileOutputStream output = new FileOutputStream(saveFile);
                            InputStream input = client.retrieveFileStream(path);
                            byte[] data = new byte[4096];

                            int len;
                            while ((len = input.read(data)) != -1) {
                                output.write(data, 0, len);
                            }
                            output.close();
                            input.close();
                            if (client.completePendingCommand()) {
                                fileList.add(saveFile);
                            }
                        }
                    } else {
                        fileList.add(saveFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpDisconnect();
        }
        return fileList;
    }

    private long ftpFileSize(String path) {
        long size = 0;
        try {
            int index = path.lastIndexOf("/") + 1;
            String pathname = path.substring(0, index);
            String filename = path.substring(index);

            for (FTPFile file : client.listFiles(pathname)) {
                if (file.getName().equals(filename)) {
                    size = file.getSize();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
