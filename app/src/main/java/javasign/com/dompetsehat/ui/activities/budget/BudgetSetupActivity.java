package javasign.com.dompetsehat.ui.activities.budget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class BudgetSetupActivity extends BaseBudgetActivity {

  public static final String MONTHLY_INCOME = "monthly_income";
  public static final String KREDIT = "kredit";
  public static final String KIDS = "kids";

  @Bind(R.id.et_date) TextView et_date;
  @Bind(R.id.et_monthly_value) MaterialEditText et_monthly_value;
  @Bind(R.id.et_monthly_kredit) MaterialEditText et_monthly_kredit;
  @Bind(R.id.et_kids) MaterialEditText et_kids;
  @Bind(R.id.btn_next) Button btn_next;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private GeneralHelper helper = GeneralHelper.getInstance();
  private SessionManager sessionManager;

  @Inject BudgetPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_budget_setup);
    ButterKnife.bind(this);
    setTitle("");

    getActivityComponent().inject(this);
    sessionManager = new SessionManager(getActivityComponent().context());
    presenter.attachView(this);
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView close = ButterKnife.findById(this, R.id.ic_back);
    close.setText(CommunityMaterial.Icon.cmd_close.getFormattedName());
    format.formatEditText(et_monthly_value);
    Words.setButtonToListen(btn_next, et_monthly_value,et_kids);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
    //prompToExit();
  }

  @Override public void finish() {
    sessionManager.setUserHasLaunchedAppOnce();
    super.finish();
  }

  @OnClick(R.id.btn_next) void onNext() {
    double monthly_income = Double.valueOf(RupiahCurrencyFormat.clearRp(et_monthly_value.getText().toString()));
    double kredit = 0;//Double.valueOf(RupiahCurrencyFormat.clearRp(et_monthly_kredit.getText().toString()));
    int child = Integer.valueOf(et_kids.getText().toString());
    presenter.saveBudgetUser(monthly_income,kredit,child);
    Intent i = new Intent();
    i.putExtra(MONTHLY_INCOME,monthly_income);
    i.putExtra(KREDIT,kredit);
    i.putExtra(KIDS,child);
    Helper.goTo(this, BudgetResultEstimatorActivity.class,i);
  }

  private SlideDateTimeListener listener = new SlideDateTimeListener() {
    @Override public void onDateTimeSet(Date date) {
      et_date.setText(Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_DD_MMM_YYYY));
    }
  };

  @OnClick(R.id.et_date) void setDate(TextView t){
    new SlideDateTimePicker.Builder(getSupportFragmentManager())
        .setInitialDate(new Date())
        .setListener(listener)
        .setIsDateOnly(true)
        .setIs24HourTime(true)
        .setIndicatorColor(Helper.GREEN_DOMPET_COLOR)
        .build()
        .show();
  }

  private void prompToExit() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.skip))
        .setMessage(getString(R.string.are_you_sure_exit))
        .setNegativeButton(getString(R.string.cancel), null)
        .setPositiveButton(getString(R.string.skip), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        })
        .show();
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
