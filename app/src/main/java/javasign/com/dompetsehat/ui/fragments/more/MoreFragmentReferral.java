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
import javasign.com.dompetsehat.ui.activities.comission.ComissionActivity;
import javasign.com.dompetsehat.ui.activities.help.HelpActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.activities.setting.SettingActivity;
import javasign.com.dompetsehat.ui.fragments.more.adapter.MoreAdapter;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;

/**
 * Created by bastianbentra on 8/5/16.
 */
public class MoreFragmentReferral extends Fragment {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.btn_chat) View btn_chat;

  private MoreAdapter adapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_more, null);
    ButterKnife.bind(this, view);

    init();
    return view;
  }

  private void init() {
    btn_chat.setVisibility(View.GONE);
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    ArrayList<String> labels = new ArrayList<>();
    labels.add(getResources().getString(R.string.comission));
    labels.add(getResources().getString(R.string.setting));
    labels.add(getResources().getString(R.string.help));
    labels.add("My Fin");

    //labels.add(Words.Feedback);

    ArrayList<String> bottLabels = new ArrayList<String>();
    bottLabels.add(getString(R.string.more_comission_referral)); // referral
    bottLabels.add(getString(R.string.more_setting_general)); //setting
    bottLabels.add(getString(R.string.more_helper)); //help
    bottLabels.add(getString(R.string.switch_referral_and_fin_subtext)); // myfin
    //bottLabels.add("Memberi ulasan baik mengenai aplikasi"); //feedback

    ArrayList<String> icons = new ArrayList<String>();
    icons.add(DSFont.Icon.dsf_comission.getFormattedName());
    icons.add(DSFont.Icon.dsf_setting.getFormattedName());
    icons.add(DSFont.Icon.dsf_help.getFormattedName());
    icons.add(DSFont.Icon.dsf_myfin.getFormattedName());

    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    classes.add(ComissionActivity.class);
    classes.add(SettingActivity.class);
    classes.add(HelpActivity.class);
    classes.add(NewMainActivity.class);
    //classes.add(FeedbackActivity.class);

    adapter =
        new MoreAdapter(labels, bottLabels).withIcons(icons).withListener((view, position) -> {
          if (position == labels.size() - 1) {
            SessionManager.getIt(getContext())
                .setCurrentUserPickAppMode(SessionManager.USER_PICK_MY_FIN);
          }
          Intent i = new Intent(getActivity(), classes.get(position));
          Helper.trackThis(getActivity(), "user klik " + labels.get(position) + " di My Referral");
          startActivity(i);
        });
    recyclerView.setAdapter(adapter);
  }

  @OnClick(R.id.btn_chat) void chatNow() {
    ConversationActivity.show(getActivity());
    Helper.trackThis(getActivity(), "User menekan tombol (floating) live chat");
  }
}
