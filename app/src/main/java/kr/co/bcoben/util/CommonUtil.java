package kr.co.bcoben.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.component.AppFinishDialog;

import static android.app.Activity.RESULT_OK;

public class CommonUtil {
    private static Toast toast;

    // 앱 종료
    public static void finishApp(Activity act) {
        AppFinishDialog.builder(act).show();
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
            view.clearFocus();
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
        return requestPermission(act, permissionArray, 123);
    }
    public static boolean requestPermission(Activity act, String[] permissionArray, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissionArray) {
                if (act.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(act, permissionArray,requestCode);
                    return false;
                }
            }
        }
        return true;
    }
    // onRequestPermissionsResult에서 호출
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

    public static final int IMAGE_FROM_GALLERY = 3001;
    public static final int IMAGE_FROM_CAMERA = 3002;
    public static final int IMAGE_FROM_GAL_CAM = 3003;
    public static Uri photoUri;

    // 갤러리(앨범) 호출
    public static void getGalleryImage(Activity act) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        act.startActivityForResult(intent, IMAGE_FROM_GALLERY);
    }
    // 카메라 호출
    public static void getCameraImage(Activity act) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            File photoFile = new File(getImagePath(), System.currentTimeMillis() + ".jpg");
            photoUri = FileProvider.getUriForFile(act, act.getPackageName(), photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            act.startActivityForResult(takePictureIntent, IMAGE_FROM_CAMERA);
        }
    }
    // 갤러리, 카메라 선택
    public static void getFileChooserImage(Activity act) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            File photoFile = new File(getImagePath(), System.currentTimeMillis() + ".jpg");
            photoUri = FileProvider.getUriForFile(act, act.getPackageName(), photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        Intent chooserIntent = Intent.createChooser(i, "파일 선택");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent});
        act.startActivityForResult(chooserIntent, IMAGE_FROM_GAL_CAM);
    }
    // 이미지 가져온 후 처리
    public static Uri getImageResult(Activity act, int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg");
            switch (requestCode) {
                case IMAGE_FROM_CAMERA:
                    MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, new String[]{mimeType}, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("getImageResult", path);
                        }
                    });
                    return photoUri;
                case IMAGE_FROM_GALLERY:
                    photoUri = data.getData();
                    return photoUri;
                case IMAGE_FROM_GAL_CAM:
                    if (data != null && data.getData() != null) {
                        photoUri = data.getData();
                    } else {
                        MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, new String[]{mimeType}, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("getImageResult", path);
                            }
                        });
                    }
                    return photoUri;
            }
        }
        return null;
    }

    // 이미지 저장폴더 가져오기
    private static final File IMAGE_PATH = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/BcoBen");
    public static File getImagePath() {
        if (!IMAGE_PATH.exists()) {
            IMAGE_PATH.mkdirs();
        }
        return IMAGE_PATH;
    }
    public static File getCachePath() {
        return AppApplication.getContext().getCacheDir();
    }
    public static File getFilePath() {
        return AppApplication.getContext().getFilesDir();
    }

    // 앱 버전 가져오기
    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    // 통신 에러 처리
    public static void showErrorMsg(String error, Activity activity) {
        if (error != null) {
            String errorCode = error.toLowerCase();
            int errorCodeId = activity.getResources().getIdentifier(errorCode, "string", activity.getPackageName());
            showToast(errorCodeId);
        } else {
            showToast(R.string.toast_error_server);
        }
    }
}
