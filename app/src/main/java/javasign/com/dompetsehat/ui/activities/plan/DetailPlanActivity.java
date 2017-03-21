package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.presenter.plan.DetailPlanInterface;
import javasign.com.dompetsehat.presenter.plan.DetailPlanPresenter;
import javasign.com.dompetsehat.ui.activities.portofolio.DetailPortofolioActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javasign.com.dompetsehat.view.CustomProgress;
import javax.inject.Inject;
import timber.log.Timber;

import static javasign.com.dompetsehat.ui.activities.plan.DetailPlanActivity.PLAN_ID_LOCAL;

/**
 * Created by lafran on 9/23/16.
 */

public class DetailPlanActivity extends BaseActivity implements DetailPlanInterface {

  private boolean isConnected = false;

  @Bind(R.id.icr_category) IconCategoryRounded icr_category;
  @Bind(R.id.tv_plan_name) TextView tv_plan_name;
  @Bind(R.id.tv_right) TextView tv_right;
  @Bind(R.id.tv_setor) TextView tv_setor;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.indicator) IconicsTextView indicator;
  @Bind(R.id.tv_status) TextView tv_status;
  @Bind(R.id.cp_bar) CustomProgress cp_bar;
  @Bind(R.id.btn_action) Button btn_action;
  @Bind(R.id.tv_persen) TextView tv_persen;
  @Bind(R.id.tv_profit) TextView tv_profit;
  @Bind(R.id.tv_monthly_credit) TextView tv_monthly_credit;
  @Bind(R.id.tv_target) TextView tv_target;
  @Bind(R.id.tv_left_time) TextView tv_left_time;
  @Bind(R.id.btn_detail) View btn_detail;
  @Bind(R.id.ll_detail) LinearLayout ll_detail;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;

  public final static String PLAN_ID_LOCAL = "id_plan";
  public Integer id_plan;
  public Plan detailPlan;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  @Inject DetailPlanPresenter presenter;
  int[] colorAccents;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_plan);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle(getString(R.string.plan_detail_title));
    Intent intent = getIntent();
    if (intent.hasExtra(PLAN_ID_LOCAL)) {
      id_plan = intent.getExtras().getInt(PLAN_ID_LOCAL);
    } else {
      finish();
    }

    presenter.cacthChangePlan();
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  public void init() {
    this.colorAccents = new int[] {
        ContextCompat.getColor(this,R.color.blue_400), ContextCompat.getColor(this,R.color.orange_400),
        ContextCompat.getColor(this,R.color.green_500)
    };
    int healthyColor = ContextCompat.getColor(this, R.color.green_health);
    int warningColor = ContextCompat.getColor(this, R.color.yellow_warning);
    int redAlerColor = ContextCompat.getColor(this, R.color.red_alert);
    cp_bar.setColors(redAlerColor, warningColor, healthyColor);
    setIsConnected(false);
    presenter.loadData(id_plan);
  }

  @OnClick(R.id.btn_action) void onActionButtonPressed(View view) {
    if (detailPlan != null) {
      if (isConnected) {
        seeDetailPortofolio();
        return;
      }
      connectNow();
    } else {
      Toast.makeText(this, getString(R.string.error_detail_invalid), Toast.LENGTH_LONG).show();
      finish();
    }
  }

  @OnClick(R.id.btn_detail) void seeTransaction(View v) {
    if (v.getTag() instanceof ArrayList) {
      ArrayList<Integer> ids = (ArrayList<Integer>) v.getTag();
      startActivity(new Intent(this, TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
          State.FROM_TIMELINE).putExtra(State.IDS_TRANSACTION, ids));
    }
  }

  private void connectNow() {
    Intent intent = getIntent();
    switch (detailPlan.getType()) {
      case DbHelper.PLAN_TYPE_PENSIUN:
        intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_PENSIUN);
        break;
      case DbHelper.PLAN_TYPE_CUSTOME:
        intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_CUSTOM);
        break;
      case DbHelper.PLAN_TYPE_DARURAT:
        intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_DARURAT);
        break;
      case DbHelper.PLAN_TYPE_KULIAH:
        intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_KULIAH);
        break;
    }
    intent.putExtra(FinishingPlanActivity.MODE, FinishingPlanActivity.MODE_RECONNECT);
    intent.putExtra(PLAN_ID_LOCAL, detailPlan.getId());
    intent.putExtra(SetupPlanActivity.TYPE_ASUMSI,
        SetupPlanActivity.getTypeByRisk(detailPlan.getPlan_risk()));
    Helper.goTo(this, FinishingPlanActivity.class, intent);
  }

  private void seeDetailPortofolio() {
    Helper.goTo(this, DetailPortofolioActivity.class);
  }

  private void setIsConnected(boolean connedted) {
    int color = connedted ? ContextCompat.getColor(this,R.color.green_manulife)
        : ContextCompat.getColor(this,R.color.red_500);
    indicator.setTextColor(color);
  }

  private void setProgress(double maxValue, double progressValue) {
    cp_bar.setMaxValue(maxValue);
    cp_bar.setProgressValue(progressValue);
    cp_bar.invalidateDraw();
  }

  @Override public void setPlan(Plan plan, Object dana) {
    Runnable r = new Runnable() {
      @Override public void run() {
        detailPlan = plan;
        switch (plan.getType()) {
          case DbHelper.PLAN_TYPE_PENSIUN:
            tv_plan_name.setTextColor(colorAccents[0]);
            icr_category.setBackgroundColorIcon(colorAccents[0]);
            icr_category.setIconCode(DSFont.Icon.dsf_danapensiun.getFormattedName());
            tv_left_time.setText(String.valueOf(plan.time / 12) + " " + getString(R.string.year));
            break;
          case DbHelper.PLAN_TYPE_CUSTOME:
            tv_plan_name.setTextColor(colorAccents[2]);
            icr_category.setBackgroundColorIcon(colorAccents[2]);
            //icr_category.setIconCode(DSFont.Icon.dsf_danadarurat.getFormattedName());
            tv_left_time.setText(String.valueOf(plan.time / 12) + " " + getString(R.string.year));
            break;
          case DbHelper.PLAN_TYPE_DARURAT:
            tv_plan_name.setTextColor(colorAccents[1]);
            icr_category.setBackgroundColorIcon(colorAccents[1]);
            icr_category.setIconCode(DSFont.Icon.dsf_danacustome.getFormattedName());
            tv_left_time.setText(String.valueOf(plan.time) + " " + getString(R.string.year));
            break;
          case DbHelper.PLAN_TYPE_KULIAH:
            tv_plan_name.setTextColor(colorAccents[1]);
            icr_category.setBackgroundColorIcon(colorAccents[1]);
            icr_category.setIconCode(DSFont.Icon.dsf_birthday.getFormattedName());
            tv_left_time.setText(String.valueOf(plan.time / 12) + " " + getString(R.string.year));
        }
        cp_bar.setMaxValue(plan.getPlan_total());
        cp_bar.setProgressValue(plan.total_saldo);

        double persen = 100;
        if(plan.getPlan_total() > 0 && plan.getPlan_total() >= plan.total_saldo) {
          persen = 100 - ((plan.getPlan_total() - plan.total_saldo) / plan.getPlan_total() * 100);
        }
        tv_persen.setText(String.format("%.0f%%", persen));
        cp_bar.invalidateDraw();
        tv_plan_name.setText(plan.getPlan_title());
        tv_value.setText(format.toRupiahFormatSimple(plan.getPlan_total()));
        tv_setor.setText(format.toRupiahFormatSimple(plan.total_saldo));
        isConnected = plan.isConnected();
        if (plan.isConnected()) {
          Timber.e("here connected " + plan.product);
          if (plan.product != null) {
            tv_status.setText(getString(R.string.connected_with) + " " + plan.product.getName());
            btn_detail.setVisibility(View.VISIBLE);
          } else {
            Timber.e("MNL " + plan.account.getIdvendor());
            tv_status.setText(getString(R.string.connected_with) + " " + AccountView.institusi.get(
                plan.account.getIdvendor()));
          }
          tv_profit.setText(plan.getPlan_risk() + " %");
          tv_monthly_credit.setText(format.toRupiahFormatSimple(plan.getPlan_amount_monthly()));
          tv_target.setText(plan.getLifetime() + " bulan");
          indicator.setTextColor(Color.GREEN);
          btn_action.setVisibility(View.GONE);
          Date date = null;
          if (plan.getCreated_at() != null) {
            date = Helper.setInputFormatter("yyyy-MM-dd", plan.getCreated_at());
          } else {
            date = Helper.setInputFormatter("yyyy-MM-dd", plan.getUpdated_at());
          }
          if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, plan.getLifetime());
            tv_left_time.setText(Helper.setSimpleDateFormat(calendar.getTime(), "dd MMMM yyyy"));
          }
        } else {
          Timber.e("here disconnected");
          ll_detail.setVisibility(View.GONE);
          tv_status.setText(Words.toSentenceCase(getString(R.string.not_connected)));
          indicator.setTextColor(Color.GRAY);
          btn_action.setVisibility(View.VISIBLE);
          btn_detail.setVisibility(View.GONE);
        }
      }
    };
    runOnUiThread(r);
  }

  @Override public void setTransactions(ArrayList<Integer> ids) {
    runOnUiThread(() -> {
      tv_value_transaction.setText(ids.size() + " Transaksi");
      if (ids.size() > 0) {
        btn_detail.setVisibility(View.VISIBLE);
        btn_detail.setTag(ids);
      } else {
        btn_detail.setVisibility(View.GONE);
      }
    });
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
