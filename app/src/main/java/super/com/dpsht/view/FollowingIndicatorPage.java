package javasign.com.dompetsehat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mikepenz.iconics.view.IconicsTextView;
import javasign.com.dompetsehat.R;

/**
 * Created by bastianbentra on 8/24/16.
 */
public class FollowingIndicatorPage extends LinearLayout {

  public static final int FILL_NEXT = 0;
  public static final int FILL_CURRENT = 1;
  public static final int FILL_DONE = 2;

  private View indicator;
  private IconicsTextView icon;
  private TextView label;

  private int mCurrentColor;
  private int mStrokeColor;

  public FollowingIndicatorPage(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.following_indicator, this);
    init(context, attrs);
  }

  protected void init(Context context, AttributeSet attrs) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FollowingIndicatorPage);
    final String apps = "http://schemas.android.com/apk/res-auto";

    indicator = findViewById(R.id.indicator);
    icon = (IconicsTextView) findViewById(R.id.icon);
    icon.setVisibility(GONE);
    label = (TextView) findViewById(R.id.label);

    int indicator_fill_color = ta.getColor(R.styleable.FollowingIndicatorPage_fill_color,
        getResources().getColor(R.color.green_dompet_ori));

    int labelTextColor = ta.getColor(R.styleable.FollowingIndicatorPage_label_color,
        getResources().getColor(R.color.grey_800));

    int iconColor = Color.WHITE;
    String label_text = attrs.getAttributeValue(apps, "label_text");

    GradientDrawable drawable = (GradientDrawable) indicator.getBackground();
    drawable.setColor(indicator_fill_color);
    drawable.setStroke(1, indicator_fill_color);

    mCurrentColor = indicator_fill_color;
    mStrokeColor = mCurrentColor;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      indicator.setBackground(drawable);
    } else {
      indicator.setBackgroundDrawable(drawable);
    }

    label.setTextColor(labelTextColor);
    label.setText(label_text);
    icon.setTextColor(iconColor);

    int size = ta.getInt(R.styleable.FollowingIndicatorPage_label_size, 9);
    float fillSize = ta.getDimension(R.styleable.FollowingIndicatorPage_indicator_size, 17.f);
    label.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    indicator.getLayoutParams().height = (int) fillSize;
    indicator.getLayoutParams().width = (int) fillSize;
    indicator.requestLayout();

    this.requestLayout();
    ta.recycle();
  }

  public void setProgress(int progress) {
    GradientDrawable drawable = (GradientDrawable) indicator.getBackground();
    icon.setVisibility(GONE);

    switch (progress) {
      case FILL_NEXT:
        drawable.setColor(Color.WHITE);
        label.setTextColor(Color.LTGRAY);
        drawable.setStroke(1, mStrokeColor);
        break;
      case FILL_CURRENT:
        drawable.setColor(mCurrentColor);
        label.setTextColor(mCurrentColor);
        drawable.setStroke(1, mStrokeColor);
        break;
      case FILL_DONE:
        drawable.setColor(mCurrentColor);
        label.setTextColor(mCurrentColor);
        drawable.setStroke(1, mStrokeColor);
        icon.setVisibility(VISIBLE);
        break;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      indicator.setBackground(drawable);
    }
    else {
      indicator.setBackgroundDrawable(drawable);
    }
  }
}
