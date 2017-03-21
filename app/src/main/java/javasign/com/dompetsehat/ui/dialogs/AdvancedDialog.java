package javasign.com.dompetsehat.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by lafran on 9/29/16.
 */

public class AdvancedDialog extends DialogFragment {

  public interface AdvancedDialogDelegate {
    void onNext(int identifier);
  }

  private ABuilder builder;
  private Intent intentTarget;
  private int identifier;
  private String tagFragmentTarget;
  private AdvancedDialog.AdvancedDialogDelegate delegate;

  public static AdvancedDialog newInstance(ABuilder builder, Intent intentTarget) {
    AdvancedDialog h = new AdvancedDialog();
    h.init(builder, intentTarget);
    return h;
  }

  protected void init(ABuilder builder, Intent intentTarget) {
    this.intentTarget = intentTarget;
    this.builder = builder;
  }

  public void setDelegate(AdvancedDialog.AdvancedDialogDelegate delegate) {
    this.delegate = delegate;
  }

  public void setIdentifier(int identifier){
    this.identifier = identifier;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog d = null;
    if (builder != null) {
      d = new AlertDialog.Builder(getContext()).setView(builder.rootview).create();
      ButterKnife.bind(this, builder.rootview);
    }

    GradientDrawable drawable = (GradientDrawable) builder.rootview.getBackground();
    drawable.setColorFilter(builder.dialogBackgroundColor, PorterDuff.Mode.SRC_ATOP);

    if(builder.isRounded) {
      d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    else {
      d.getWindow().setBackgroundDrawable(new ColorDrawable(builder.dialogBackgroundColor));
    }

    return d;
  }

  @OnClick({R.id.cancel, R.id.btn_close}) void cancel() {
    dismissAllowingStateLoss();
  }

  @OnClick(R.id.ok) void onNext() {
    if (delegate == null) return;

    delegate.onNext(identifier);
    navigateTo(tagFragmentTarget);
    dismissAllowingStateLoss();
  }

  protected void navigateTo(String tagFragmentTarget) {
    if(intentTarget == null) return;

    Intent i = intentTarget;
    //i.putExtra("dummy", tagFragmentTarget);
    if(!intentTarget.hasExtra("keephistory")) {
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    startActivity(i);
    getActivity().finish();
  }

  public static class ABuilder {

    int titleColor = Color.BLACK;
    int descriptionTextColor = Color.BLACK;
    int dialogBackgroundColor = Color.WHITE;
    int buttonTextColor = Helper.GREEN_DOMPET_COLOR;
    int buttonBackgroundColor = Color.WHITE;
    String mTitle;
    View rootview;
    ViewGroup container;
    Context mContext;
    boolean mCloseButton = false;
    boolean mFooterButton = true;
    boolean isRounded = true;
    AdvancedDialogDelegate dialogDelegate;

    public ABuilder(Context context, String title, int titleColor) {
      mTitle = title;
      mContext = context;
      rootview = View.inflate(context, R.layout.dialog_advanced, null);
      this.titleColor = titleColor;
      container = ButterKnife.findById(rootview, R.id.ll_content);

      TextView tv_title = ButterKnife.findById(rootview, R.id.title);
      tv_title.setText(title);
      tv_title.setTextColor(titleColor);
    }

    public ABuilder withBackgroundColor(int dialogBackgroundColor){
      this.dialogBackgroundColor = dialogBackgroundColor;
      return this;
    }

    public ABuilder withCustomButtonStyle(int buttonTextColor, int buttonBackgroundColor){
      this.buttonTextColor = buttonTextColor;
      this.buttonBackgroundColor = buttonBackgroundColor;

      TextView cancel = ButterKnife.findById(rootview, R.id.cancel);
      TextView next = ButterKnife.findById(rootview, R.id.ok);
      View ll_footer_button = ButterKnife.findById(rootview, R.id.ll_footer_button);
      cancel.setTextColor(buttonTextColor);
      next.setTextColor(buttonTextColor);
      ll_footer_button.setBackgroundColor(buttonBackgroundColor);
      return this;
    }

    public ABuilder withTitleBold(boolean bold){
      TextView tv_title = ButterKnife.findById(rootview, R.id.title);
      if(bold) {
        tv_title.setTypeface(null, Typeface.BOLD);
      }
      return this;
    }

    public ABuilder withRoundedCorner(boolean isRounded){
      this.isRounded = isRounded;
      return this;
    }

    public ABuilder withCloseButton(boolean show){
      this.mCloseButton = show;
      View v = ButterKnife.findById(rootview, R.id.fl_button);
      v.setVisibility(show ? View.VISIBLE : View.GONE);
      return this;
    }

    public ABuilder withFooterButton(boolean show){
      this.mFooterButton = show;
      View v = ButterKnife.findById(rootview, R.id.ll_footer_button);
      v.setVisibility(show ? View.VISIBLE : View.GONE);
      return this;
    }

    public ABuilder withSingleFooterButton(String buttonLabel){
      this.mFooterButton = true;
      ButterKnife.findById(rootview, R.id.ll_footer_button).setVisibility(View.VISIBLE);
      ButterKnife.findById(rootview, R.id.cancel).setVisibility(View.GONE);
      AppCompatTextView next = ButterKnife.findById(rootview, R.id.ok);
      next.setText(buttonLabel);
      return this;
    }

    public ABuilder withCustomDescriptionTextColor(int descriptionTextColor){
      this.descriptionTextColor = descriptionTextColor;
      return this;
    }

    public ABuilder withDelegate(AdvancedDialogDelegate dialogDelegate){
      this.dialogDelegate = dialogDelegate;
      return this;
    }

    public ABuilder addText(String text, int textColor) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setText(Html.fromHtml(text));
      textView.setTextColor(textColor);
      attachToContainer(textView);
      return this;
    }

    public ABuilder addText(String text, int textColor, boolean bold) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setText(Html.fromHtml(text));
      textView.setTextColor(textColor);

      if(bold)
        textView.setTypeface(null, Typeface.BOLD);

      attachToContainer(textView);
      return this;
    }

    public ABuilder addText(String text, int textSize, int typedValueUnit) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setText(Html.fromHtml(text));
      textView.setTextSize(typedValueUnit, textSize);
      textView.setTextColor(descriptionTextColor);
      attachToContainer(textView);
      return this;
    }

    public ABuilder addText(String text) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setText(Html.fromHtml(text));
      textView.setTextColor(descriptionTextColor);
      attachToContainer(textView);
      return this;
    }

    public ABuilder addText(int gravity, String text) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setGravity(gravity);
      textView.setText(Html.fromHtml(text));
      textView.setTextColor(descriptionTextColor);
      attachToContainer(textView);
      return this;
    }

    public ABuilder addText(int gravity, String text, boolean bold) {
      TextView textView = (TextView) View.inflate(mContext, R.layout.advanced_dialog_textview, null)
          .findViewById(R.id.textview);

      textView.setGravity(gravity);
      textView.setText(Html.fromHtml(text));
      textView.setTextColor(descriptionTextColor);

      if(bold)
        textView.setTypeface(null, Typeface.BOLD);

      attachToContainer(textView);
      return this;
    }

    public ABuilder addImage(@DrawableRes int resId, @Nullable Integer tintColor, int width,
        int height) {
      ImageView imageView =
          (ImageView) View.inflate(mContext, R.layout.advanced_dialog_imageview, null)
              .findViewById(R.id.imageView);

      ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
      imageView.setImageResource(resId);
      imageView.setLayoutParams(params);

      if (tintColor != null) imageView.setColorFilter(tintColor);

      attachToContainer(imageView);
      return this;
    }

    public ABuilder addSpace(int width, int height) {
      View v = new View(mContext);
      v.setBackgroundColor(mContext.getResources().getColor(R.color.grey_200));

      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
      int marginVertical = mContext.getResources().getDimensionPixelSize(R.dimen.padding_root_layout);
      int marginHorizontal = mContext.getResources().getDimensionPixelSize(R.dimen.padding_root_layout);
      layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);

      v.setLayoutParams(layoutParams);
      attachToContainer(v);
      return this;
    }

    public ABuilder addSpace(int size) {
      View v = new View(mContext);
      v.setBackgroundColor(mContext.getResources().getColor(R.color.transparant));

      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);

      v.setLayoutParams(layoutParams);
      attachToContainer(v);
      return this;
    }

    private void attachToContainer(View view) {
      container.addView(view);
    }
  }
}
