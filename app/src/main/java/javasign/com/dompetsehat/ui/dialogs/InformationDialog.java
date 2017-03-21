package javasign.com.dompetsehat.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 9/15/16.
 */

public class InformationDialog extends DialogFragment {

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.dialog_information, null);
    ButterKnife.bind(this, view);

    AlertDialog d = new AlertDialog.Builder(getContext())
        .setView(view)
        .create();

    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    return d;
  }

  @OnClick(R.id.btn_done) void onDone(View view){
    dismissAllowingStateLoss();
  }
}
