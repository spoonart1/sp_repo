package javasign.com.dompetsehat.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bastianbentra on 4/22/16.
 */

public class WrapContentViewPager extends ViewPager {

  private boolean enableSwipePage = false;

  public WrapContentViewPager(Context context) {
    super(context);
  }

  public WrapContentViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setEnableSwipePage(boolean enableSwipePage){
    this.enableSwipePage = enableSwipePage;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int height = 0;
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      int h = child.getMeasuredHeight();
      if (h > height) height = h;
    }

    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
