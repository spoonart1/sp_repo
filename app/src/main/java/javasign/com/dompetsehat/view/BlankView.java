package javasign.com.dompetsehat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import com.wang.avi.AVLoadingIndicatorView;
import javasign.com.dompetsehat.R;
import toan.android.floatingactionmenu.FloatingActionButton;

/**
 * Created by lafran on 9/13/16.
 */

public class BlankView {

  final String DEFAULT_TEXT = "";
  final long DEFAULT_DELAY = 300;

  String iconCode;
  String desc;
  Context context;
  long currentDelay = DEFAULT_DELAY;

  @Bind(R.id.blankview) View blankView;
  @Bind(R.id.icon) IconicsTextView icon;
  @Bind(R.id.tv_desc) TextView tv_desc;
  @Bind(R.id.action_btn) TextView action_btn;
  @Bind(R.id.fab_add) FloatingActionButton fab_add_transaction;
  @Bind(R.id.progress_bar) AVLoadingIndicatorView progressBar;
  @Bind(R.id.aditional_btn) Button aditional_btn;

  public BlankView(View mRootView, String iconCode, String desc) {
    ButterKnife.bind(this, mRootView);
    this.iconCode = iconCode;
    this.desc = desc;
    icon.setText(iconCode);
    tv_desc.setText(desc);
    blankView.setAlpha(1.0f);
    progressBar.setVisibility(View.GONE);
  }

  public void setDesc(String desc) {
    this.desc = desc;
    tv_desc.setText(desc);
  }

  public void setDelayAnimation(long delayAnimation){
    this.currentDelay = delayAnimation;
  }

  public BlankView showFloatingButton(View.OnClickListener onClickListener){
    fab_add_transaction.setVisibility(View.VISIBLE);
    fab_add_transaction.setOnClickListener(onClickListener);
    return this;
  }

  public void show(boolean withDelay) {
    blankView.setAlpha(1.0f);
    blankView.setVisibility(View.VISIBLE);

    final long delay = withDelay ? currentDelay : 0;

    progressBar.setAlpha(1);
    ViewCompat.animate(progressBar)
        .alpha(0.0f)
        .setStartDelay(delay)
        .withEndAction(new Runnable() {
          @Override public void run() {
            progressBar.hide();
            progressBar.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
            icon.setText(iconCode);
            tv_desc.setText(desc);
          }
        })
        .start();
  }

  public void beginLoading(@Nullable String loadingText) {
    if(blankView.getVisibility() == View.GONE){
      blankView.setAlpha(1.0f);
      blankView.setVisibility(View.VISIBLE);
    }
    progressBar.setVisibility(View.VISIBLE);
    progressBar.smoothToShow();
    icon.setVisibility(View.GONE);

    if (TextUtils.isEmpty(loadingText)) loadingText = DEFAULT_TEXT;

    tv_desc.setText(loadingText);
  }

  public void dispose() {
    if (blankView != null) {

      blankView.setAlpha(1.0f);

      ViewCompat.animate(blankView)
          .alpha(0.0f)
          .setStartDelay(currentDelay)
          .withEndAction(new Runnable() {
            @Override public void run() {
              blankView.setVisibility(View.GONE);
              progressBar.setVisibility(View.GONE);
              icon.setVisibility(View.GONE);
              fab_add_transaction.setVisibility(View.GONE);
            }
          })
          .start();

    }
  }

  public void showActionButton(String label, View.OnClickListener listener) {
    action_btn.setVisibility(View.VISIBLE);
    action_btn.setText(Html.fromHtml("<font><u>" + label + "</u></font>"));
    //action_btn.setText(label);
    action_btn.setOnClickListener(listener);
  }

  public void hideActionButton() {
    action_btn.setVisibility(View.GONE);
    action_btn.setText("");
    action_btn.setOnClickListener(null);
  }


  public TextView getAction_btn() {
    action_btn.setVisibility(View.VISIBLE);
    return action_btn;
  }

  public Button getAditional_btn() {
    aditional_btn.setVisibility(View.VISIBLE);
    return aditional_btn;
  }
}
