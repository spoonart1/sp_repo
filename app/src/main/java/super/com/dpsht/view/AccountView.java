package javasign.com.dompetsehat.view;

import android.graphics.Color;

import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.utils.DSFont;

/**
 * Created by spoonart on 2/5/16.
 */
public class AccountView {

  public static final String TAG_1 = "idbank";
  public static final String TAG_2 = "namebank";
  public static final String TAG_3 = "linkbank";

  public static final int DP_COLOR = Color.parseColor("#28b89b");
  public static final int MD_COLOR = Color.parseColor("#053464");
  public static final int BC_COLOR = Color.parseColor("#0C64AE");
  public static final int BN_COLOR = Color.parseColor("#F84117");
  public static final int BR_COLOR = Color.parseColor("#17469E");
  public static final int TP_COLOR = Color.parseColor("#42B549");
  public static final int PM_COLOR = Color.parseColor("#C6082A");
  public static final int BSM_COLOR = Color.parseColor("#01573C");
  public static final int MNL_COLOR = Color.parseColor("#007053");
  public static final int CIMB_COLOR = Color.parseColor("#ee1c25");
  public static final int MTIX_COLOR = Color.parseColor("#6f4713");
  public static final int KW_COLOR = Color.parseColor("#a51e2f");

  public final static int MD = 1, BC = 2, BN = 3, BR = 4, TP = 5, DP = 6, PM = 7, BSM = 8, MNL = 10,
      CIMB = 11, MTIX = 12;

  public final static String MD_S = "Mandiri", BC_S = "BCA", BN_S = "BNI", BR_S = "BRI", TP_S =
      "Tokopedia", DP_S = "Tunai", PM_S = "Permata", BSM_S = "Syariah Mandiri", MNL_S =
      "Reksa Dana Manulife", CIMB_S = "CIMB Niaga", MTIX_S = "MTIX CiNEPLEX";

  public final static String MD_URL = "https://ib.bankmandiri.co.id", BC_URL =
      "https://ibank.klikbca.com", BN_URL = "https://ibank.bni.co.id", BR_URL =
      "https://ib.bri.co.id", TP_URL = "https://www.tokopedia.com", DP_URL = "dompetsehat.com",
      PM_URL = "https://new.permatanet.com/permatanet/retail/logon", BSM_URL =
      "https://bsmnet.syariahmandiri.co.id", MNL_URL = "https://www.klikmami.com", CIMB_URL =
      "https://www.cimbclicks.co.id", MTIX_URL = "https://mtix.21cineplex.com";

  public static final HashMap<Integer, Integer> accountColor = new HashMap<Integer, Integer>();
  public static final HashMap<Integer, String> vendors = new HashMap<Integer, String>();
  public static final HashMap<Integer, String> urls = new HashMap<Integer, String>();
  public static final HashMap<Integer, String> iconVendor = new HashMap<Integer, String>();
  public static final HashMap<Integer, String> institusi = new HashMap<Integer, String>();
  public static final HashMap<Integer, Integer> logoVendorRes = new HashMap<Integer, Integer>();

  static {

    logoVendorRes.put(MD, -1);
    logoVendorRes.put(BC, -1);
    logoVendorRes.put(BN, -1);
    logoVendorRes.put(BR, -1);
    logoVendorRes.put(TP, -1);
    logoVendorRes.put(DP, -1);
    logoVendorRes.put(PM, -1);
    logoVendorRes.put(BSM, -1);
    logoVendorRes.put(MNL, R.drawable.reksa_dana_manulife);
    logoVendorRes.put(CIMB, -1);
    logoVendorRes.put(MTIX, -1);

    accountColor.put(MD, MD_COLOR);
    accountColor.put(BC, BC_COLOR);
    accountColor.put(BN, BN_COLOR);
    accountColor.put(BR, BR_COLOR);
    accountColor.put(TP, TP_COLOR);
    accountColor.put(DP, DP_COLOR);
    accountColor.put(PM, PM_COLOR);
    accountColor.put(BSM, BSM_COLOR);
    accountColor.put(MNL, MNL_COLOR);
    accountColor.put(CIMB, CIMB_COLOR);
    accountColor.put(MTIX, MTIX_COLOR);

    vendors.put(0, "Error");
    vendors.put(MD, MD_S);
    vendors.put(BC, BC_S);
    vendors.put(BN, BN_S);
    vendors.put(BR, BR_S);
    vendors.put(TP, TP_S);
    vendors.put(DP, DP_S);
    vendors.put(PM, PM_S);
    vendors.put(BSM, BSM_S);
    vendors.put(MNL, MNL_S);
    vendors.put(CIMB, CIMB_S);
    vendors.put(MTIX, MTIX_S);

    urls.put(0, "Error");
    urls.put(MD, MD_URL);
    urls.put(BC, BC_URL);
    urls.put(BN, BN_URL);
    urls.put(BR, BR_URL);
    urls.put(TP, TP_URL);
    urls.put(DP, DP_URL);
    urls.put(PM, PM_URL);
    urls.put(BSM, BSM_URL);
    urls.put(MNL, MNL_URL);
    urls.put(CIMB, CIMB_URL);
    urls.put(MTIX, MTIX_URL);

    iconVendor.put(MD, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(BC, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(BN, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(BR, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(TP, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(DP, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(PM, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(BSM, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(MNL, DSFont.Icon.dsf_manulife.getFormattedName());
    iconVendor.put(CIMB, DSFont.Icon.dsf_bank.getFormattedName());
    iconVendor.put(MTIX, DSFont.Icon.dsf_snack.getFormattedName());

    institusi.put(MNL, "Reksa Dana Manulife");
  }

  public static class Item {

    public static final int TYPE_DUMMY = 0x09;

    public String name;
    public String url;
    public int bgColor;
    public int idKey;

    public Item(int keyId) {
      this.idKey = keyId;

      if (this.idKey == TYPE_DUMMY) return;

      name = vendors.get(keyId);
      url = urls.get(keyId);
      bgColor = accountColor.get(keyId);
    }
  }

  public static int getSection(int account_id) {
    if (account_id == 9) return -1; //skip financial goal

    if (account_id == 1
        || account_id == 2
        || account_id == 3
        || account_id == 4
        || account_id == 7
        || account_id == 8
        || account_id == 11) {
      return AccountPresenter.SECTION_BANK;
    } else if (account_id == 5 || account_id == 12) {
      return AccountPresenter.SECTION_MERCHANT;
    } else if (account_id == 6) {
      return AccountPresenter.SECTION_DOMPET;
    } else if (account_id == 10) {
      return AccountPresenter.SECTION_INVESTMENT;
    } else {
      return -1;
    }
  }
}
