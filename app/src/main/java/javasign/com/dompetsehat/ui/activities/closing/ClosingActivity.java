package javasign.com.dompetsehat.ui.activities.closing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.account.ManageEachAccountActivity;
import javasign.com.dompetsehat.view.AccountView;

/**
 * Created by bastianbentra on 8/24/16.
 */
public class ClosingActivity extends BaseActivity {

  public static final String TITLE_KEY = "title";
  public static final String TEXT_2_KEY = "text2";
  public static final String TEXT_3_KEY = "text3";
  public static final String BUTTON_LABEL = "btn.label";
  public static final String BUTTON_DRAWABLE_RES = "btn.drawable";
  public static final String IMAGE_RES = "img.res.id";

  @Bind(R.id.tv_title) TextView tv_title;
  @Bind(R.id.text2) TextView text2;
  @Bind(R.id.text3) TextView text3;
  @Bind(R.id.tv_referral_code) TextView tv_referral_code;
  @Bind(R.id.btn_close) Button btn_close;
  @Bind(R.id.iv_image) ImageView iv_image;
  @Bind(R.id.ll_referral) View ll_referral;

  private String mTitle = "";
  private String mText2 = "";
  private String mText3 = "";
  private String mButtonLabel = "Tutup";
  private int mBtnDrawableRes = R.drawable.button_grey_dark;
  private int mImageRes = R.drawable.happy;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_closing);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    Bundle b = getIntent().getExtras();

    if(b != null){
      mTitle = b.getString(TITLE_KEY, "");
      mText2 = b.getString(TEXT_2_KEY, "");
      mText3 = b.getString(TEXT_3_KEY, "");

      if(TextUtils.isEmpty(b.getString("reffcode"))){
        ll_referral.setVisibility(View.GONE);
      }
      else {
        ll_referral.setVisibility(View.VISIBLE);
        tv_referral_code.setText(b.getString("reffcode"));
      }

      mButtonLabel = b.getString(BUTTON_LABEL, "Tutup");
      mBtnDrawableRes = b.getInt(BUTTON_DRAWABLE_RES, R.drawable.button_grey_dark);
      mImageRes = b.getInt(IMAGE_RES, R.drawable.happy);
    }

    init();
  }

  @OnClick(R.id.btn_close) void onClose(){
    if(getIntent().hasExtra("id_account")){
      Bundle b = new Bundle();
      b.putInt(ManageEachAccountActivity.KEY_ID_ACCOUNT,getIntent().getExtras().getInt("id_account"));
      int color = AccountView.accountColor.get(10);
      b.putInt("color", color);
      Intent intent = new Intent(ClosingActivity.this, ManageEachAccountActivity.class);
      intent.putExtras(b).putExtra("from","login");
      startActivity(intent);
    }
    finish();
  }

  private void init() {
    tv_title.setText(Html.fromHtml(mTitle));
    text2.setText(Html.fromHtml(mText2));
    text3.setText(Html.fromHtml(mText3));

    Spanned label = Html.fromHtml(mButtonLabel);
    btn_close.setText(label);
    btn_close.setBackgroundDrawable(getResources().getDrawable(mBtnDrawableRes));

    iv_image.setImageResource(mImageRes);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
