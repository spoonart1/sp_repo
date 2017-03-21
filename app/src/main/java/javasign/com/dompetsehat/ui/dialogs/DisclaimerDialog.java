package javasign.com.dompetsehat.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by lafran on 9/15/16.
 */

public class DisclaimerDialog extends DialogFragment {

  @Bind(R.id.tv_top) TextView tv_top;
  @Bind(R.id.title) TextView tv_title;
  @Bind(R.id.tv_bot) TextView tv_bot;

  String topMessage;
  String footerMessage;
  String url;
  String title;

  public static DisclaimerDialog newInstance(String topMessage, String message){
    DisclaimerDialog dialog = new DisclaimerDialog();
    dialog.init(topMessage, message, "");

    return dialog;
  }

  public static DisclaimerDialog newInstance(String topMessage, String message, String url){
    DisclaimerDialog dialog = new DisclaimerDialog();
    dialog.init(topMessage, message, url);

    return dialog;
  }

  private void init(String topMessage, String message, String url) {
    this.topMessage = topMessage;
    this.footerMessage = message;
    this.url = url;
  }

  public void setTitle(String title){
    this.title = title;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.dialog_disclaimer, null);
    ButterKnife.bind(this, view);

    AlertDialog d = new AlertDialog.Builder(getContext())
        .setView(view)
        .create();

    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    if (!TextUtils.isEmpty(title)){
      tv_title.setVisibility(View.VISIBLE);
      tv_title.setText(Html.fromHtml(title));
      tv_top.setGravity(Gravity.LEFT);
    }

    tv_top.setText(Html.fromHtml(topMessage));
    if(!TextUtils.isEmpty(footerMessage))
      tv_bot.setText(Html.fromHtml(footerMessage));
    else
      tv_bot.setVisibility(View.GONE);

    if(!url.isEmpty()) {
      tv_bot.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          startActivity(new Intent(getActivity(), WebviewInstutisiActivity.class)
              .putExtra(WebviewInstutisiActivity.URL_KEY, url)
              .putExtra("from", WebviewInstutisiActivity.FLAG_TO_HIDE)
              .putExtra("accent-color", Helper.GREEN_DOMPET_COLOR)
          );
          dismissAllowingStateLoss();
        }
      });

    }

    return d;
  }

  @OnClick(R.id.btn_close) void onClose(){
    dismissAllowingStateLoss();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    dismissAllowingStateLoss();
  }
}
