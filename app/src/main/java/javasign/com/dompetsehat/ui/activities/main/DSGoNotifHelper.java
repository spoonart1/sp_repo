package javasign.com.dompetsehat.ui.activities.main;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.dialogs.DSAdvancedDialog;
import javasign.com.dompetsehat.ui.dialogs.DSOptionDialog;

/**
 * Created by lafran on 11/14/16.
 */

public class DSGoNotifHelper {

  private FragmentManager fragmentManager;
  private Context context;
  private DSOptionDialog.OnCheckedItem onCheckedItem;
  private DSAdvancedDialog.SimpleOnButtonsClicked simpleOnButtonsClicked;

  public DSGoNotifHelper(Context context) {
    this.context = context;
  }

  public DSGoNotifHelper withCheckedItemListener(DSOptionDialog.OnCheckedItem onCheckedItem) {
    this.onCheckedItem = onCheckedItem;
    return this;
  }

  public DSGoNotifHelper withSimpleButtonListener(
      DSAdvancedDialog.SimpleOnButtonsClicked simpleButtonListener) {
    this.simpleOnButtonsClicked = simpleButtonListener;
    return this;
  }

  public DSGoNotifHelper show(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
    showNotifDialog();
    return this;
  }

  private void showNotifDialog() {
    String sender = "John";
    String company = "Asuransi Jiwa Manulife ";
    DSAdvancedDialog dialog =
        new DSAdvancedDialog.Builder(context).setTitle(context.getString(R.string.announcement))
            .withAvatar(true, null)
            .withTextField(true)
            .withTextFieldText(
                context.getString(R.string.agent) + " " + sender + " " + context.getString(
                    R.string.from) + " " + company + context.getString(
                    R.string.confirmation_acception) + sender + "?")
            .withCustomButtonBackground(0, R.drawable.button_white,
                R.drawable.ds_alert_positive_button)
            .withCustomButtonTextColor(DSAdvancedDialog.Builder.DEFAULT_TEXT_COLOR, Color.BLACK,
                DSAdvancedDialog.Builder.DEFAULT_TEXT_COLOR)
            .withButtons("", context.getString(R.string.decline),
                context.getString(R.string.yes_i_knew),
                new DSAdvancedDialog.SimpleOnButtonsClicked() {
                  @Override public void positive(Dialog alertDialog, CharSequence fromEditField) {
                    showListSharedableData();
                  }
                })
            .create();

    dialog.show(this.fragmentManager, "ds-dialog");
  }

  private void showListSharedableData() {
    DSOptionDialog dsOptionDialog = new DSOptionDialog.Builder(context, new String[] {
        context.getString(R.string.expense), context.getString(R.string.income),
        context.getString(R.string.budget), context.getString(R.string.financial_plan),
        context.getString(R.string.porto_investment)
    }, onCheckedItem).withCustomButtonBackground(0, R.drawable.button_white,
        R.drawable.ds_alert_positive_button)
        .withCustomButtonTextColor(DSOptionDialog.Builder.DEFAULT_TEXT_COLOR, Color.BLACK,
            DSOptionDialog.Builder.DEFAULT_TEXT_COLOR)
        .withButtons("", context.getString(R.string.cancel), context.getString(R.string.accept),
            simpleOnButtonsClicked)
        .create();

    dsOptionDialog.show(fragmentManager, "ds-option");
  }
}
