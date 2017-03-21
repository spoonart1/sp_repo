package javasign.com.dompetsehat.ui.fragments.timeline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.activities.tag.adapter.TagListAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Words;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/8/16.
 */
public class NewTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private TimelineView timelineView;
  private boolean hideCategoryLabel = false;
  private Context context;
  private RupiahCurrencyFormat format = new RupiahCurrencyFormat();
  SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
  OnClickItem onClickItem;
  private boolean isHideAccountName = false;
  private boolean isMultilines = false;

  public NewTimelineAdapter(TimelineView timelineView) {
    this.timelineView = timelineView;
  }

  public NewTimelineAdapter setOnClickItem(OnClickItem onClickItem) {
    this.onClickItem = onClickItem;
    return this;
  }

  /*hide category's name default is false
  * */
  public NewTimelineAdapter setHideCategoryLabel(boolean hideCategoryLabel) {
    this.hideCategoryLabel = hideCategoryLabel;
    return this;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_timeline_item, parent, false);
    ItemHolder ih = new ItemHolder(view);
    return ih;
  }

  public NewTimelineAdapter setHideAccountName(boolean hideAccountName) {
    isHideAccountName = hideAccountName;
    return this;
  }

  public NewTimelineAdapter setMultilines(boolean multilines) {
    isMultilines = multilines;
    return this;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ItemHolder ih = (ItemHolder) holder;
    int visibility = position == getItemCount() - 1 ? View.GONE : View.VISIBLE;
    ih.v_divider.setVisibility(visibility);

    Cash cash = timelineView.cashs.get(position);
    String type = cash.getType();

    String description = cash.getCashflow_rename();
    if (TextUtils.isEmpty(description)) {
      description = cash.getDescription();
    }
    if (description.equalsIgnoreCase("-")) {
      ih.tv_transaction_name.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
    } else if (!TextUtils.isEmpty(description)) {
      ih.tv_transaction_name.setText(Words.toSentenceCase(description));
    } else if (TextUtils.isEmpty(description) && !TextUtils.isEmpty(cash.getNote())) {
      ih.tv_transaction_name.setText(Words.toSentenceCase(cash.getNote()));
    } else {
      ih.tv_transaction_name.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
    }

    String accountName = "-";
    if (cash.getAccount() != null) {
      if (cash.getAccount().getName() != null) {
        accountName = cash.getAccount().getName();
      }
    }
    if (cash.getCategory() != null) {
      if (cash.getCategory().getName() != null) {
        if (!hideCategoryLabel) {
          String html =
              accountName.toUpperCase() + " &bull; " + cash.getCategory().getName().toUpperCase();
          if (Build.VERSION.SDK_INT >= 24) {
            ih.tv_account_name.setText(Html.fromHtml(html, 1)); // for 24 api and more
          } else {
            ih.tv_account_name.setText(Html.fromHtml(html)); // or for older api
          }
        } else {
          ih.tv_account_name.setText(accountName.toUpperCase());
        }
      }
    }

    ih.tv_note.setText(cash.getDescription().toUpperCase());
    String kalendar = "";
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateParser.parse(cash.getCash_date()));
      int tgl = calendar.get(Calendar.DATE);
      int bln = calendar.get(Calendar.MONTH);
      int thn = calendar.get(Calendar.YEAR);
      String bulan = Helper.MONTHS_SHORT[bln];
      kalendar = tgl + " " + bulan + " " + thn;
      ih.tv_time.setText(kalendar);
    } catch (Exception e) {
      ih.tv_time.setText("");
    }
    if (cash.getCategory() != null) {
      if(cash.getCategory().getIcon() != null) {
        ih.tv_category_icon.setText(cash.getCategory().getIcon());
      }
    }
    ih.cv_background.setCircleColor(Color.parseColor(cash.getCategory().getColor()));
    ih.tv_category_icon.setTextColor(Color.WHITE);
    if (type.equalsIgnoreCase("DB")) {
      ih.tv_values.setTextColor(ContextCompat.getColor(this.context, R.color.red_700));
      ih.tv_values.setText("-" + format.toRupiahFormatSimple(cash.getAmount()));
    } else {
      ih.tv_values.setTextColor(ContextCompat.getColor(this.context, R.color.green_600));
      ih.tv_values.setText(format.toRupiahFormatSimple(cash.getAmount()));
    }

    ih.itemView.setOnClickListener(view -> {
      Timber.e("avesina ");
      if (onClickItem != null) {
        onClickItem.onClick(view, cash,0,position);
      }
    });

    ih.itemView.setOnLongClickListener(view -> {
      if (onClickItem != null) {
        onClickItem.onLongClick(view, cash, 0, position);
      }
      return false;
    });

    ih.cv_background.setOnClickListener(v -> {
      if (onClickItem != null) {
        onClickItem.onCategoryClick(v, cash, position);
      }
    });
    if (cash.after_update) {
      ih.itemView.setBackgroundColor(Color.parseColor("#dddddd"));
    }

    if(isHideAccountName){
      ih.tv_account_name.setVisibility(View.GONE);
    }

    if(isMultilines){
      ih.tv_transaction_name.setSingleLine(false);
    }else{
      ih.tv_transaction_name.setLines(1);
    }
  }

  @Override public int getItemCount() {
    return timelineView.cashs.size();
  }

  public void changeItem(Cash cash, Category category) {
    int i = timelineView.cashs.indexOf(cash);
    if (i > -1) {
      timelineView.cashs.get(i).setCategory_id(category.getId_category()).setCategory(category);
      timelineView.cashs.get(i).after_update = true;
      this.notifyDataSetChanged();
    }
  }

  public void changeItem(int pos, Cash cash) {
    timelineView.cashs.set(pos, cash);
    this.notifyDataSetChanged();
  }

  public void changeChidlItem(Cash cash, UserCategory usercategory) {
    int i = timelineView.cashs.indexOf(cash);
    if (i > -1) {
      timelineView.cashs.get(i)
          .setCategory(usercategory.getParentCategory())
          .setUserCategory(usercategory);
      timelineView.cashs.get(i).after_update = true;
      this.notifyDataSetChanged();
    }
  }

  class ItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.tv_total) TextView tv_values;
    @Bind(R.id.tv_transaction_name) TextView tv_transaction_name;
    @Bind(R.id.tv_note) TextView tv_note;
    @Bind(R.id.tv_account_name) TextView tv_account_name;
    @Bind(R.id.tv_time) TextView tv_time;
    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.tv_category_icon) IconicsTextView tv_category_icon;

    public ItemHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnClickItem {
    void onClick(View v, Cash cash, int section, int position);

    void onLongClick(View v, Cash cash, int section, int position);

    void onCategoryClick(View v, Cash cash, int position);
  }
}
