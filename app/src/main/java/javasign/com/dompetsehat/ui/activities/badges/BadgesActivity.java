package javasign.com.dompetsehat.ui.activities.badges;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Badges;
import javasign.com.dompetsehat.ui.globaladapters.ListAdapterBadges;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.SessionManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by Spoonart on 9/1/2015.
 */
public class BadgesActivity extends BaseActivity {

  private final String ID_TAG = "id";
  private final String VERSION_LABEL = "version";
  private final String BADGE_TAG = "Badge";
  private final String TITLE_TAG = "title";
  private final String FIRST_BAR_TAG = "firstbar";
  private final String SECOND_BAR_TAG = "secondbar";
  private final String DRAWABLE_TAG = "drawable";
  private DbHelper db;
  private SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private float transaction, saldo;
  private int badges_id;

  private String currentVersionPack;
  private ListAdapterBadges adapterBadges;
  private ArrayList<Badges> badgeses;
  private int newerVersion = 0;

  @Bind(R.id.lv_bagdes) ListView lv_bagdes;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_badges);
    ButterKnife.bind(this);

    session = new SessionManager(BadgesActivity.this);
    db = DbHelper.getInstance(BadgesActivity.this);

    getActivityComponent().inject(this);

    transaction = db.getTransactionCount(Integer.valueOf(session.getIdUser()));
    saldo = db.getTotalSaldoAllAccount(Integer.valueOf(session.getIdUser()));
    badges_id = db.getBadgesID(Integer.valueOf(session.getIdUser()));

    lv_bagdes = (ListView) findViewById(R.id.lv_bagdes);
    setTitle("Lencana");
    //cekVersion();
    loadPing();

    MyCustomApplication.initTracker(this, "Page : Badges");
    Helper.trackThis(this, "user membuka tampilan badge");
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  protected void loadPing() {
    AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {

      @Override protected void onPreExecute() {
        super.onPreExecute();
      }

      @Override protected String doInBackground(String... strings) {
        String mypath = LoadAndSaveImage.dirAppsPing + LoadAndSaveImage.pingName;
        if (cekFile(LoadAndSaveImage.dirAppsPing, LoadAndSaveImage.pingName)) {
          try {
            XMLPullParserHandler parser = new XMLPullParserHandler();
            badgeses = parser.parse(new FileInputStream(mypath));
            adapterBadges =
                new ListAdapterBadges(BadgesActivity.this, badgeses, badges_id, transaction, saldo);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return helper.YES;
      }

      @Override protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (adapterBadges != null) lv_bagdes.setAdapter(adapterBadges);
      }
    };
    asyncTask.execute();
  }

  public void addHeaderListener() {
    lv_bagdes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout bottom = (LinearLayout) adapterBadges.getView(position, view, parent)
            .findViewById(R.id.ll_bottom_info);

        if (bottom == null) return;

        if (bottom.getVisibility() == View.GONE) {
          bottom.setVisibility(View.VISIBLE);
          return;
        } else {
          bottom.setVisibility(View.GONE);
          return;
        }
      }
    });
  }

  protected void cekVersion() {
    new AsyncTask<String, String, String>() {
      @Override protected String doInBackground(String... params) {
        String version = "zonk";
        if (version.equals("zonk")) {
          List<NameValuePair> param = new ArrayList<NameValuePair>();
          param.add(new BasicNameValuePair("version", "request"));
          //IKI DI GANTI IP NE
          version = makeHttpRequest("http://192.168.1.22/uploadimages/check.php", "GET", param);
          return version;
        }
        return "zonk";
      }

      @Override protected void onPostExecute(String s) {
        super.onPostExecute(s);
        newerVersion = Integer.parseInt(s.substring(0, 1));

        if (newerVersion > Integer.parseInt(currentVersionPack)) {
          new AlertDialog.Builder(BadgesActivity.this).setTitle(
              getString(R.string.alert_dialog_badges))
              .setMessage(getString(R.string.alert_dialog_badges))
              .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  Toast.makeText(BadgesActivity.this, getString(R.string.downloading),
                      Toast.LENGTH_LONG).show();
                }
              })
              .show();
        }
      }
    }.execute();
  }

  InputStream is = null;
  String valueResult = "kosong";

  private String makeHttpRequest(String url, String method, List<NameValuePair> params) {
    try {
      if (method.equals("GET")) {
        // request method is GET
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += "?" + paramString;
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        is = httpEntity.getContent();
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
      is.close();
      valueResult = sb.toString();
    } catch (Exception e) {
    }
    return valueResult;
  }

  protected boolean cekFile(String path, String filename) {
    File file = new File(path, filename);
    return file.exists();
  }

  class XMLPullParserHandler {
    ArrayList<Badges> badgesList;
    private Badges badges;

    public XMLPullParserHandler() {
      badgesList = new ArrayList<Badges>();
    }

    public List<Badges> getBadges() {
      return badgesList;
    }

    public ArrayList<Badges> parse(InputStream is) {
      XmlPullParserFactory factory = null;
      XmlPullParser parser = null;
      try {
        factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newPullParser();
        parser.setInput(is, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
          String tagname = parser.getName();
          switch (eventType) {
            case XmlPullParser.START_TAG:
              if (tagname.equalsIgnoreCase(VERSION_LABEL)) {
                currentVersionPack = parser.nextText();
              }
              if (tagname.equalsIgnoreCase(BADGE_TAG)) {
                badges = new Badges();
              }
              if (badges != null) {
                if (tagname.equalsIgnoreCase(ID_TAG)) {
                  badges.id = parser.nextText();
                } else if (tagname.equalsIgnoreCase(TITLE_TAG)) {
                  badges.title = parser.nextText();
                } else if (tagname.equalsIgnoreCase(DRAWABLE_TAG)) {
                  badges.drawable = parser.nextText();
                } else if (tagname.equalsIgnoreCase(FIRST_BAR_TAG)) {
                  badges.firstBarTitle = parser.getAttributeValue(0);
                  badges.firstBarMin = parser.getAttributeValue(1);
                  badges.firstBarMax = parser.getAttributeValue(2);
                } else if (tagname.equalsIgnoreCase(SECOND_BAR_TAG)) {
                  badges.secondBarTitle = parser.getAttributeValue(0);
                  badges.secondBarMin = parser.getAttributeValue(1);
                  badges.secondBarMax = parser.getAttributeValue(2);
                }
              }
              break;
            case XmlPullParser.END_TAG:
              if (tagname.equalsIgnoreCase(BADGE_TAG)) {
                if (badges != null) badgesList.add(badges);
                //Log.d("XML_END",badgesList.size()+"");
              }
              break;
            default:
              break;
          }
          eventType = parser.next();
        }
      } catch (XmlPullParserException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return badgesList;
    }
  }
}
