package javasign.com.dompetsehat.ui.activities.transaction.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.itextpdf.text.pdf.GrayColor;
import com.mikepenz.iconics.view.IconicsTextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.activities.tag.adapter.TagListAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import org.zakariya.stickyheaders.SectioningAdapter;
import timber.log.Timber;

/**
 * Created by lafran on 11/21/16.
 */

public class StickyTransactionAdapter extends SectioningAdapter
    implements Filterable, ItemEventHelper.ItemEventInterface {

  private final String PatternWithDaysOfWeek = "EEEE, dd MMMM yyyy";
  private final String PatternWithoutDaysOfWeek = "dd MMM yyyy";

  private Context context;
  private ItemEventHelper itemEventHelper;
  private ArrayList<Transaction> transactions;
  private ArrayList<Transaction> backupTransactions;
  private RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  private SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
  private SimpleDateFormat dateFormat =
      new SimpleDateFormat(PatternWithDaysOfWeek, State.getLocale());

  private int highlightColor = Color.parseColor("#FFD0FFEA");

  public StickyTransactionAdapter(ArrayList<Transaction> transactions) {
    this.transactions = transactions;
  }

  public StickyTransactionAdapter setItemEventHelper(ItemEventHelper itemEventHelper) {
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  public StickyTransactionAdapter setTransactions(ArrayList<Transaction> transactions) {
    this.transactions = transactions;
    this.backupTransactions = new ArrayList<>(transactions);
    return this;
  }

  public ArrayList<Transaction> getBackupTransactions() {
    ArrayList<Transaction> bc = new ArrayList<>();
    for (Transaction transaction : backupTransactions) {
      Transaction bct = new Transaction();
      bct.date = transaction.date;
      bct.amount = transaction.amount;
      bct.cashs = new ArrayList<>(transaction.cashs);
      bc.add(bct);
    }
    return bc;
  }

  public void remove(Cash cash) {
    int section = (int) cash.getTag();
    int itemIdx = transactions.get(section).cashs.indexOf(cash);
    transactions.get(section).cashs.remove(itemIdx);
    notifySectionItemRemoved(section, itemIdx);
  }

  @Override public int getNumberOfSections() {
    return transactions != null ? transactions.size() : 0;
  }

  @Override
  public void onBindGhostHeaderViewHolder(GhostHeaderViewHolder viewHolder, int sectionIndex) {
    viewHolder.itemView.setBackgroundColor(Color.CYAN);
    super.onBindGhostHeaderViewHolder(viewHolder, sectionIndex);
  }

  @Override public int getNumberOfItemsInSection(int section) {
    return transactions.get(section) != null ? transactions.get(section).cashs.size() : 0;
  }

  public int getSectionByCashDate(String cashDate) {
    int i = 0;
    for (Transaction t : transactions) {
      if (t.date.equalsIgnoreCase(cashDate)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  public void changeItem(Cash cash, Category category, int sec,int pos) {
    transactions.get(sec).cashs.get(pos).setCategory_id(category.getId_category()).setCategory(category);
    transactions.get(sec).cashs.get(pos).after_update = true;
    this.notifyDataSetChanged();
  }
  public void changeChidlItem(Cash cash, UserCategory usercategory,int sec,int pos) {
    transactions.get(sec).cashs.get(pos).setUserCategory(usercategory).setCategory(usercategory.getParentCategory());
    transactions.get(sec).cashs.get(pos).after_update = true;
    this.notifyDataSetChanged();
  }

  @Override public boolean doesSectionHaveHeader(int sectionIndex) {
    return true;
  }

  @Override public boolean doesSectionHaveFooter(int sectionIndex) {
    return false;
  }

  @Override public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
    if (parent != null) {
      context = parent.getContext();
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.adapter_transaction_item, parent, false);
      return new StickyTransactionAdapter.Item(view);
    } else {
      return null;
    }
  }

  @Override public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
    if (parent != null) {
      context = parent.getContext();
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.adapter_transaction_header, parent, false);
      return new StickyTransactionAdapter.Header(view);
    } else {
      return null;
    }
  }

  @Override public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder holder, int section,
      int relativePosition, int itemType) {
    if (transactions.size() > section) {
      Transaction set_header = transactions.get(section);
      if (set_header.cashs.size() > relativePosition) {
        Cash cash = set_header.cashs.get(relativePosition);
        cash.setTag(section);
        Item item = (Item) holder;
        item.loadTag(cash.getCash_tag());

        item.v_divider.setVisibility(
            relativePosition == set_header.cashs.size() - 1 ? View.GONE : View.VISIBLE);

        item.tv_values.setText(rcf.toRupiahFormatSimple(cash.getAmount()));
        if (cash.getType().equals("CR")) {
          item.tv_values.setTextColor(ContextCompat.getColor(this.context, R.color.green_600));
        } else {
          item.tv_values.setTextColor(ContextCompat.getColor(this.context, R.color.red_700));
        }
        item.icon.setTextColor(Color.WHITE);
        if (cash.getCategory() != null) {
          if (cash.getCategory().getIcon() != null) {
            item.icon.setText(cash.getCategory().getIcon());
          }
          if (cash.getCategory().getName() != null) {
            item.tv_categori_name.setText(cash.getCategory().getName().toUpperCase());
            if(cash.getUserCategory() != null){
              item.tv_categori_name.setText(cash.getUserCategory().getName().toUpperCase());
            }
          }
        }
        if(cash.getCategory() != null) {
          item.cv_background.setCircleColor(Color.parseColor(cash.getCategory().getColor() != null?cash.getCategory().getColor():"#ddddd"));
        }
        item.tv_transaction_name.setText("");

        item.itemView.setBackgroundColor(cash.getStatus().equalsIgnoreCase("unread") ? highlightColor : Color.WHITE);
        if(cash.after_update){
          item.itemView.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        String detail_text = cash.getCashflow_rename();
        if (TextUtils.isEmpty(detail_text)) {
          detail_text = cash.getDescription();
        }
        if (detail_text.equalsIgnoreCase("-")) {
          item.tv_transaction_name.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
        } else if (!TextUtils.isEmpty(detail_text)) {
          item.tv_transaction_name.setText(Words.toSentenceCase(detail_text));
        } else if (TextUtils.isEmpty(cash.getDescription()) && !TextUtils.isEmpty(cash.getNote())) {
          item.tv_transaction_name.setText(Words.toSentenceCase(cash.getNote()));
        } else {
          item.tv_transaction_name.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
        }
        String accountName = "-";
        if (cash.getAccount() != null) {
          accountName = cash.getAccount().getName().toUpperCase();
        }

        item.tv_account_name.setText(accountName);
        item.tv_note.setText(cash.getDescription().toUpperCase());
        item.itemView.setTag(cash);
        //item.ll_content.setOnClickListener((v) -> {
        //  if (onItemClick != null) {
        //    Cash cash1 = (Cash) v.getTag();
        //    onItemClick.OnClick(v, cash1.getId());
        //  }
        //});

        try {
          Date date = format.parse(set_header.date);
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          dateFormat.applyPattern(PatternWithoutDaysOfWeek);
          item.tv_time.setText(dateFormat.format(date));
        } catch (Exception e) {
          Timber.e("ERROR " + e);
        }
        holder.setIsRecyclable(true);
        if (!Helper.isEmptyObject(getItemEventHelper())) {
          getItemEventHelper().attach(item.itemView, item.cv_background,section,relativePosition);
        }
      }
    }
  }

  @Override
  public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder holder, int sectionIndex,
      int headerType) {
    Transaction set_header = transactions.get(sectionIndex);
    StickyTransactionAdapter.Header header = (StickyTransactionAdapter.Header) holder;
    header.tv_day.setText("");
    try {
      Date date = format.parse(set_header.date);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      dateFormat.applyPattern(PatternWithDaysOfWeek);
      header.tv_day.setText(dateFormat.format(date));
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
    header.setIsRecyclable(false);
    header.tv_values.setText(rcf.toRupiahFormatSimple(set_header.amount));
    Timber.e("section " + sectionIndex);
    header.itemView.setBackgroundColor(Color.WHITE);
  }

  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(CharSequence constraint) {
        transactions = new ArrayList<>(getBackupTransactions());
        List<Transaction> filteredResults = null;
        if (constraint == null || constraint.length() == 0) {
          filteredResults = transactions;
        } else {
          filteredResults = getFilteredResults(constraint.toString().toLowerCase(), transactions);
        }

        FilterResults results = new FilterResults();
        results.values = filteredResults;
        results.count = filteredResults.size();
        return results;
      }

      @Override protected void publishResults(CharSequence constraint, FilterResults results) {
        transactions = (ArrayList<Transaction>) results.values;
        notifyAllSectionsDataSetChanged();
        notifyDataSetChanged();
      }
    };
  }

  protected List<Transaction> getFilteredResults(String constraint, ArrayList<Transaction> data) {
    ArrayList<Transaction> results = new ArrayList<>();
    if (constraint != "" && constraint.length() > 0) {
      String keyword = constraint;
      for (Transaction item : data) {
        ArrayList<Cash> cashes = new ArrayList<>();
        double total = 0.0;
        ArrayList<Cash> cashesMod = new ArrayList<>(item.cashs);
        for (Cash cash : cashesMod) {
          String description = "";
          if (!TextUtils.isEmpty(cash.getDescription())) {
            description = cash.getDescription().toString().toLowerCase();
          }
          String note = "";
          if (cash.getNote() != null) {
            note = cash.getNote().toLowerCase();
          }
          String name = "";
          if (!TextUtils.isEmpty(cash.getCashflow_rename())) {
            name = cash.getCashflow_rename().toLowerCase();
          }

          String tag = "";
          if (!TextUtils.isEmpty(cash.getCash_tag())) {
            tag = cash.getCash_tag().toLowerCase();
          }

          String cat = "";
          if (cash.getCategory() != null) {
            cat = cash.getCategory().getName().toLowerCase();
          }

          if (name.contains(keyword)
              || note.contains(keyword)
              || description.contains(keyword)
              || tag.contains(keyword)
              || cat.contains(keyword)) {

            cashes.add(cash);
            total += cash.getAmount();
          }
        }

        if (cashes.size() > 0) {
          item.cashs = cashes;
          item.amount = total;
          results.add(item);
        }
      }
      return results;
    } else {
      return data;
    }
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    getItemEventHelper().setAdapterOnClickItem(onClickItem);
  }

  /*
  * viewholders
  * */

  public class Header extends SectioningAdapter.HeaderViewHolder {

    @Bind(R.id.tv_day) TextView tv_day;
    @Bind(R.id.tv_total) TextView tv_values;

    public Header(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public RecyclerView.LayoutParams getLayoutParams() {
      return (RecyclerView.LayoutParams) itemView.getLayoutParams();
    }
  }

  class Item extends SectioningAdapter.ItemViewHolder {

    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.tv_total) TextView tv_values;
    @Bind(R.id.tv_transaction_name) TextView tv_transaction_name;
    @Bind(R.id.tv_categori_name) TextView tv_categori_name;
    @Bind(R.id.tv_note) TextView tv_note;
    @Bind(R.id.tv_account_name) TextView tv_account_name;
    @Bind(R.id.tv_time) TextView tv_time;
    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.tv_category_icon) IconicsTextView icon;
    @Bind(R.id.ll_content) LinearLayout ll_content;
    @Bind(R.id.tv_hash_tag) TextView tv_hash_tag;

    public Item(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void loadTag(String cash_tags) {
      if (TextUtils.isEmpty(cash_tags)) {
        tv_hash_tag.setVisibility(View.GONE);
        return;
      }
      final String[] tags = cash_tags.split(",");
      final String hastags = TagListAdapter.extractTohashtags(tags);
      System.out.println("Item.loadTag: hastag -> " + hastags);
      tv_hash_tag.setText(hastags);
      tv_hash_tag.setVisibility(View.VISIBLE);
    }
  }
}
