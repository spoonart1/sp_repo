package javasign.com.dompetsehat.ui.activities.help.listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.smooch.ui.ConversationActivity;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class KontakFragment extends Fragment {

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_kontak, null);
    ButterKnife.bind(this, view);
    init();
    return view;
  }

  private void init() {

  }

  @OnClick(R.id.tv_link) void openChat(){
    ConversationActivity.show(getContext());
    Helper.trackThis(getActivity(), "User menekan tombol livechat di halaman Kontak");
  }
}
