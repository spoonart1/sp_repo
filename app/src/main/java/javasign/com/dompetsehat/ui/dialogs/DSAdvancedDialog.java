package javasign.com.dompetsehat.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.LinkedHashMap;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 11/9/16.
 */

public class DSAdvancedDialog extends DialogFragment {

  protected static final int DEFAULT_RES_DRAWABLE = R.drawable.ds_alert_positive_button;

  public interface OnButtonsClicked {
    void positive(Dialog dialog, CharSequence fromEditField);

    void positive(Dialog dialog, LinkedHashMap<Integer, Boolean> checkedMap);

    void negative(Dialog dialog);

    void neutral(Dialog dialog);
  }

  Builder mBuilder;

  private SimpleOnButtonsClicked simpleOnButtonsClicked;

  public static DSAdvancedDialog create(Builder builder) {
    DSAdvancedDialog dsAdvancedDialog = new DSAdvancedDialog();
    dsAdvancedDialog.mBuilder = builder;
    return dsAdvancedDialog;
  }

  @Bind(R.id.btn_close) View btn_close;
  @Bind(R.id.spacer) View spacer;
  @Bind(R.id.tv_title) AppCompatTextView tv_title;
  @Bind(R.id.icon) IconicsTextView icon;
  @Bind(R.id.avatar) RoundedImageView avatar;
  @Bind(R.id.textfield) AppCompatTextView textfield;
  @Bind(R.id.editField) AppCompatEditText editField;
  @Bind(R.id.btn_pannel) View btn_panel;
  @Bind(R.id.neutral) Button neutral;
  @Bind(R.id.positive) Button positive;
  @Bind(R.id.negative) Button negative;

  private void setSimpleOnButtonsClicked(@Nullable SimpleOnButtonsClicked simpleOnButtonsClicked) {
    this.simpleOnButtonsClicked = simpleOnButtonsClicked;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_client, null);
    ButterKnife.bind(this, dialogView);
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    if (mBuilder != null) {
      hideSomeViews();
      setproperties();
      setSimpleOnButtonsClicked(mBuilder.simpleOnButtonsClicked);
    }
    return alertDialog;
  }

  private void setproperties() {
    icon.setText(mBuilder.iconCode);
    icon.setTextColor(mBuilder.iconColor);
    icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, mBuilder.iconSize);

    tv_title.setText(mBuilder.title);
    textfield.setText(mBuilder.textfieldText);
    editField.setText(mBuilder.editfieldText);
    editField.setHint(mBuilder.editfieldHint);

    neutral.setText(mBuilder.neutralBtnText);
    negative.setText(mBuilder.negativeBtnText);
    positive.setText(mBuilder.positiveBtnText);

    neutral.setBackgroundResource(mBuilder.neutralBackground);
    negative.setBackgroundResource(mBuilder.negativeBackground);
    positive.setBackgroundResource(mBuilder.positiveBackground);

    neutral.setTextColor(mBuilder.neutralColor);
    negative.setTextColor(mBuilder.negativeColor);
    positive.setTextColor(mBuilder.positiveColor);

    avatar.setImageBitmap(mBuilder.avatar);
  }

  @OnClick({ R.id.negative, R.id.positive, R.id.neutral, R.id.btn_close }) void onButtonClick(
      View button) {

    if (simpleOnButtonsClicked != null) {
      switch (button.getId()) {
        case R.id.neutral:
          simpleOnButtonsClicked.neutral(getDialog());
          break;
        case R.id.positive:
          simpleOnButtonsClicked.positive(getDialog(), editField.getText());
          break;
        case R.id.negative:
          simpleOnButtonsClicked.negative(getDialog());
          break;
      }
    }

    dismissAllowingStateLoss();
  }

  private void hideSomeViews() {
    btn_close.setVisibility(!mBuilder.closeButton ? View.GONE : View.VISIBLE);
    spacer.setVisibility(!mBuilder.closeButton ? View.VISIBLE : View.GONE);
    tv_title.setVisibility(TextUtils.isEmpty(mBuilder.title) ? View.GONE : View.VISIBLE);
    icon.setVisibility(TextUtils.isEmpty(mBuilder.iconCode) ? View.GONE : View.VISIBLE);
    textfield.setVisibility(!mBuilder.textField ? View.GONE : View.VISIBLE);
    editField.setVisibility(!mBuilder.editField ? View.GONE : View.VISIBLE);
    btn_panel.setVisibility(mBuilder.simpleOnButtonsClicked == null ? View.GONE : View.VISIBLE);
    neutral.setVisibility(TextUtils.isEmpty(mBuilder.neutralBtnText) ? View.GONE : View.VISIBLE);
    positive.setVisibility(TextUtils.isEmpty(mBuilder.positiveBtnText) ? View.GONE : View.VISIBLE);
    negative.setVisibility(TextUtils.isEmpty(mBuilder.negativeBtnText) ? View.GONE : View.VISIBLE);
    avatar.setVisibility(mBuilder.userAvatar ? View.VISIBLE : View.GONE);
  }

  public static class Builder {

    public static final int DEFAULT_TEXT_COLOR = 0x0099;

    SimpleOnButtonsClicked simpleOnButtonsClicked;
    String title;
    Context context;

    int iconColor = 0;
    int iconSize = 24;
    String iconCode = "";
    Bitmap avatar;

    String negativeBtnText = "";
    String positiveBtnText = "";
    String neutralBtnText = "";

    boolean closeButton;
    boolean editField;
    boolean textField;
    boolean userAvatar;

    String editfieldHint = "";
    String editfieldText = "";
    String textfieldText = "";

    int positiveBackground = DEFAULT_RES_DRAWABLE;
    int negativeBackground = DEFAULT_RES_DRAWABLE;
    int neutralBackground = DEFAULT_RES_DRAWABLE;

    int neutralColor = Color.WHITE;
    int negativeColor = Color.WHITE;
    int positiveColor = Color.WHITE;

    public Builder(Context context) {
      this.context = context;
      this.avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.agent_empty);
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withCloseButton(boolean show) {
      this.closeButton = show;
      return this;
    }

    public Builder withIconCode(String iconCode, int iconColor, int iconSize) {
      this.iconCode = iconCode;
      this.iconColor = iconColor;
      this.iconSize = iconSize;
      return this;
    }

    public Builder withEditField(boolean show) {
      this.editField = show;
      return this;
    }

    public Builder withEditFieldText(String text, String hint) {
      this.editfieldText = text;
      this.editfieldHint = hint;
      return this;
    }

    public Builder withTextFieldText(String text) {
      this.textfieldText = text;
      return this;
    }

    public Builder withTextField(boolean show) {
      this.textField = show;
      return this;
    }

    public Builder withButtons(String neutralBtnText, String negativeBtnText,
        String positiveBtnText, SimpleOnButtonsClicked simpleOnButtonsClicked) {
      this.negativeBtnText = negativeBtnText;
      this.neutralBtnText = neutralBtnText;
      this.positiveBtnText = positiveBtnText;
      this.simpleOnButtonsClicked = simpleOnButtonsClicked;
      return this;
    }

    public Builder withCustomButtonBackground(int neutralRes, int negativeRes, int positiveRes) {
      if (neutralRes != 0) this.neutralBackground = neutralRes;
      if (negativeRes != 0) this.negativeBackground = negativeRes;
      if (positiveRes != 0) this.positiveBackground = positiveRes;

      return this;
    }

    public Builder withCustomButtonTextColor(int neutralColor, int negativeColor,
        int positiveColor) {
      if (neutralColor != DEFAULT_TEXT_COLOR) this.neutralColor = neutralColor;
      if (negativeColor != DEFAULT_TEXT_COLOR) this.negativeColor = negativeColor;
      if (positiveColor != DEFAULT_TEXT_COLOR) this.positiveColor = positiveColor;
      return this;
    }

    public Builder withAvatar(boolean userAvatar, @Nullable Bitmap defaultAvatar) {
      this.userAvatar = userAvatar;
      if (defaultAvatar != null) {
        this.avatar = defaultAvatar;
      }
      return this;
    }

    public DSAdvancedDialog create() {
      return DSAdvancedDialog.create(this);
    }
  }

  public static class SimpleOnButtonsClicked implements OnButtonsClicked {

    @Override public void positive(Dialog alertDialog, CharSequence fromEditField) {
      System.out.println("SimpleOnButtonsClicked.positive withText: " + fromEditField);
    }

    @Override public void positive(Dialog alertDialog, LinkedHashMap<Integer, Boolean> checkedMap) {
      System.out.println("SimpleOnButtonsClicked.positive");
    }

    @Override public void negative(Dialog alertDialog) {
      System.out.println("SimpleOnButtonsClicked.negative");
    }

    @Override public void neutral(Dialog alertDialog) {
      System.out.println("SimpleOnButtonsClicked.neutral");
    }
  }
}