package javasign.com.dompetsehat.presenter.referral;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.google.gson.Gson;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.models.response.MamiDataResponse;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by avesina on 10/24/16.
 */

public class ReferralPresenter extends BasePresenter<ReferralInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  public final SessionManager session;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private ProgressDialog dialog;

  String[] labels = new String[] {
      //"Bagikan ke Facebook",
      //"Bagikan ke Twitter",
      //"Bagikan ke Whatsapp",
      "Bagikan Kode Referral", "Salin Kode Referral"
  };

  String[] icons = new String[] {
      //DSFont.Icon.dsf_fb_2.getFormattedName(),
      //DSFont.Icon.dsf_twitter.getFormattedName(),
      //DSFont.Icon.dsf_whatsapp.getFormattedName(),
      DSFont.Icon.dsf_referral.getFormattedName(), DSFont.Icon.dsf_copy.getFormattedName()
  };

  int[] colors;

  @Inject
  public ReferralPresenter(@ActivityContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(ReferralInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void init() {
    try {
      User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
      colors = new int[] {
          //ContextCompat.getColor(context, R.color.accent_facebook),
          //ContextCompat.getColor(context, R.color.accent_twitter),
          //ContextCompat.getColor(context, R.color.accent_whatsapp),
          ContextCompat.getColor(context, R.color.accent_gmail), ContextCompat.getColor(context, R.color.grey_800)
      };
      String referral_code = "";
      if (user != null) {
        referral_code = user.getReferral_code();
      }
      Account account = db.getAccountById(10, 1, Integer.valueOf(session.getIdUser()));
      boolean set = session.ihaveAnInstutitionAccount();
      String status = null;
      if (account == null) {
        set = false;
      } else {
        if (!TextUtils.isEmpty(account.getProperties())) {
          Gson gson = new Gson();
          MamiDataResponse.Data m = gson.fromJson(account.getProperties(), MamiDataResponse.Data.class);
          //switch (m.ClientStat){
          //  case 1:
          //    break;
          //  case 2:
          //    break;
          //  case 3:
          //    break;
          //  case 4:
          //    break;
          //}
          //if (m.CliRegStat != 1) {
          //  referral_code = referral_code.substring(0, referral_code.length() - 3) + "***";
          //  status = "Kode Referral belum aktif";
          //}
        }
      }
      getMvpView().setAdapter(set, referral_code, status, labels, icons, colors);
    }catch (Exception e){

    }
  }
}
