package javasign.com.dompetsehat.ui.activities.institusi.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 12/28/16.
 */

public class InstitusiConditionDialog extends DialogFragment {

  public interface OnFooterButtonListener {
    void onClick(boolean isAgree);
  }

  private OnFooterButtonListener listener;

  @Bind(R.id.webview) WebView webview;

  public static InstitusiConditionDialog newInstance(OnFooterButtonListener listener) {
    InstitusiConditionDialog dialog = new InstitusiConditionDialog();
    dialog.listener = listener;
    return dialog;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View dialogView =
        LayoutInflater.from(getContext()).inflate(R.layout.dialog_institusi_condition, null);
    ButterKnife.bind(this, dialogView);
    String path = "file:///android_asset/supported_files/klikmami_my_referral_condition.html";
    webview.loadUrl(path);

    AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

    return dialog;
  }

  @OnClick({ R.id.btn_disagree, R.id.btn_agree }) void onFooterBtnClick(View v) {
    if (listener == null) return;

    switch (v.getId()) {
      case R.id.btn_disagree:
        dismissAllowingStateLoss();
        listener.onClick(false);
        break;
      case R.id.btn_agree:
        dismissAllowingStateLoss();
        listener.onClick(true);
        break;
    }
  }

  public void showNow(FragmentManager fragmentManager) {
    this.show(fragmentManager, "condition-dialog");
  }
}
