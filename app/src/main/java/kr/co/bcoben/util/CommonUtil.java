package kr.co.bcoben.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.security.MessageDigest;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;

public class CommonUtil {
    private static final int FINISH_TIMER = 2000;
    private static boolean isFinish = false;
    private static Toast toast;

    // 앱 종료
    public static void finishApp(Activity act) {
        if (isFinish) {
            act.finishAffinity();
        } else {
            showToast(R.string.app_finish);
            isFinish = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFinish = false;
                }
            }, FINISH_TIMER);
        }
    }

    // Toast 출력
    public static void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(AppApplication.getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(int msgId) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(AppApplication.getContext(), msgId, Toast.LENGTH_SHORT);
        toast.show();
    }

    // 키보드 숨김
    public static void hideKeyboard(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = act.getCurrentFocus();
        if (view == null) {
            view = new View(act);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    // 키보드 출력
    public static void showKeyboard(Activity act, EditText edit) {
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edit, 0);
        }
    }

    // 사이즈 Converting
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    // 권한 체크
    public enum PermissionState { GRANT, DENY, ALWAYS_DENY }
    public static boolean requestPermission(Activity act, String[] permissionArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissionArray) {
                if (act.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(act, permissionArray,123);
                    return false;
                }
            }
        }
        return true;
    }
    public static  PermissionState resultRequestPermission(Activity act, String[] permissions, int[] grantResults) {
        boolean isGranted = false;
        for (int result : grantResults) {
            if (!(isGranted = (result == PackageManager.PERMISSION_GRANTED))) {
                break;
            }
        }

        if (grantResults.length == 0 || !isGranted) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED && ActivityCompat.shouldShowRequestPermissionRationale(act, permissions[i])) {
                    return PermissionState.DENY;
                }
            }
            return PermissionState.ALWAYS_DENY;
        } else {
            return PermissionState.GRANT;
        }
    }

    // 키해시
    public static void getKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KeyHash", keyHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 이미지 저장폴더 가져오기
    private static final File SAVE_PATH = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/BcoBen");
    public static File getSavePath() {
        if (!SAVE_PATH.exists()) {
            SAVE_PATH.mkdirs();
        }
        return SAVE_PATH;
    }

    // 앱 버전 가져오기
    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
