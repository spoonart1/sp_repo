package javasign.com.dompetsehat.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by bastianbentra on 4/22/16.
 */

public class UnSwapContentViewPager extends ViewPager {

  private boolean enableSwipePage = false;

  public UnSwapContentViewPager(Context context) {
    super(context);
  }

  public UnSwapContentViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setEnableSwipePage(boolean enableSwipePage){
    this.enableSwipePage = enableSwipePage;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return enableSwipePage && super.onInterceptTouchEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return enableSwipePage && super.onTouchEvent(event);
  }
}
