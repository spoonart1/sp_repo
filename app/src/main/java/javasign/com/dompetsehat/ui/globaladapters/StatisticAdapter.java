package javasign.com.dompetsehat.ui.globaladapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.ui.activities.account.NewManageAccountActivity;
import javasign.com.dompetsehat.ui.activities.budget.BudgetActivity;
import javasign.com.dompetsehat.ui.activities.comission.ComissionActivity;
import javasign.com.dompetsehat.ui.activities.institusi.ListAvailableInstitusiActivity;
import javasign.com.dompetsehat.ui.activities.plan.PlansActivity;
import javasign.com.dompetsehat.ui.activities.portofolio.PortofolioActivity;
import javasign.com.dompetsehat.ui.activities.reminder.ReminderActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;

/**
 * Created by bastianbentra on 8/3/16.
 */
public class StatisticAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final String CHEVRON_RIGHT_ICON = DSFont.Icon.dsf_right_chevron_thin.getFormattedName();
  private final String LOCKED_ICON = DSFont.Icon.dsf_lock.getFormattedName();

  private boolean isRegisteredUser = false;
  private String[] icons;
  private String[] subtitles;
  private String[] prefixTitles;
  private String[] subtitleUnregistered;

  public static final int ACCOUNT_POS = 0;
  public static final int TRANS_POS = 1;
  public static final int BUDGET_POS = 2;
  public static final int PLAN_POS = 3;
  public static final int REMIND_POS = 4;
  public static final int PORTO_POS = 5;
  public static final int COMISSION_POS = 6;

  private Integer[] counts = { 0, 0, 0, 0, 0, 0, 0 };
  private Double[] values = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

  private Context context;

  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();

  public StatisticAdapter(Context context, boolean isRegisteredUser) {
    this.context = context;
    this.isRegisteredUser = isRegisteredUser;
    populateData();
  }

  public StatisticAdapter(Context context, boolean isRegisteredUser, Integer[] counts,
      Double[] values) {
    this.context = context;
    this.isRegisteredUser = isRegisteredUser;
    if (counts != null) {
      this.counts = counts;
    }
    if (values != null) {
      this.values = values;
    }
    populateData();
  }

  public void populateData() {

    if (context == null) {
      throw new Error("context is null");
    }

    icons = new String[] {
        DSFont.Icon.dsf_wallet_3.getFormattedName(),
        DSFont.Icon.dsf_transactions.getFormattedName(), DSFont.Icon.dsf_budget.getFormattedName(),
        DSFont.Icon.dsf_my_plan.getFormattedName(), DSFont.Icon.dsf_clock.getFormattedName(),
        DSFont.Icon.dsf_portofolio.getFormattedName(), DSFont.Icon.dsf_comission.getFormattedName()
    };

    prefixTitles = new String[] {
        context.getString(R.string.account) + ": ", context.getString(R.string.transactions) + ": ",
        context.getString(R.string.budget), context.getString(R.string.tab_title_plan) + ":",
        context.getString(R.string.reminder_title), context.getString(R.string.portfolio),
        context.getString(R.string.comission),
    };

    subtitles = new String[] {
        context.getString(R.string.account_balance), context.getString(R.string.total_transaction),
        context.getString(R.string.total_budget), context.getString(R.string.total_plan),
        context.getString(R.string.total_reminder), context.getString(R.string.total_portfolio),
        context.getString(R.string.comission_kamu),
    };

    subtitleUnregistered = new String[] {
        context.getString(R.string.subtitle_unregistered_manage_account),
        context.getString(R.string.subtitle_unregistered_transaction),
        context.getString(R.string.subtitle_unregistered_budget),
        context.getString(R.string.subtitle_unregistered_plan),
        context.getString(R.string.subtitle_unregistered_reminder),
        context.getString(R.string.subtitle_unregistered_portfolio),
        context.getString(R.string.subtitle_unregistered_comission),
    };
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_statistic, parent, false);
    Holder h = new Holder(view);
    return h;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    String icon = icons[position];
    String subText = isRegisteredUser ? subtitles[position] : subtitleUnregistered[position];
    String titlePrefix = prefixTitles[position];
    Holder h = (Holder) holder;

    int count = -1;
    if (this.counts[position] != null) {
      count = this.counts[position];
    }

    h.tv_icon.setText(icon);
    h.tv_subtitle.setText(subText);
    h.tv_title.setText(titlePrefix);

    if (isRegisteredUser && count != -1) {
      //override text if registered user!!
      if (position == 6) {
        h.tv_subtitle.setText(subText);
        h.tv_values.setText(rcf.toRupiahFormatSimple(this.values[position]));
        h.tv_values.setVisibility(View.GONE);
      } else {
        h.tv_subtitle.setText(subText + " (" + count + ")");
        h.tv_values.setText(rcf.toRupiahFormatSimple(this.values[position]));
        h.tv_values.setVisibility(View.VISIBLE);
      }
    }

    if (!isRegisteredUser) return;

    switch (position) {
      case 0://"Rekening: ":
        h.itemView.setOnClickListener(v -> Helper.goTo(context, NewManageAccountActivity.class));
        NewManageAccountActivity.accessFrom = "dari profile";
        Helper.trackThis(context, "user membuka rekening di profile");
        break;
      case 1://"Transaksi: ":
        h.itemView.setOnClickListener(view -> {
          Intent intent = new Intent();
          intent.putExtra(TransactionsActivity.FROM, State.FROM_TIMELINE);
          Helper.goTo(context, TransactionsActivity.class, intent);
        });
        Helper.trackThis(context, "user membuka transaksi di profile");
        break;
      case 2://"Anggaran":
        h.itemView.setOnClickListener(v -> Helper.goTo(context, BudgetActivity.class));
        Helper.trackThis(context, "user membuka anggaran di profile");
        break;
      case 3://"Rencana:":
        h.itemView.setOnClickListener(v -> Helper.goTo(context, PlansActivity.class));
        Helper.trackThis(context, "user membuka total rencana di profile");
        break;
      case 4://"Pengingat":
        h.itemView.setOnClickListener(v -> Helper.goTo(context, ReminderActivity.class));
        Helper.trackThis(context, "user membuka tagihan di profile");
        break;
      case 5://"Portofolio":
        Intent i = new Intent(context, ListAvailableInstitusiActivity.class).putExtra("target",
            "portofolio").putExtra("from", "profile");
        h.itemView.setOnClickListener(
            v -> Helper.goTo(context, ListAvailableInstitusiActivity.class, i));
        Helper.trackThis(context, "user membuka portofolio di profile");
        break;
      case 6://"Komisi":
        Intent ii =
            new Intent(context, ListAvailableInstitusiActivity.class).putExtra("target", "komisi")
                .putExtra("from", "profile");
        h.itemView.setOnClickListener(
            v -> Helper.goTo(context, ListAvailableInstitusiActivity.class, ii));
        Helper.trackThis(context, "user membuka referral fee portofolio di profile");
        break;
    }
  }

  public void hideView(View v) {
    if (v.getVisibility() != View.GONE) v.setVisibility(View.GONE);
    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) v.getLayoutParams();
    layoutParams.setMargins(0, 0, 0, 0);
    v.setLayoutParams(layoutParams);
  }

  @Override public int getItemCount() {
    if (MyCustomApplication.showInvestasi()) {
      return isRegisteredUser ? icons.length : icons.length - 1;
    } else {
      return isRegisteredUser ? 5 : 4;
    }
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_icon) IconicsTextView tv_icon;
    @Bind(R.id.right_icon) IconicsTextView right_icon;
    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_subtitle) TextView tv_subtitle;
    @Bind(R.id.tv_total) TextView tv_values;
    @Bind(R.id.ll_content) LinearLayout ll_content;
    @Bind({ R.id.tv_icon, R.id.tv_subtitle, R.id.tv_total, R.id.tv_title, R.id.right_icon })
    TextView[] texts;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      for (TextView t : texts) {
        t.setEnabled(isRegisteredUser);
      }
      String icon = isRegisteredUser ? CHEVRON_RIGHT_ICON : LOCKED_ICON;
      right_icon.setText(icon);

      int visible = isRegisteredUser ? View.VISIBLE : View.GONE;
      tv_values.setVisibility(visible);
    }
  }
}
