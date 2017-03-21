package javasign.com.dompetsehat.ui.globaladapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Badges;
import javasign.com.dompetsehat.ui.activities.badges.BadgesActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.view.RoundProgress;
import timber.log.Timber;

/**
 * Created by Spoonart on 9/2/2015.
 */
public class ListAdapterBadges extends BaseAdapter {
  Context context;
  ArrayList<Badges> badgeses;
  DbHelper db;
  float transaction, saldo;
  int badges_id;
  SessionManager session;
  RupiahCurrencyFormat rp = new RupiahCurrencyFormat();
  int i = 0;

  public ListAdapterBadges(Context context, ArrayList<Badges> list, int badges_id,
      float transaction, float saldo) {
    this.context = context;
    this.badgeses = list;
    this.transaction = transaction;
    this.saldo = saldo;
    this.badges_id = badges_id;
    db = DbHelper.getInstance(context);
    session = new SessionManager(context);
  }

  @Override public int getCount() {
    return badgeses.size();
  }

  @Override public Object getItem(int position) {
    return badgeses.get(position);
  }

  @Override public long getItemId(int position) {
    return Long.parseLong(badgeses.get(position).id);
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    this.context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    boolean isProcessing = false;
    boolean isLocked = false;
    boolean isUnlocked = false;

    Badges badges = badgeses.get(position);
    float max = Float.valueOf(badges.firstBarMax);
    float max2 = Float.valueOf(badges.secondBarMax);

    ViewHolder holder;
    rp = new RupiahCurrencyFormat();
    transaction = db.getTransactionCount(Integer.valueOf(session.getIdUser()));
    saldo = db.getTotalSaldoAllAccount(Integer.valueOf(session.getIdUser()));
    DecimalFormat decimalFormat = new DecimalFormat("0.#");

    if (convertView != null) {
      holder = (ViewHolder) convertView.getTag();

      if (holder.position != position) {
        if (Integer.valueOf(badges.id) == badges_id) {
          isProcessing = true;
          convertView = inflater.inflate(R.layout.list_item_badges, null);
        } else if (Integer.valueOf(badges.id) > badges_id) {
          isLocked = true;
          convertView = inflater.inflate(R.layout.list_badge_locked, null);
        } else if (Integer.valueOf(badges.id) < badges_id) {
          isUnlocked = true;
          convertView = inflater.inflate(R.layout.list_badge_unlocked, null);
        }

        holder.position = position;
        convertView.setTag(holder);
      }
    } else {
      holder = new ViewHolder();

      if (Integer.valueOf(badges.id) == badges_id) {
        isProcessing = true;
        convertView = inflater.inflate(R.layout.list_item_badges, null);
      } else if (Integer.valueOf(badges.id) > badges_id) {
        isLocked = true;
        convertView = inflater.inflate(R.layout.list_badge_locked, null);
      } else if (Integer.valueOf(badges.id) < badges_id) {
        isUnlocked = true;
        convertView = inflater.inflate(R.layout.list_badge_unlocked, null);
      }

      holder.position = position;
      convertView.setTag(holder);
    }

    String img_path = "drawable/" + badges.drawable;
    int imageResource =
        context.getResources().getIdentifier(img_path, null, context.getPackageName());
    Drawable image = context.getResources().getDrawable(imageResource);

    if (isLocked) {
      holder.badgesIcon = (ImageView) convertView.findViewById(R.id.badges_icon);
      holder.ll_header = (LinearLayout) convertView.findViewById(R.id.ll_header);
      holder.tv_saldo_title = (TextView) convertView.findViewById(R.id.tv_saldo_title);
      holder.tv_transaction_title = (TextView) convertView.findViewById(R.id.tv_transaction_title);
      holder.tv_badges_name = (TextView) convertView.findViewById(R.id.tv_badges_name);
      holder.ll_bottom_info = (LinearLayout) convertView.findViewById(R.id.ll_bottom_info);
      holder = (ViewHolder) convertView.getTag();

      //set details
      holder.tv_saldo_title.setText(rp.toRupiahFormatSimple(Double.valueOf(badges.firstBarMax)));
      holder.tv_transaction_title.setText(String.valueOf(badges.secondBarMax));
      //grayscale
      image = convertToGrayscale(image);
      holder.badgesIcon.setImageDrawable(image);
      holder.tv_badges_name.setText(badges.title);
      holder.ll_bottom_info.setVisibility(View.GONE);

      ((BadgesActivity) context).addHeaderListener();
    }

    if (isProcessing) {
      holder.tv_badges_status = (TextView) convertView.findViewById(R.id.tv_badges_status);
      holder.badgesIcon = (ImageView) convertView.findViewById(R.id.badges_icon);
      holder.tv_saldo_title = (TextView) convertView.findViewById(R.id.tv_saldo_title);
      holder.tv_transaction_title = (TextView) convertView.findViewById(R.id.tv_transaction_title);
      holder.tv_badges_name = (TextView) convertView.findViewById(R.id.tv_badges_name);
      holder.saldo_bar = (RoundProgress) convertView.findViewById(R.id.saldo_bar);
      holder.transaction_bar = (RoundProgress) convertView.findViewById(R.id.transaction_bar);
      holder = (ViewHolder) convertView.getTag();

      //set value kene
      holder.saldo_bar.setMax(max);
      holder.transaction_bar.setMax(max2);
      holder.tv_badges_name.setText(badges.title);

      if (Integer.valueOf(badges.id) < badges_id) {
        holder.tv_badges_status.setText(context.getString(R.string.in_progress));
        holder.badgesIcon.setImageDrawable(image);

        holder.tv_saldo_title.setText(rp.toRupiahFormatSimple(Double.valueOf(badges.firstBarMax))
            + "/"
            + rp.toRupiahFormatSimple(Double.valueOf(badges.firstBarMax)));
        holder.tv_transaction_title.setText(badges.secondBarMax + "/" + badges.secondBarMax);

        holder.saldo_bar.setProgress(Float.valueOf(badges.firstBarMax));
        holder.transaction_bar.setProgress(Float.valueOf(badges.secondBarMax));
      } else {
        image = convertToGrayscale(image);
        holder.tv_badges_status.setText("Sedang dalam proses");
        holder.badgesIcon.setImageDrawable(image);

        holder.tv_saldo_title.setText(
            rp.toRupiahFormatSimple(saldo) + "/" + rp.toRupiahFormatSimple(
                Double.valueOf(badges.firstBarMax)));
        holder.tv_transaction_title.setText(
            decimalFormat.format(transaction) + "/" + badges.secondBarMax);

        holder.saldo_bar.setProgress(saldo);
        holder.transaction_bar.setProgress(transaction);
      }
    }

    if (isUnlocked) {
      holder.badgesIcon = (ImageView) convertView.findViewById(R.id.badges_icon);
      holder.tv_badges_name = (TextView) convertView.findViewById(R.id.tv_badges_name);
      holder = (ViewHolder) convertView.getTag();

      holder.badgesIcon.setImageDrawable(image);
      holder.tv_badges_name.setText(badges.title);
    }

    return convertView;
  }

  class ViewHolder {
    int position;

    //BezelImageView badgesIcon;
    ImageView badgesIcon;
    ImageView iv_gembok;

    LinearLayout ll_header;
    LinearLayout ll_bottom_info;

    TextView tv_saldo_title;
    TextView tv_transaction_title;
    TextView tv_badges_status;
    TextView tv_badges_name;

    RoundProgress saldo_bar;
    RoundProgress transaction_bar;
  }

  private Drawable convertToGrayscale(Drawable drawable) {
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(0);
    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
    drawable.setColorFilter(filter);
    return drawable;
  }

  @Override public int getItemViewType(int position) {
    return position;
  }

  @Override public int getViewTypeCount() {
    return badgeses.size();
  }
}
