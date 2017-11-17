package javasign.com.dompetsehat.utils;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.lang.reflect.Field;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 12/29/16.
 */

public class BehaviorUtil {
  @Bind(R.id.appbar) AppBarLayout appBarLayout;
  @Bind(R.id.ll_header) View ll_header;
  @Bind(R.id.tv_persen_behavior) TextView tv_persen_behavior;
  @Bind(R.id.tv_total_behavior) TextView tv_total_behavior;
  @Bind(R.id.tv_title_left) TextView tv_title_left;
  @Bind(R.id.tv_title_right) TextView tv_title_right;

  private SwipeRefreshLayout swipeRefreshLayout;
  private TextView left;
  private TextView right;

  private String leftTitle;
  private String rightTitle;
  private String customSymbol = "%";
  private View rootView;
  private GestureDetector gesture;

  public BehaviorUtil(View rootView) {
    this.rootView = rootView;
    ButterKnife.bind(this, rootView);
    swipeRefreshLayout = ButterKnife.findById(rootView, R.id.refresh_layout);
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.setDistanceToTriggerSync(30);
    }
  }

  public static BehaviorUtil attach(View rootview) {
    BehaviorUtil bu = new BehaviorUtil(rootview);
    return bu;
  }

  public BehaviorUtil listenToTextView(TextView left, TextView right) {
    this.left = left;
    this.right = right;
    return this;
  }

  public BehaviorUtil setTitles(String leftTitle, String rightTitle) {
    this.leftTitle = leftTitle;
    this.rightTitle = rightTitle;
    return this;
  }

  public BehaviorUtil setCustomSymbol(String customSymbol) {
    this.customSymbol = customSymbol;
    return this;
  }

  public void register(int customTextColor) {
    if (left == null && right == null) return;

    if (customTextColor > -1) tv_persen_behavior.setTextColor(customTextColor);

    tv_title_left.setText(leftTitle);
    tv_title_right.setText(rightTitle);

    CharSequence leftTextValue = left.getText() + customSymbol;
    CharSequence rightTextValue = right.getText();

    tv_persen_behavior.setText(leftTextValue);
    tv_total_behavior.setText(rightTextValue);

    appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
      int maxHeight = appBarLayout.getTotalScrollRange();
      int yOffSet = Math.abs(verticalOffset);
      float alphaValue = (float) yOffSet / maxHeight;
      if (alphaValue <= 0.6) alphaValue = 0;
      ll_header.setAlpha(alphaValue);
    });
  }

  public BehaviorUtil addSwipeGestureToThisScene(View viewToAttach) {
    if (viewToAttach != null) {

      if (gesture == null) {
        gesture = new GestureDetector(viewToAttach.getContext(),
            new GestureDetector.SimpleOnGestureListener() {

              @Override public boolean onDown(MotionEvent e) {
                return true;
              }

              @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                  float velocityY) {
                Log.i("SWIPE", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                  if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
                  if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                      && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("SWIPE", "Right to Left");
                  } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                      && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("SWIPE", "Left to Right");
                  }
                } catch (Exception e) {
                  // nothing
                }
                return super.onFling(e1, e2, velocityX, velocityY);
              }
            });
      }

      viewToAttach.setOnTouchListener((v, event) -> gesture.onTouchEvent(event));
    }
    return this;
  }
}
