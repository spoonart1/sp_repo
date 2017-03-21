package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Words;

/**
 * Created by bastianbentra on 8/24/16.
 */
public class InputReferralCodeMamiActivity extends BaseActivity {

  @Bind(R.id.et_field) MaterialEditText et_field;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_input_referral_mami);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    init();
  }

  private void init() {
    Words.setButtonToListen(ButterKnife.findById(this, R.id.btn_next), et_field);
  }

  @OnClick(R.id.btn_next) void validasiCode(View view){
    Helper.showCustomSnackBar(view, getLayoutInflater(), "No API :( ");
  }

  @OnClick(R.id.ll_skip) void skip(){
    Helper.goTo(this, RegisterMamiActivity.class);
    finish();
  }
}
