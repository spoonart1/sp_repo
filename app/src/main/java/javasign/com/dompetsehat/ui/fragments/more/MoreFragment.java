package javasign.com.dompetsehat.ui.fragments.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.smooch.ui.ConversationActivity;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.ui.activities.account.NewManageAccountActivity;
import javasign.com.dompetsehat.ui.activities.debts.DebtsActivity;
import javasign.com.dompetsehat.ui.activities.help.HelpActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.activities.reminder.ReminderActivity;
import javasign.com.dompetsehat.ui.activities.setting.SettingActivity;
import javasign.com.dompetsehat.ui.fragments.more.adapter.MoreAdapter;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;

/**
 * Created by bastianbentra on 8/5/16.
 */
public class MoreFragment extends Fragment {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.btn_chat) View btn_chat;

  private MoreAdapter adapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_more, null);
    ButterKnife.bind(this, view);

    init();

    Helper.trackThis(getActivity(), "user membuka tampilan lainnya");

    return view;
  }

  private void init() {
    btn_chat.setVisibility(View.GONE);
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    ArrayList<String> labels = new ArrayList<>();
    //labels.add(getResources().getString(R.string.account));
    labels.add("Hutang");
    labels.add(getResources().getString(R.string.reminder_title));
    //labels.add(getResources().getString(R.string.comission));
    labels.add(getResources().getString(R.string.setting));
    labels.add(getResources().getString(R.string.help));
    if (MyCustomApplication.showInvestasi()) {
      labels.add("My Referral");
    }
    //labels.add(Words.Feedback);

    ArrayList<String> bottLabels = new ArrayList<String>();
    //bottLabels.add(getString(R.string.more_account_manager)); //account
    bottLabels.add("Hutang dan piutang");
    bottLabels.add(getString(R.string.more_bill_reminder)); //reminder
    //bottLabels.add(getString(R.string.more_comission_referral)); // referral
    bottLabels.add(getString(R.string.more_setting_general)); //setting
    bottLabels.add(getString(R.string.more_helper)); //help
    if (MyCustomApplication.showInvestasi()) {
      bottLabels.add(getString(R.string.switch_referral_only_subtext)); //myreferral
    }
    //bottLabels.add("Memberi ulasan baik mengenai aplikasi"); //feedback

    ArrayList<String> icons = new ArrayList<String>();
    //icons.add(DSFont.Icon.dsf_budget.getFormattedName());
    icons.add(DSFont.Icon.dsf_pot_money.getFormattedName());
    icons.add(DSFont.Icon.dsf_reminder_2.getFormattedName());
    //icons.add(DSFont.Icon.dsf_comission.getFormattedName());
    icons.add(DSFont.Icon.dsf_setting.getFormattedName());
    icons.add(DSFont.Icon.dsf_help.getFormattedName());
    if (MyCustomApplication.showInvestasi()) {
      icons.add(DSFont.Icon.dsf_myreferral.getFormattedName());
    }
    //icons.add(DSFont.Icon.dsf_feedback.getFormattedName());

    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    //classes.add(NewManageAccountActivity.class);
    classes.add(DebtsActivity.class);
    classes.add(ReminderActivity.class);
    //classes.add(ReferralLoaderActivity.class);
    classes.add(SettingActivity.class);
    classes.add(HelpActivity.class);
    if (MyCustomApplication.showInvestasi()) {
      classes.add(NewMainActivity.class);
    }
    //classes.add(FeedbackActivity.class);

    //if(!MyCustomApplication.showInvestasi()) {
    //  int[] removes = { 2, 3 };
    //  removeUnnecessary(labels, removes);
    //  removeUnnecessary(bottLabels, removes);
    //  removeUnnecessary(icons, removes);
    //  removeUnnecessary(classes, removes);
    //}

    adapter =
        new MoreAdapter(labels, bottLabels).withIcons(icons).withListener((view, position) -> {
          if (position == labels.size() - 1) {
            SessionManager.getIt(getContext())
                .setCurrentUserPickAppMode(SessionManager.USER_PICK_MY_REFERRAL);
          }
          Intent i = new Intent(getActivity(), classes.get(position));
          Helper.trackThis(getActivity(),
              "user membuka tampilan " + labels.get(position) + " di menu lainnya");
          startActivity(i);
        });
    recyclerView.setAdapter(adapter);

    Helper.trackThis(getActivity(), "User di halaman menu lainnya");
  }

  private void removeUnnecessary(ArrayList<?> arrayList, int... indexsToRemove) {
    int counter = -1;
    for (int i = 0; i < indexsToRemove.length; i++) {
      counter++;
      arrayList.remove(indexsToRemove[i] - counter);
    }
  }

  @OnClick(R.id.btn_chat) void chatNow() {
    ConversationActivity.show(getActivity());
    Helper.trackThis(getActivity(), "User klik tombol (floating) live chat");
  }
}
