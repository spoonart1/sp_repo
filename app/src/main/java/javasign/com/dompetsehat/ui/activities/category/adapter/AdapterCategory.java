package javasign.com.dompetsehat.ui.activities.category.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/12/16.
 */
public class AdapterCategory extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>
    implements Filterable {

  private OnCategoryClick onCategoryClick;
  private Context context;
  private List<CategoryBinder> categoryBinders;
  private List<CategoryBinder> mostUsedBinders;
  private List<CategoryBinder> originalBinders;
  private int index = -1;
  private int categoryType = ListCategoryFragment.EXPENSE_CATEGORY;
  private HashMap<Integer, List<UserCategory>> userCategories;

  public AdapterCategory(Context context, List<Category> mostRecentUsedCategory,
      List<Category> parentCategories, HashMap<Integer, List<UserCategory>> userCategories,
      OnCategoryClick listener, int categoryType) {

    categoryBinders = new ArrayList<>();
    originalBinders = new ArrayList<>();
    this.context = context;
    this.categoryType = categoryType;
    this.onCategoryClick = listener;
    this.userCategories = userCategories;

    bindMostUsedCategories(mostRecentUsedCategory); //check if most recent category is exist
    bindParentCategories(parentCategories); //add parent category

    this.shouldShowHeadersForEmptySections(true);

    removeAndSaveOriginalBinders();
  }

  private void removeAndSaveOriginalBinders() {
    originalBinders.addAll(categoryBinders);
    if (mostUsedBinders != null && !mostUsedBinders.isEmpty()) {
      originalBinders.removeAll(mostUsedBinders);
    }
  }

  private void bindMostUsedCategories(List<Category> mostRecentUsedCategory) {
    if (mostRecentUsedCategory != null && mostRecentUsedCategory.size() > 0) {
      for (Category category : mostRecentUsedCategory) {
        CategoryBinder mostUsedBinder = new CategoryBinder();
        mostUsedBinder.initialCategory = category;
        mostUsedBinder.isMostUsed = true;
        categoryBinders.add(mostUsedBinder);
      }
      index++;
      categoryBinders.get(index).title = "";

      mostUsedBinders = new ArrayList<>();
      mostUsedBinders.addAll(categoryBinders);
    }
  }

  private void bindParentCategories(List<Category> parentCategories) {

    if (parentCategories != null && parentCategories.size() > 0) {
      for (Category c : parentCategories) {
        CategoryBinder initialParent = new CategoryBinder();
        initialParent.initialCategory = c;
        initialParent.initialCategory.setDescription(c.getDescription());
        initialParent.userCategories = userCategories.get(c.getId_category());
        categoryBinders.add(initialParent);
      }
      index++;
    }

    //header title
    String title = context.getString(R.string.all_expense_category);
    try {
      if (categoryType == ListCategoryFragment.EXPENSE_CATEGORY) {
        title = context.getString(R.string.all_expense_category);
      }else if(categoryType == ListCategoryFragment.INCOME_CATEGORY){
        title = context.getString(R.string.all_income_category);
      }else {
        title = context.getString(R.string.all_income_transfer);
      }
      categoryBinders.get(index).title = title;
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
  }

  public void setOnCategoryClick(OnCategoryClick onCategoryClick) {
    this.onCategoryClick = onCategoryClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = null;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_category_header, parent, false);
        HeadHolder headHolder = new HeadHolder(view);
        return headHolder;
      case VIEW_TYPE_ITEM:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_category_item, parent, false);
        ChildHolder holder = new ChildHolder(view);
        return holder;
    }
    return null;
  }

  @Override public int getSectionCount() {
    if (categoryBinders != null) {
      return categoryBinders.size();
    } else {
      return 1;
    }
  }

  @Override public int getItemCount(int section) {
    if (categoryBinders != null) {
      if (categoryBinders.get(section).userCategories != null) {
        return categoryBinders.get(section).userCategories.size();
      }
    }
    return 0;
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
    HeadHolder h = (HeadHolder) holder;
    h.hideTitle(section != index);
    CategoryBinder binder = categoryBinders.get(section);
    h.tv_desc.setVisibility(binder.isMostUsed ? View.GONE : View.VISIBLE);
    h.tv_desc.setText(TextUtils.isEmpty(binder.initialCategory.getDescription()) ? ""
        : context.getString(R.string.example)
            + ": "
            + binder.initialCategory.getDescription()
            + ", "
            + context.getString(R.string.etc));
    h.tv_title.setText(binder.title);
    h.icr_category.setIconCode(binder.initialCategory.getIcon());
    h.icr_category.setBackgroundColorIcon(Color.parseColor(binder.initialCategory.getColor()));
    h.tv_categori_name.setText(binder.initialCategory.getName());

    h.ll_content.setTag(section);
    h.ll_content.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onCategoryClick == null) return;

        int _section = (int) v.getTag();
        onCategoryClick.onClickParent(categoryBinders.get(_section).initialCategory, _section);
      }
    });

    h.ll_content.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        if(onCategoryClick == null) return false;
        int _section = (int) view.getTag();
        onCategoryClick.onLongClickParent(categoryBinders.get(_section).initialCategory,_section);
        return true;
      }
    });
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition,
      int absolutePosition) {
    ChildHolder ch = (ChildHolder) holder;

    if (categoryBinders.get(section).userCategories.isEmpty()) return;

    UserCategory userCategory = categoryBinders.get(section).userCategories.get(relativePosition);
    userCategory.setParentCategory(categoryBinders.get(section).initialCategory);

    ch.tv_categori_name.setText(userCategory.getName());
    ch.ll_content.setTag(userCategory);
    ch.ll_content.setTag(R.id.position, relativePosition);
    ch.ll_content.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onCategoryClick == null) return;

        int pos = (int) v.getTag(R.id.position);
        UserCategory UC = (UserCategory) v.getTag();
        onCategoryClick.onClickChild(UC, pos);
      }
    });
  }

  class ChildHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_categori_name) TextView tv_categori_name;
    @Bind(R.id.ll_content) LinearLayout ll_content;

    public ChildHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  class HeadHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_desc) TextView tv_desc;
    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_categori_name) TextView tv_categori_name;
    @Bind(R.id.ll_content) LinearLayout ll_content;

    public HeadHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void hideTitle(boolean hide) {
      tv_title.setVisibility(hide ? View.GONE : View.VISIBLE);
    }
  }

  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(CharSequence constraint) {
        List<CategoryBinder> filteredResults = new ArrayList<>();

        if (!TextUtils.isEmpty(constraint)) {
          filteredResults = getFilteredResults(constraint.toString().toLowerCase());
        } else {
          filteredResults = categoryBinders;
        }

        FilterResults results = new FilterResults();
        results.values = filteredResults;

        return results;
      }

      @Override protected void publishResults(CharSequence constraint, FilterResults results) {
        List<CategoryBinder> resultBinders = (List<CategoryBinder>) results.values;
        categoryBinders = resultBinders;
        if (TextUtils.isEmpty(constraint)) {
          categoryBinders.clear();
          categoryBinders.addAll(originalBinders);
        }
        notifyDataSetChanged();
      }
    };
  }

  protected List<CategoryBinder> getFilteredResults(String keyword) {
    List<CategoryBinder> results = new ArrayList<>();
    for (CategoryBinder binder : originalBinders) {
      boolean isFound = false;
      boolean isFoundAtChild = false;

      if (binder.userCategories != null) {
        for (UserCategory userCategory : binder.userCategories) {
          if (userCategory != null) {
            String name =
                binder.initialCategory.getName().toLowerCase() + "|" + userCategory.getName()
                    .toLowerCase();
            if (!TextUtils.isEmpty(binder.initialCategory.getDescription())) {
              name += "|" + binder.initialCategory.getDescription().toLowerCase();
            }
            if (name.contains(keyword.toLowerCase())) {
              isFoundAtChild = true;
            }
          }
        }
      }

      if (!isFoundAtChild) {
        String name = binder.initialCategory.getName().toLowerCase();
        if (!TextUtils.isEmpty(binder.initialCategory.getDescription())) {
          name += "|" + binder.initialCategory.getDescription().toLowerCase();
        }
        if (name.contains(keyword)) {
          isFound = true;
        }
      }

      if (isFound || isFoundAtChild) {
        results.add(binder);
      }
    }
    return results;
  }

  public interface OnCategoryClick {
    void onClickParent(Category category, int section);

    void onClickChild(UserCategory userCategory,int position);

    void onLongClickParent(Category category, int section);
  }

  class CategoryBinder {
    String title = "";
    boolean isMostUsed = false;
    Category initialCategory;
    List<UserCategory> userCategories = new ArrayList<>();
  }
}
