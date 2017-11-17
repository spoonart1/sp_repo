package javasign.com.dompetsehat.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 11/9/16.
 */

public class DSOptionDialog extends DialogFragment {

  protected static final int DEFAULT_RES_DRAWABLE = R.drawable.ds_alert_positive_button;

  Builder mBuilder;

  private DSAdvancedDialog.SimpleOnButtonsClicked simpleOnButtonsClicked;
  private AdapterDialogOption adapterDialogOption;
  private OnCheckedItem onCheckedItem;

  public static DSOptionDialog create(Builder builder) {
    DSOptionDialog dsAdvancedDialog = new DSOptionDialog();
    dsAdvancedDialog.mBuilder = builder;
    return dsAdvancedDialog;
  }

  @Bind(R.id.btn_pannel) View btn_panel;
  @Bind(R.id.neutral) Button neutral;
  @Bind(R.id.positive) Button positive;
  @Bind(R.id.negative) Button negative;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  private void setSimpleOnButtonsClicked(
      @Nullable DSAdvancedDialog.SimpleOnButtonsClicked simpleOnButtonsClicked) {
    this.simpleOnButtonsClicked = simpleOnButtonsClicked;
  }

  private void setOnCheckedItem(OnCheckedItem onCheckedItem) {
    this.onCheckedItem = onCheckedItem;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_list_option, null);
    ButterKnife.bind(this, dialogView);
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
    if (mBuilder != null) {
      hideSomeViews();
      setproperties();
      setSimpleOnButtonsClicked(mBuilder.simpleOnButtonsClicked);
      setOnCheckedItem(mBuilder.onCheckedItem);

      adapterDialogOption = new AdapterDialogOption(mBuilder.options, onCheckedItem);
      recycleview.setAdapter(adapterDialogOption);
    }
    return alertDialog;
  }

  private void setproperties() {
    neutral.setText(mBuilder.neutralBtnText);
    negative.setText(mBuilder.negativeBtnText);
    positive.setText(mBuilder.positiveBtnText);

    neutral.setBackgroundResource(mBuilder.neutralBackground);
    negative.setBackgroundResource(mBuilder.negativeBackground);
    positive.setBackgroundResource(mBuilder.positiveBackground);

    neutral.setTextColor(mBuilder.neutralColor);
    negative.setTextColor(mBuilder.negativeColor);
    positive.setTextColor(mBuilder.positiveColor);
  }

  @OnClick({ R.id.negative, R.id.positive, R.id.neutral }) void onButtonClick(View button) {

    if (simpleOnButtonsClicked != null) {
      switch (button.getId()) {
        case R.id.neutral:
          simpleOnButtonsClicked.neutral(getDialog());
          break;
        case R.id.positive:
          simpleOnButtonsClicked.positive(getDialog(), getCheckedMap());
          break;
        case R.id.negative:
          simpleOnButtonsClicked.negative(getDialog());
          break;
      }
    }

    dismissAllowingStateLoss();
  }

  private LinkedHashMap<Integer, Boolean> getCheckedMap() {
    if (adapterDialogOption == null) return null;
    return adapterDialogOption.getCheckedMap();
  }

  private void hideSomeViews() {
    btn_panel.setVisibility(mBuilder.simpleOnButtonsClicked == null ? View.GONE : View.VISIBLE);
    neutral.setVisibility(TextUtils.isEmpty(mBuilder.neutralBtnText) ? View.GONE : View.VISIBLE);
    positive.setVisibility(TextUtils.isEmpty(mBuilder.positiveBtnText) ? View.GONE : View.VISIBLE);
    negative.setVisibility(TextUtils.isEmpty(mBuilder.negativeBtnText) ? View.GONE : View.VISIBLE);
  }

  public static class Builder {

    public static final int DEFAULT_TEXT_COLOR = 0x0099;

    DSAdvancedDialog.SimpleOnButtonsClicked simpleOnButtonsClicked;
    OnCheckedItem onCheckedItem;
    Context context;

    String negativeBtnText = "";
    String positiveBtnText = "";
    String neutralBtnText = "";

    int positiveBackground = DEFAULT_RES_DRAWABLE;
    int negativeBackground = DEFAULT_RES_DRAWABLE;
    int neutralBackground = DEFAULT_RES_DRAWABLE;

    int neutralColor = Color.WHITE;
    int negativeColor = Color.WHITE;
    int positiveColor = Color.WHITE;

    String[] options;

    public Builder(Context context, String[] options, @Nullable OnCheckedItem onCheckedItem) {
      this.context = context;
      this.options = options;
      this.onCheckedItem = onCheckedItem;
    }

    public Builder withButtons(String neutralBtnText, String negativeBtnText,
        String positiveBtnText, DSAdvancedDialog.SimpleOnButtonsClicked simpleOnButtonsClicked) {
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

    public DSOptionDialog create() {
      return DSOptionDialog.create(this);
    }
  }

  public interface OnCheckedItem {
    void onCheck(LinkedHashMap<Integer, Boolean> checkedMap);
  }

  public static class AdapterDialogOption
      extends RecyclerView.Adapter<AdapterDialogOption.OptHolder> {
    String[] options;
    ArrayList<OptModel> optModels;
    LinkedHashMap<Integer, Boolean> checkedMap;
    OnCheckedItem onCheckedItem;

    public AdapterDialogOption(String[] options, OnCheckedItem onCheckedItem) {
      this.options = options;
      this.onCheckedItem = onCheckedItem;
      init();
    }

    private void init() {
      optModels = new ArrayList<>();
      checkedMap = new LinkedHashMap<>();
      for (int i = 0; i < options.length; i++) {
        boolean ischeck = false;
        optModels.add(new OptModel(options[i], ischeck));
        checkedMap.put(i, ischeck);
      }
    }

    @Override
    public AdapterDialogOption.OptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.adapter_dialog_option, parent, false);
      return new OptHolder(view);
    }

    @Override public void onBindViewHolder(AdapterDialogOption.OptHolder holder, int position) {
      OptModel optModel = optModels.get(position);
      holder.itemView.setTag(position);
      holder.setCheched(optModel.isChecked);
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          int pos = (int) v.getTag();
          optModels.get(pos).isChecked = !optModels.get(pos).isChecked;
          checkedMap.put(pos, optModels.get(pos).isChecked);
          notifyDataSetChanged();

          if (onCheckedItem != null) onCheckedItem.onCheck(getCheckedMap());
        }
      });
    }

    @Override public int getItemCount() {
      return optModels.size();
    }

    public LinkedHashMap<Integer, Boolean> getCheckedMap() {
      return checkedMap;
    }

    class OptHolder extends RecyclerView.ViewHolder {
      @Bind(R.id.checkbox) IconicsTextView checkbox;

      public OptHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }

      public void setCheched(boolean check) {
        String iconCode = check ? CommunityMaterial.Icon.cmd_check_circle.getFormattedName()
            : CommunityMaterial.Icon.cmd_checkbox_blank_circle.getFormattedName();
        int color = check ? Color.parseColor("#30CC70") : Color.LTGRAY;
        checkbox.setText(iconCode);
        checkbox.setTextColor(color);
      }
    }

    class OptModel {
      boolean isChecked = false;
      String label;

      OptModel(String label, boolean isChecked) {
        this.label = label;
        this.isChecked = isChecked;
      }
    }
  }
}