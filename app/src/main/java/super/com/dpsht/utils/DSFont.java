package javasign.com.dompetsehat.utils;

/**
 * Created by spoonart on 2/17/16.
 */

import android.content.Context;
import android.graphics.Typeface;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class DSFont implements ITypeface {
  private static final String TTF_FILE = "ds_font.ttf";
  private static Typeface typeface = null;
  private static HashMap<String, Character> mChars;

  private static DSFont dsFont;
  public static DSFont getInstance(){
    if(dsFont == null){
      dsFont = new DSFont();
    }

    return dsFont;
  }

  @Override public IIcon getIcon(String key) {
    return Icon.valueOf(key);
  }

  @Override public HashMap<String, Character> getCharacters() {
    if (mChars == null) {
      HashMap<String, Character> aChars = new HashMap<String, Character>();
      for (Icon v : Icon.values()) {
        aChars.put(v.name(), v.character);
      }
      mChars = aChars;
    }
    return mChars;
  }

  @Override public String getMappingPrefix() {
    return "dsf";
  }

  @Override public String getFontName() {
    return "DSIcon";
  }

  @Override public String getVersion() {
    return "1.0.0.1";
  }

  @Override public int getIconCount() {
    return mChars.size();
  }

  @Override public Collection<String> getIcons() {
    Collection<String> icons = new LinkedList<String>();
    for (Icon value : Icon.values()) {
      icons.add(value.name());
    }
    return icons;
  }

  @Override public String getAuthor() {
    return "Spoonart";
  }

  @Override public String getUrl() {
    return "http://dompetsehat.com/";
  }

  @Override public String getDescription() {
    return "nothing to explain :)";
  }

  @Override public String getLicense() {
    return "DS license";
  }

  @Override public String getLicenseUrl() {
    return "-";
  }

  @Override public Typeface getTypeface(Context context) {
    if (typeface == null) {
      try {
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + TTF_FILE);
      } catch (Exception e) {
        return null;
      }
    }
    return typeface;
  }

  public enum Icon implements IIcon {
    dsf_business('\ue900'),
    dsf_selling('\ue901'),
    dsf_money_coin('\ue902'),
    dsf_bonus('\ue902'),
    dsf_bills('\ue904'),
    dsf_cosmetics('\ue905'),
    dsf_entertainment('\ue906'),
    dsf_shopping('\ue907'),
    dsf_phone('\ue908'),
    dsf_cafe('\ue909'),
    dsf_food('\ue90a'),
    dsf_fuel('\ue90b'),
    dsf_birthday('\ue90c'),
    dsf_general('\ue90d'),
    dsf_hobby('\ue90e'),
    dsf_education('\ue90f'),
    dsf_sport('\ue910'),
    dsf_finance('\ue911'),
    dsf_inn('\ue912'),
    dsf_gift('\ue913'),
    dsf_treatment('\ue914'),
    dsf_traveling('\ue915'),
    dsf_shipping('\ue916'),
    dsf_transport('\ue917'),
    dsf_healthy('\ue918'),
    dsf_tools('\ue919'),
    dsf_house('\ue91a'),
    dsf_budget('\ue91b'),
    dsf_home('\ue91c'),
    dsf_reminder('\ue91d'),
    dsf_restaurant('\ue91e'),
    dsf_investment_2('\ue929'),
    dsf_my_plan('\ue92a'),
    dsf_transactions('\ue92b'),
    dsf_referral('\ue92c'),
    dsf_right_chevron_thin('\ue92d'),
    dsf_down_chevron_thin('\ue92e'),
    dsf_up_chevron_thin('\ue92f'),
    dsf_lock('\ue94d'),
    dsf_feature('\ue920'),
    dsf_information('\ue921'),
    dsf_snack('\ue922'),
    dsf_kids('\ue923'),
    dsf_parking('\ue924'),
    dsf_badge('\ue925'),
    dsf_check_bold('\ue926'),
    dsf_paper_plane('\ue927'),
    dsf_overview('\ue928'),
    dsf_investment('\ue929'),
    dsf_left_chevron_thin('\ue930'),
    dsf_overview_2('\ue931'),
    dsf_timeline('\ue932'),
    dsf_arrow_back('\ue933'),
    dsf_filter('\ue934'),
    dsf_search('\ue935'),
    dsf_plus('\ue936'),
    dsf_dots_horizontal('\ue937'),
    dsf_note('\ue938'),
    dsf_location('\ue939'),
    dsf_date('\ue93a'),
    dsf_category('\ue93b'),
    dsf_plus_circle('\ue93c'),
    dsf_parent_category('\ue93d'),
    dsf_check('\ue93e'),
    dsf_handphone('\ue93f'),
    dsf_email('\ue940'),
    dsf_fb('\ue941'),
    dsf_portofolio('\ue942'),
    dsf_comission('\ue943'),
    dsf_referral_ref('\ue944'),
    dsf_finplan('\ue945'),
    dsf_myfin('\ue946'),
    dsf_myreferral('\ue96b'),
    dsf_danapensiun('\ue974'),
    dsf_danadarurat('\ue975'),
    dsf_danacustome('\ue976'),
    dsf_whatsapp('\ue97a'),
    dsf_reminder_2('\ue97b'),
    dsf_budget_2('\ue97c'),
    dsf_email_2('\ue97d'),
    dsf_fb_2('\ue97e'),
    dsf_manulife('\ue97f'),
    dsf_feedback('\ue977'),
    dsf_setting('\ue978'),
    dsf_twitter('\ue979'),
    dsf_delete_all('\ue980'),
    dsf_clock('\ue981'),
    dsf_exportalldata('\ue982'),
    dsf_faqs('\ue983'),
    dsf_privacypolicy('\ue984'),
    dsf_securitypractice('\ue985'),
    dsf_switchmode('\ue986'),
    dsf_synchronizedata('\ue987'),
    dsf_termofuse('\ue988'),
    dsf_bugs_circle('\ue989'),
    dsf_information_circle('\ue98a'),
    dsf_feature_circle('\ue98b'),
    dsf_bank('\ue98c'),
    dsf_help('\ue98d'),
    dsf_referral_filled('\ue98e'),
    dsf_comission_filled('\ue98f'),
    dsf_finplan_filled('\ue990'),
    dsf_dots_horizontal_filled('\ue991'),
    dsf_timeline_filled('\ue992'),
    dsf_overview_filled('\ue993'),
    dsf_budget_filled('\ue994'),
    dsf_portofolio_filled('\ue995'),
    dsf_edit('\ue996'),
    dsf_logout('\ue950'),
    dsf_worldwide('\ue951'),
    dsf_danakuliah('\ue952'),
    dsf_notification('\ue953'),
    dsf_notification_2('\ue95a'),
    dsf_check_circle('\ue95b'),
    dsf_hourglass('\ue954'),
    dsf_money_paper('\ue955'),
    dsf_pot_money('\ue956'),
    dsf_camera('\ue957'),
    dsf_empty_person('\ue997'),
    dsf_copy('\ue999'),
    dsf_information_outline('\ue99a'),
    dsf_transactions_filled('\ue99b'),
    dsf_download('\ue99c'),
    dsf_calculator('\ue99d'),
    dsf_callendar_2('\ue99e'),
    dsf_tax('\ue9a0'),
    dsf_job_n_business('\ue99f'),
    dsf_wallet('\uf102'),
    dsf_wallet_2('\ue9a1'),
    dsf_wallet_3('\ue947'),
    dsf_chat_bubble('\ue948'),
    dsf_fee('\uf103'),
    dsf_gender('\uf104'),
    dsf_bank_old('\uf105'),
    dsf_callendar('\uf106'),
    dsf_koinworks('\ue958'),
    dsf_trx_bank('\ue959'),
    dsf_credit_card('\ue95c'),
    dsf_insert_card_vertical('\ue962'),
    dsf_insert_card_horizontal('\ue969'),
    dsf_arrow_chevron_down('\uf110');

    char character;

    Icon(char character) {
      this.character = character;
    }

    public String getFormattedName() {
      return "{" + name() + "}";
    }

    public char getCharacter() {
      return character;
    }

    public String getName() {
      return name();
    }

    // remember the typeface so we can use it later
    private static ITypeface typeface;

    public ITypeface getTypeface() {
      if (typeface == null) {
        typeface = new DSFont();
      }
      return typeface;
    }
  }
}
