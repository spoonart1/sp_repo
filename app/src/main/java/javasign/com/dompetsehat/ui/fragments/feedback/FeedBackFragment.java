package javasign.com.dompetsehat.ui.fragments.feedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Arrays;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.feedback.FeedbackInterface;
import javasign.com.dompetsehat.presenter.feedback.FeedbackPresenter;
import javasign.com.dompetsehat.ui.activities.feedback.ThankFullActivity;
import javasign.com.dompetsehat.ui.activities.help.HelpActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 1/17/17.
 */

public class FeedBackFragment extends Fragment implements FeedbackInterface {

  //@Bind(R.id.et_title) MaterialEditText et_title;
  @Bind(R.id.et_msg) EditText et_msg;
  @Bind(R.id.btn_send) Button btn_send;

  @Bind(R.id.rb_bug) RadioButton rb_bug;
  @Bind(R.id.rb_info) RadioButton rb_info;
  @Bind(R.id.rb_feature) RadioButton rb_feature;

  private RadioButton[] radioButtons;
  private RadioButton mCheckedBtn;
  @Inject FeedbackPresenter presenter;
  ProgressDialog progressDialog;

  String[] subjects = {"bug","information","feature"};

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_feedback, null);
    ButterKnife.bind(this, view);
    init();

    ((HelpActivity)getActivity()).getActivityComponent().inject(this);
    presenter.attachView(this);
    progressDialog = new ProgressDialog(getActivity());

    return view;
  }

  @OnClick(R.id.btn_send) void sendFeedback(View view) {
    String msg = et_msg.getText().toString();
    //String title = et_title.getText().toString();
    String subjek = "";
    if (mCheckedBtn != null) {
      int idx = Arrays.asList(radioButtons).indexOf(mCheckedBtn);
      if(idx>=0) {
        subjek = subjects[idx];
        Timber.e("subjek :: "+subjek);
      }
    }
    et_msg.setText("");
    //et_title.setText("");
    progressDialog.setMessage("Sedang mengirim ...");
    progressDialog.show();
    presenter.sendFeedBack(subjek,msg);
  }

  private void init(){
    Helper.trackThis(getActivity(), "User di halaman feedback/ulasan");
    Words.setButtonToListen(btn_send, et_msg);
    prepareButton();
  }

  private void prepareButton() {
    rb_bug.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ds_font.ttf"));
    rb_info.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ds_font.ttf"));
    rb_feature.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ds_font.ttf"));

    rb_bug.setText("\uE98A");
    rb_info.setText("\uE989");
    rb_feature.setText("\uE98B");

    radioButtons = new RadioButton[] { rb_bug, rb_info, rb_feature };
    setCheckButton(rb_bug);
  }

  private void setCheckButton(RadioButton target) {
    for (RadioButton rb : radioButtons) {
      if (rb != target) {
        rb.setChecked(false);
      } else {
        rb.setChecked(true);
        mCheckedBtn = rb;
        int idx = Arrays.asList(radioButtons).indexOf(mCheckedBtn);
        Timber.e("idx "+idx);
        if(idx>=0) {
          Timber.e("checked " + mCheckedBtn.getId() + " subject " + subjects[idx]);
        }
      }
    }
  }

  @OnClick({R.id.rb_bug, R.id.rb_info, R.id.rb_feature}) void onCheck(RadioButton b){
    setCheckButton(b);
  }

  @Override public void SuccessFeedBack() {
    progressDialog.dismiss();
    Helper.trackThis(getActivity(), "user behasil kirim feedback di menu bantuan");
    startActivity(new Intent(getActivity(), ThankFullActivity.class));
  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {
    progressDialog.dismiss();

  }

  @Override public void onNext(int RequestID) {

  }

  @Override public void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
