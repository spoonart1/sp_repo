package javasign.com.dompetsehat.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;

/**
 * Created by bastianbentra on 8/31/16.
 */
public class HintDialog extends DialogFragment {

  private int pickMode = SessionManager.USER_PICK_NONE;
  private SessionManager sessionManager;
  private SwitchCompat switchCompat;
  private boolean mUserHasPick = false;

  @Bind(R.id.viewflipper) ViewFlipper viewFlipper;

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_hint, null);
    ButterKnife.bind(this, view);
    final Dialog dialog = new Dialog(getActivity());
    dialog.setContentView(view);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    ButterKnife.findById(view, R.id.btn_close).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
        mUserHasPick = false;
        switchCompat.setChecked(false);
      }
    });

    return dialog;
  }

  public void setSwitchCompat(SwitchCompat switchCompat) {
    this.switchCompat = switchCompat;
  }

  public void setPickMode(SessionManager sessionManager, int pickMode) {
    this.sessionManager = sessionManager;
    this.pickMode = pickMode;
  }

  public boolean isUserHasPick() {
    return mUserHasPick;
  }

  @OnClick(R.id.btn_done) void onDone() {
    mUserHasPick = true;
    sessionManager.setCurrentUserPickAppMode(this.pickMode);
    if (this.pickMode == SessionManager.USER_PICK_MY_REFERRAL) {
      Helper.trackThis(getActivity(), "User mengganti setelan aplikasi ke My Referral");
    } else {
      Helper.trackThis(getActivity(), "User mengganti setelan aplikasi ke My Fin");
    }
    Intent i = new Intent(getActivity(), NewMainActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);

    dismiss();
  }

  @OnClick(R.id.btn_back) void back() {
    viewFlipper.showPrevious();
  }

  @OnClick(R.id.btn_next) void next() {
    viewFlipper.showNext();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    if (!mUserHasPick) {
      switchCompat.setChecked(false);
    }
    super.onDismiss(dialog);
  }
}
