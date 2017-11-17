package javasign.com.dompetsehat.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsTextView;

import javasign.com.dompetsehat.R;

/**
 * Created by Spoonart on 12/7/2015.
 */
public class IconCategoryRounded extends RelativeLayout {

  private int icBgColor;
  private int icStrokeColor;
  private int icStrokeWidth = 0;
  private String iconCode;

  private ImageView iv_bgIcon;
  private IconicsTextView itv_icon;

  private GradientDrawable defaulDrawable;
  private ImageView iv_mask;

  private boolean fixSize = false;
  private int textSize = 0;

  public String hasCategory;
  public int id_category;

  public IconCategoryRounded(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.merge_color_icon, this);
    init(context, attrs);
  }

  protected void init(Context context, AttributeSet attrs) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconCategoryRounded);

    final String apps = "http://schemas.android.com/apk/res-auto";

    int bgColor = ta.getColor(R.styleable.IconCategoryRounded_ic_color,
        getResources().getColor(R.color.grey_800));
    int icTextColor = ta.getColor(R.styleable.IconCategoryRounded_ic_text_color,
        getResources().getColor(R.color.white));

    int strokeWidth = ta.getInt(R.styleable.IconCategoryRounded_ic_stroke_width, -1);
    int strokeColor = ta.getColor(R.styleable.IconCategoryRounded_ic_stroke_color, Color.TRANSPARENT);

    GradientDrawable circle = (GradientDrawable) getResources().getDrawable(R.drawable.shape_oval);
    defaulDrawable = circle;

    withIcBgColor(bgColor);
    withStrokeColor(strokeColor, strokeWidth);

    invalidateDrawable();

    ImageView iv_background = (ImageView) findViewById(R.id.iv_background);
    iv_mask = (ImageView) findViewById(R.id.iv_mask);
    iv_background.setImageDrawable(circle);
    iv_bgIcon = iv_background;

    int size = CircleShapeView.convertDpToPixel(
        ta.getInt(R.styleable.IconCategoryRounded_ic_view_size, 70), context);
    RelativeLayout container = (RelativeLayout) findViewById(R.id.rl_container);
    LayoutParams params = (LayoutParams) container.getLayoutParams();
    params.height = size;
    params.width = size;
    container.setLayoutParams(params);
    container.requestLayout();

    String ic_code = attrs.getAttributeValue(apps, "ic_code");
    String tag_category = attrs.getAttributeValue(apps, "tag_category");

    hasCategory = tag_category;

    String defaultIcCode = DSFont.Icon.dsf_general.getFormattedName();
    ic_code = ic_code == null ? defaultIcCode : ic_code;

    withIconCode(ic_code);
    int textSize = ta.getInt(R.styleable.IconCategoryRounded_ic_size, 24);
    IconicsTextView iconicsTextView = (IconicsTextView) findViewById(R.id.itv_icon_text);
    iconicsTextView.setTextColor(icTextColor);
    iconicsTextView.setText(getIconCode());
    iconicsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    itv_icon = iconicsTextView;
    this.textSize = textSize;
    this.requestLayout();
    ta.recycle();
  }

  private void invalidateDrawable(){
    if(defaulDrawable == null) return;

    defaulDrawable.setStroke(icStrokeWidth, icStrokeColor);
    defaulDrawable.setColor(getIcBgColor());
  }

  public int getIcBgColor() {
    return icBgColor;
  }

  public void withIcBgColor(int icBgColor) {
    this.icBgColor = icBgColor;
  }

  public void withStrokeColor(int icStrokeColor, int width){
    this.icStrokeColor = icStrokeColor;
    this.icStrokeWidth = width;
    invalidateDrawable();
  }

  public String getIconCode() {
    return iconCode;
  }

  public void withIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public void setBackgroundColorIcon(int color) {
    if (iv_bgIcon == null) return;

    icBgColor = color;
    withIcBgColor(color);

    defaulDrawable.setStroke(icStrokeWidth, icStrokeColor);
    defaulDrawable.setColor(getIcBgColor());
    iv_bgIcon.setImageDrawable(defaulDrawable);
  }

  public void setItemSelected() {
    if (iv_mask == null) return;
    iv_mask.setVisibility(VISIBLE);
  }

  public void resetItem() {
    if (iv_mask == null) return;
    iv_mask.setVisibility(GONE);
  }

  public void setIconCode(String iconCode) {
    if (itv_icon == null) return;

    this.iconCode = iconCode;
    itv_icon.setText(iconCode);
  }

  public void setIconTextColor(int color) {
    if (itv_icon == null) return;

    itv_icon.setTextColor(color);
  }

  public void setIconTextColor(ColorStateList color) {
    if (itv_icon == null) return;

    itv_icon.setTextColor(color);
  }

  public void setFixSize(boolean fixSize) {
    this.fixSize = fixSize;

    if (fixSize) {
      correctWidth(itv_icon, 55);
    } else {
      itv_icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
  }

  public void setTextSize(int textSize) {
    this.textSize = textSize;
    itv_icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
  }

  private void correctWidth(TextView textView, int desiredWidth) {
    Paint paint = new Paint();
    Rect bounds = new Rect();

    paint.setTypeface(textView.getTypeface());
    float textSize = textView.getTextSize();
    paint.setTextSize(textSize);
    String text = textView.getText().toString();
    paint.getTextBounds(text, 0, text.length(), bounds);

    while (bounds.width() > desiredWidth) {
      textSize--;
      paint.setTextSize(textSize);
      paint.getTextBounds(text, 0, text.length(), bounds);
    }

    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
  }

  public String getHasCategory() {
    return hasCategory;
  }

  public void setHasCategory(String hasCategory) {
    this.hasCategory = hasCategory;
  }

  public int getIDCategory() {
    return id_category;
  }

  public void setIDCategory(int id_category) {
    this.id_category = id_category;
  }
}
