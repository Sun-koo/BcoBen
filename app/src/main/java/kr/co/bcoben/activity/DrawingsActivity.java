package kr.co.bcoben.activity;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener {

    private ArrayList<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    private PhotoViewAttacher photoViewAttacher;
    private float scale;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

    @Override
    protected void initView() {
        photoViewAttacher = new PhotoViewAttacher(dataBinding.ivDrawings);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
        photoViewAttacher.setMaximumScale(6);

        photoViewAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                dataBinding.layoutTable.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home: case R.id.btn_close:
                finish();
                break;

            case R.id.btn_save:
                //TODO For Test
                showDrawingsSelectDialog();
                break;

            case R.id.btn_zoom_in:
                scale = calculateScale(photoViewAttacher.getScale());
                scale = (scale + 0.5f) > 6 ? 6f : scale + 0.5f;
                photoViewAttacher.setScale(scale);

                showScaleView();
                break;

            case R.id.btn_zoom_out:
                scale = calculateScale(photoViewAttacher.getScale());
                scale = (scale - 0.5f) < 1 ? 1f : scale - 0.5f;
                photoViewAttacher.setScale(scale);

                showScaleView();
                break;

            // table
            case R.id.btn_select:
                break;

            case R.id.btn_delete:
                break;
        }
    }

    // 도면 선택 다이얼로그 출력
    private void showDrawingsSelectDialog() {
        final DrawingsSelectDialog dialog = new DrawingsSelectDialog(this);
        dialog.selectInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.selectResetInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.selectOtherListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //TODO Pinch Zoom 으로 바뀐 Scale을 0.5단위로 맞추기 위해 구현
    private float calculateScale(float scale) {
        if (scale > 1f && scale < 1.5f) {
            return 1.5f;
        } else if (scale > 1.5f && scale < 2f) {
            return 2f;
        } else if (scale > 2f && scale < 2.5f) {
            return 2.5f;
        } else if (scale > 2.5f && scale < 3f) {
            return 3f;
        } else if (scale > 3f && scale < 3.5f) {
            return 3.5f;
        } else if (scale > 3.5f && scale < 4f) {
            return 4f;
        } else if (scale > 4f && scale < 4.5f) {
            return 4.5f;
        } else if (scale > 4.5f && scale < 5f) {
            return 5f;
        } else if (scale > 5f && scale < 5.5f) {
            return 5.5f;
        } else if (scale > 5.5f && scale < 6f) {
            return 6f;
        } else {
            return scale;
        }
    }

    // 현재 줌 배수 출력
    private void showScaleView() {
        dataBinding.layoutScale.setVisibility(View.VISIBLE);
        dataBinding.txtScale.setText("x"+ scale +"배");

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dataBinding.layoutScale.startAnimation(fadeIn);
    }
}
