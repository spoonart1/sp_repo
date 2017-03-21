package javasign.com.dompetsehat.ui.activities.search;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.ui.activities.tag.adapter.TagListAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Words;
import org.zakariya.stickyheaders.SectioningAdapter;

/**
 * Created by lafran on 2/6/17.
 */

public class AdapterTableResult extends SectioningAdapter implements Filterable {

  private ArrayList<Cash> cashes;
  private ArrayList<Cash> backupCashes;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
  private OnClickItem onClickItem;

  public AdapterTableResult(ArrayList<Cash> cashes) {
    this.cashes = cashes;
  }

  public AdapterTableResult setTransactions(ArrayList<Cash> transactions) {
    this.cashes = transactions;
    this.backupCashes = new ArrayList<>(transactions);
    return this;
  }

  @Override public int getNumberOfSections() {
    return 1;
  }

  @Override public int getNumberOfItemsInSection(int section) {
    if (cashes != null) {
      return cashes.size();
    } else {
      return 0;
    }
  }

  public AdapterTableResult setOnClickItem(OnClickItem onClickItem) {
    this.onClickItem = onClickItem;
    return this;
  }

  @Override public boolean doesSectionHaveHeader(int sectionIndex) {
    return true;
  }

  @Override public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_child_table_result_search, null);
    return new Child(view);
  }

  @Override public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_header_table_result_search, null);
    return new Header(view);
  }

  @Override public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex,
      int headerUserType) {

  }

  @Override
  public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex,
      int itemUserType) {
    Child child = (Child) viewHolder;
    Cash cash = cashes.get(itemIndex);
    child.tv_date.setText(
        dateFormat.format(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date())));
    child.tv_category.setText(cash.getCategory().getName().toUpperCase());
    if(cash.getUserCategory() != null){
      child.tv_category.setText(cash.getUserCategory().getName().toUpperCase());
    }
    int color = ContextCompat.getColor(((Child) viewHolder).itemView.getContext(), R.color.red_400);
    if (cash.getType().equalsIgnoreCase(Cash.CREDIT)) {
      color = ContextCompat.getColor(((Child) viewHolder).itemView.getContext(),
          R.color.green_dompet_ori);
    }
    child.tv_category.setTextColor(color);
    child.tv_amount.setTextColor(color);
    String[] arr_tags = cash.getCash_tag().split(",");
    List<String> tags = new ArrayList<>(new LinkedHashSet<String>(Arrays.asList(arr_tags)));
    String text_tags = "";
    for (String tag : tags) {
      if (!TextUtils.isEmpty(tag)) {
        text_tags += "#" + tag + " ";
      }
    }
    if (TextUtils.isEmpty(text_tags)) {
      child.tv_hash_tag.setVisibility(View.GONE);
    } else {
      child.tv_hash_tag.setVisibility(View.VISIBLE);
      child.tv_hash_tag.setText(text_tags);
    }
    String detail_text = cash.getCashflow_rename();
    if (TextUtils.isEmpty(detail_text)) {
      detail_text = cash.getDescription();
    }
    if (detail_text.equalsIgnoreCase("-")) {
      child.tv_desc.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
    } else if (!TextUtils.isEmpty(detail_text)) {
      child.tv_desc.setText(Words.toSentenceCase(detail_text));
    } else if (TextUtils.isEmpty(cash.getDescription()) && !TextUtils.isEmpty(cash.getNote())) {
      child.tv_desc.setText(Words.toSentenceCase(cash.getNote()));
    } else {
      child.tv_desc.setText(TagListAdapter.getFormattedTags(cash.getCash_tag()));
    }

    child.tv_amount.setText(RupiahCurrencyFormat.getInstance().toRupiahFormatSimple(cash.getAmount()));
    View.OnClickListener listener = view -> {
      if(onClickItem != null){
        onClickItem.OnClick(cash,itemIndex);
      }
    };
    child.ll_transaction.setOnClickListener(listener);
    child.tv_amount.setOnClickListener(listener);
    child.tv_date.setOnClickListener(listener);
  }

  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(CharSequence constraint) {
        cashes = new ArrayList<>(getBackupTransactions());
        List<Cash> filteredResults = null;
        if (constraint == null || constraint.length() == 0) {
          filteredResults = cashes;
        } else {
          filteredResults = getFilteredResults(constraint.toString().toLowerCase(), cashes);
        }

        FilterResults results = new FilterResults();
        results.values = filteredResults;
        results.count = filteredResults.size();
        return results;
      }

      @Override protected void publishResults(CharSequence constraint, FilterResults results) {
        cashes = (ArrayList<Cash>) results.values;
        notifyAllSectionsDataSetChanged();
        notifyDataSetChanged();
      }
    };
  }

  protected List<Cash> getFilteredResults(String constraint, ArrayList<Cash> data) {
    ArrayList<Cash> results = new ArrayList<>();
    if (constraint != "" && constraint.length() > 0) {
      String keyword = constraint;
      ArrayList<Cash> cashesMod = new ArrayList<>(data);
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

        if (name.contains(keyword) || note.contains(keyword) || description.contains(keyword) || tag
            .contains(keyword) || cat.contains(keyword)) {

          results.add(cash);
        }
      }

      return results;
    } else {
      return data;
    }
  }

  public interface OnClickItem {
    void OnClick(Cash cash, int position);
  }

  public ArrayList<Cash> getBackupTransactions() {
    ArrayList<Cash> bc = new ArrayList<>();
    for (Cash cash : backupCashes) {
      bc.add(cash);
    }
    return bc;
  }

  class Header extends HeaderViewHolder {
    public Header(View itemView) {
      super(itemView);
    }
  }

  class Child extends ItemViewHolder {
    @Bind(R.id.tv_date) TextView tv_date;
    @Bind(R.id.tv_category) TextView tv_category;
    @Bind(R.id.tv_desc) TextView tv_desc;
    @Bind(R.id.tv_hash_tag) TextView tv_hash_tag;
    @Bind(R.id.tv_amount) TextView tv_amount;
    @Bind(R.id.ll_transaction) View ll_transaction;

    public Child(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
