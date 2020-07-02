package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import java.io.File;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;
import kr.co.bcoben.util.FTPConnectUtil;

import static kr.co.bcoben.util.CommonUtil.getFilePath;

public class AppUpdateDialog extends Dialog {

    private Activity activity;
    private ConstraintLayout layoutUpdateContent;
    private ConstraintLayout layoutUpdateDownload;
    private ImageView btnUpdateClose;
    private TextView txtDownloadContent, txtDownloadPercent;
    private View viewDownloadProgress, viewDownloadRemain;

    public interface BtnClickListener {
        void onClick();
    }

    private AppUpdateDialog(final Activity activity, @Nullable final BtnClickListener btnCloseListener) {
        super(activity);
        setContentView(R.layout.dialog_app_update);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        this.activity = activity;
        layoutUpdateContent = findViewById(R.id.layout_update_content);
        layoutUpdateDownload = findViewById(R.id.layout_update_download);
        btnUpdateClose = findViewById(R.id.btn_update_close);
        txtDownloadContent = findViewById(R.id.txt_download_content);
        txtDownloadPercent = findViewById(R.id.txt_download_percent);
        viewDownloadProgress = findViewById(R.id.view_download_progress);
        viewDownloadRemain = findViewById(R.id.view_download_remain);

        findViewById(R.id.btn_update_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!activity.getPackageManager().canRequestPackageInstalls()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivityForResult(intent, 1000);
                        return;
                    }
                }

                final String apkUrl = AppApplication.getInstance().getUpdateData().getApk_url();
                if (apkUrl != null) {
                    btnUpdateClose.setVisibility(View.GONE);
                    layoutUpdateContent.setVisibility(View.GONE);
                    layoutUpdateDownload.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (FTPConnectUtil.getInstance().ftpApkDownload(apkUrl, AppUpdateDialog.this)) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                installApk(apkUrl);
                            }
                        }
                    }).start();
                }
            }
        });
        btnUpdateClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (btnCloseListener != null) {
                    btnCloseListener.onClick();
                }
            }
        });
    }

    public void setDownloadProgress(final int percent) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtDownloadPercent.setText(percent + "%");
                LinearLayout.LayoutParams progressParam = ((LinearLayout.LayoutParams) viewDownloadProgress.getLayoutParams());
                LinearLayout.LayoutParams remainParam = ((LinearLayout.LayoutParams) viewDownloadRemain.getLayoutParams());
                progressParam.weight = percent;
                remainParam.weight = 100 - percent;

                viewDownloadProgress.setLayoutParams(progressParam);
                viewDownloadRemain.setLayoutParams(remainParam);
            }
        });
    }

    private void installApk(final String apkUrl) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
                txtDownloadContent.setText(R.string.dialog_app_update_download_complete);
                String filename = apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
                Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName(), new File(getFilePath(), filename));

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                activity.startActivityForResult(intent, 1001);
            }
        });
    }

    public static Builder builder(Activity activity) {
        return new AppUpdateDialog.Builder(activity);
    }

    public static class Builder {
        private Activity activity;
        private BtnClickListener btnCloseListener;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public Builder setBtnCloseListener(BtnClickListener btnCloseListener) {
            this.btnCloseListener = btnCloseListener;
            return this;
        }
        public void show() {
            new AppUpdateDialog(activity, btnCloseListener).show();
        }
    }
}
