package javasign.com.dompetsehat.ui.activities.help.listfragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.help.HelpActivity;
import javasign.com.dompetsehat.ui.activities.webview.WebLoaderActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Words;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class WebviewFragment extends Fragment {

  protected final int DEFAULT_POS = 0;

  @Bind(R.id.webview) WebView webView;
  @Bind(R.id.radio_group) RadioGroup radio_group;
  @Bind(R.id.btn_action) Button btn_action;
  @Bind(R.id.btn_action_faq) Button btn_action_faq;
  @Bind(R.id.btn_support_center) Button btn_support_center;
  @Bind(R.id.action_frame) View action_frame;
  @Bind({ R.id.rb1, R.id.rb2, R.id.rb3 }) RadioButton[] btns;

  private String tag;
  private String[] url;
  private String[] buttonTitles;
  private boolean withTopButton = false;
  private boolean withActionButton = false;

  public static WebviewFragment newInstance(String tag, String... urlToLoad) {
    WebviewFragment webviewFragment = new WebviewFragment();
    webviewFragment.init(tag, urlToLoad);
    return webviewFragment;
  }

  public static WebviewFragment newInstance(String tag, boolean withTopButton,
      String[] buttonTitles, String... urlToLoad) {
    WebviewFragment webviewFragment = new WebviewFragment();
    webviewFragment.init(tag, urlToLoad);
    webviewFragment.setWithTopButton(withTopButton, buttonTitles);
    return webviewFragment;
  }

  public WebviewFragment showActionButton(boolean withActionButton) {
    this.withActionButton = withActionButton;
    return this;
  }

  private void init(String tag, String... urlToLoad) {
    this.tag = tag;
    this.url = urlToLoad;
  }

  private void setWithTopButton(boolean show, String[] titles) {
    this.withTopButton = show;
    this.buttonTitles = titles;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_webview, null);
    ButterKnife.bind(this, view);

    setUrlToLoad(DEFAULT_POS);

    initView();
    return view;
  }

  private void initView() {
    if (withTopButton) {
      setButtonTitles();

      radio_group.setOnCheckedChangeListener((group, checkedId) -> {
        int radioButtonID = group.getCheckedRadioButtonId();
        View radioButton = group.findViewById(radioButtonID);
        int idx = group.indexOfChild(radioButton);

        setUrlToLoad(idx);
      });
    }
  }

  public void setUrlToLoad(int position) {
    if(url != null) {
      beginToLoad(url[position]);
    }else{
      beginToLoad(Words.full_link_pdf);
    }
  }

  protected void beginToLoad(String urlToLoad) {
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);

    if (!withActionButton) {
      webView.setWebViewClient(new WebViewClient());
      webView.loadUrl(urlToLoad);
    } else {
      action_frame.setVisibility(View.VISIBLE);
      btn_action.setOnClickListener(v -> {
        Helper.trackThis(getActivity(), "user klik Buku Panduan di menu bantuan");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad));
        startActivity(browserIntent);
        //startActivity(new Intent(getActivity(), WebLoaderActivity.class)
        //    .putExtra("url",urlToLoad)
        //    .putExtra("title", btn_action_faq.getText().toString()));
      });
      btn_action_faq.setOnClickListener(v -> {
        Helper.trackThis(getActivity(), "user klik FAQ di menu bantuan");
        startActivity(new Intent(getActivity(), WebLoaderActivity.class)
            .putExtra("url", "https://dompetsehat.com/faq")
            .putExtra("title", btn_action_faq.getText().toString()));
      });

      btn_support_center.setOnClickListener(view -> {
        Helper.trackThis(getActivity(), "user klik Support Center di menu bantuan");
        startActivity(new Intent(getActivity(), WebLoaderActivity.class)
            .putExtra("url", "http://support.dompetsehat.com/")
            .putExtra("title", btn_support_center.getText().toString()));
      });

      Helper.trackThis(getActivity(), "User klik tombol panduan penggunaan ");
    }
    webView.setBackgroundColor(Color.TRANSPARENT);
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  protected void setButtonTitles() {
    radio_group.setVisibility(View.VISIBLE);
    for (int i = 0; i < btns.length; i++) {
      btns[i].setText(buttonTitles[i]);
    }
  }
}
