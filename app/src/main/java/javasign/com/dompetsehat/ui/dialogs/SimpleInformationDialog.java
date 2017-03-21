package javasign.com.dompetsehat.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 9/15/16.
 */

public class SimpleInformationDialog extends DialogFragment {

  @Bind(R.id.title) TextView tv_title;
  @Bind(R.id.tv_desc) TextView tv_desc;

  String title;
  String message;
  int gravitity = Gravity.CENTER;

  public static SimpleInformationDialog newInstance(String title, String message){
    SimpleInformationDialog dialog = new SimpleInformationDialog();
    dialog.init(title, message);

    return dialog;
  }

  private void init(String title, String message) {
    this.title = title;
    this.message = message;
  }

  public void setGravitity(int gravity){
    this.gravitity = gravity;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.dialog_simple_information, null);
    ButterKnife.bind(this, view);

    AlertDialog d = new AlertDialog.Builder(getContext())
        .setView(view)
        .create();

    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    tv_title.setText(Html.fromHtml(title));
    tv_title.setGravity(gravitity);
    tv_desc.setText(Html.fromHtml(message));

    return d;
  }

  @OnClick(R.id.btn_close) void onClose(){
    dismissAllowingStateLoss();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    dismissAllowingStateLoss();
  }
}
