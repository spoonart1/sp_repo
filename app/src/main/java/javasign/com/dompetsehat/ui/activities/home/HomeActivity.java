package javasign.com.dompetsehat.ui.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.home.adapter.HomeAdapter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyReferral;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by bastianbentra on 8/4/16.
 */
public class HomeActivity extends BaseActivity
    implements HomeAdapter.OnNextClickListener, AdvancedDialog.AdvancedDialogDelegate {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_greeting) TextView tv_greeting;

  private final int MYFIN_ID = SessionManager.USER_PICK_MY_FIN;
  private final int MYREFERRAL_ID = SessionManager.USER_PICK_MY_REFERRAL;

  private LinearLayoutManager layoutManager;
  private HomeAdapter adapter;
  private SessionManager sessionManager;
  private String[] fragmentTagsTarget =
      new String[] { State.FLAG_FRAGMENT_TIMELINE, State.FLAG_FRAGMENT_FINPLAN };

  private final Class[] classes =
      new Class[] { NewMainActivityMyFin.class, NewMainActivityMyReferral.class };

  private MCryptNew mCryptNew = new MCryptNew();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    sessionManager = new SessionManager(this);
    initProperties();
    Helper.trackThis(this, "user sampai di tampilan pilih sesuai kebutuhan");
  }

  private void initProperties() {
    tv_greeting.setText(
        getString(R.string.hai) + " " + mCryptNew.decrypt(sessionManager.getUsername()));
    layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new HomeAdapter(this);
    adapter.setOnNextClickListener(this);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onClick(View v, final int pos, final String tagFragmentTarget) {
    sessionManager.setCurrentUserPickAppMode(pos);
    Intent i = new Intent(this, classes[pos])
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
  }

  @Override public void onIconHelpClick(View v, int pos, String fragmentTagTarget) {
    openHomeDialog(pos);
  }

  @Override public void onNext(int identifier) {
    sessionManager.setCurrentUserPickAppMode(identifier);
  }

  private void openHomeDialog(int pos) {
    switch (pos) {
      case 0:
        showMyFinDialog(MYFIN_ID);
        break;
      case 1:
        showMyReferralDialog(MYREFERRAL_ID);
        break;
    }
  }

  private void showMyFinDialog(int identifier) {
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(this, "MY FIN", Helper.GREEN_DOMPET_COLOR).withRoundedCorner(
            false);

    builder.addImage(R.drawable.myfin, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 130));
    builder.addText(getString(R.string.manage_your_financial_easily));
    builder.addImage(R.drawable.sambungds, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 103));
    builder.addText(getString(R.string.note_automatically));

    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));

    builder.addImage(R.drawable.ss_myfin_add, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 205));
    builder.addText(getString(R.string.how_to_add));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));

    builder.addImage(R.drawable.ss_myfin_rencana, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 510));
    builder.addText(getString(R.string.plan_and_realizing));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));

    builder.addImage(R.drawable.ic_popup_budget, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 103));
    builder.addText(getString(R.string.create_monthly_budget));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));

    builder.addImage(R.drawable.ic_popup_reminder, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 110));
    builder.addText(getString(R.string.need_bill_reminder));

    createDialog(builder, new Intent(this, NewMainActivityMyFin.class), identifier).show(
        getSupportFragmentManager(), "fin-dialog");

    Helper.trackThis(this, "user memilih MyFin");
  }

  private void showMyReferralDialog(int identifier) {
    AdvancedDialog.ABuilder builder = new AdvancedDialog.ABuilder(this, "MY REFERRAL",
        Helper.GREEN_DOMPET_COLOR).withRoundedCorner(false);

    builder.addImage(R.drawable.ic_popup_myreferral, null, ViewGroup.LayoutParams.MATCH_PARENT,
        ChartUtils.dp2px(getResources().getDisplayMetrics().density, 130));
    builder.addText(getString(R.string.want_to_get_money), 18, TypedValue.COMPLEX_UNIT_SP);
    builder.addSpace(getResources().getDimensionPixelSize(R.dimen.padding_root_layout));

    builder.addText(getString(R.string.dompetsehat_colaborating_with));
    builder.addSpace(getResources().getDimensionPixelSize(R.dimen.padding_root_layout));

    builder.addText(getString(R.string.by_activating_my_referral));
    builder.addSpace(getResources().getDimensionPixelSize(R.dimen.padding_size_high));

    createDialog(builder, new Intent(this, NewMainActivityMyReferral.class), identifier).show(
        getSupportFragmentManager(), "ref-dialog");

    Helper.trackThis(this, "user memilih MyReferral");
  }

  private AdvancedDialog createDialog(AdvancedDialog.ABuilder builder, Intent destination,
      int identifier) {
    AdvancedDialog dialog = AdvancedDialog.newInstance(builder, destination);
    dialog.setDelegate(this);
    dialog.setIdentifier(identifier);
    return dialog;
  }
}
