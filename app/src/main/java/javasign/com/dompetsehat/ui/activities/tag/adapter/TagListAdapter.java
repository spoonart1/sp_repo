package javasign.com.dompetsehat.ui.activities.tag.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.tag.AddTagActivity;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by spoonart on 2/2/16.
 */
public class TagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private ArrayList<TagView> tagViews;

  private int color_state_true;
  private int color_state_false;

  private OnTagSelected onTagSelected;
  private boolean styleForTransactionList = false;
  private boolean fullSpan = false;

  IconicsButton lastSelect;

  public TagListAdapter(Context context, ArrayList<TagView> tagViews, boolean withPlusBtn) {
    this.context = context;
    this.tagViews = tagViews;
    inflater = LayoutInflater.from(context);
    init();

    if (withPlusBtn) addPlusButton();
  }

  public ArrayList<TagView> getTagViews() {
    return tagViews;
  }

  public TagListAdapter withStyleTagForTransactionList(boolean smallstyle, boolean fullSpan) {
    this.styleForTransactionList = smallstyle;
    this.fullSpan = fullSpan;
    return this;
  }

  private void init() {
    color_state_true = context.getResources().getColor(R.color.white);
    color_state_false = context.getResources().getColor(R.color.black);
  }

  private void addPlusButton() {
    TagView tagView = new TagView(DSFont.Icon.dsf_plus.getFormattedName(), 0x001);
    tagView.typeView = TagView.VIEW_AS_DUMMY;
    this.tagViews.add(tagView);
  }

  public void AddTag(TagView tagView) {
    tagViews.add(0, tagView);
    notifyDataSetChanged();
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    linearLayoutManager.scrollToPosition(0);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    switch (viewType) {
      case TagView.VIEW_AS_DUMMY:
        itemView = inflater.inflate(R.layout.adapter_tag_smaller, parent, false);
        return new TagHolder(itemView);

      case TagView.VIEW_AS_TAG:
        if (!styleForTransactionList) {
          itemView = inflater.inflate(R.layout.adapter_tag, parent, false);
        } else {
          itemView = inflater.inflate(R.layout.adapter_tag_for_transaction_list, parent, false);
        }
        return new TagHolder(itemView);
    }

    return null;
  }

  @Override public long getItemId(int position) {
    return tagViews.get(position).idTag;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    TagHolder th = (TagHolder) holder;
    final TagView tagView = tagViews.get(position);
    switch (tagView.typeView) {
      case TagView.VIEW_AS_DUMMY:
        th.btn.setText(tagView.tag);
        th.btn.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            //            context.startActivity(new Intent(context, AddTagActivity.class));
            ((AddTransactionActivity) context).startActivityForResult(
                new Intent(context, AddTagActivity.class), AddTransactionActivity.REGUEST_CODE_TAG);
          }
        });
        break;

      case TagView.VIEW_AS_TAG:
        th.btn.setText(tagView.tag.toUpperCase());
        th.btn.setTag(tagView);

        if (fullSpan) {
          StaggeredGridLayoutManager.LayoutParams layoutParams =
              (StaggeredGridLayoutManager.LayoutParams) th.itemView.getLayoutParams();
          layoutParams.setFullSpan(true);
        }

        if (styleForTransactionList) break;

        setButtonSelected(th.btn, tagView, tagView.statePressed, false);
        th.btn.setOnClickListener(new View.OnClickListener() {

          IconicsTextView clicked;

          @Override public void onClick(View v) {

            if (onTagSelected != null) {
              clicked = (IconicsTextView) v;
              TagView target = (TagView) clicked.getTag();
              if (clicked.isSelected()) {
                setButtonSelected(clicked, target, false, true);
              } else {
                setButtonSelected(clicked, target, true, true);
              }
            }
          }
        });

        break;
    }
  }

  public void setOnTagSelected(OnTagSelected onTagSelected) {
    this.onTagSelected = onTagSelected;
  }

  protected synchronized void setButtonSelected(IconicsTextView btn, TagView tagView, boolean select, boolean broadcast) {
    btn.setSelected(select);
    tagView.statePressed = select;
    if (broadcast) onTagSelected.tagSelected(btn, tagView.idTag, btn.isSelected());
    if (btn.isSelected()) {
      btn.setTextColor(color_state_true);
      return;
    }

    btn.setTextColor(color_state_false);
    return;
  }

  public interface OnTagSelected {
    void tagSelected(View v, int idTag, boolean isSelected);
  }

  @Override public int getItemCount() {
    return tagViews.size();
  }

  @Override public int getItemViewType(int position) {
    return tagViews.get(position).typeView;
  }

  class TagHolder extends RecyclerView.ViewHolder {
    IconicsTextView btn;

    public TagHolder(View itemView) {
      super(itemView);
      btn = (IconicsTextView) itemView.findViewById(R.id.btn_tag);
    }
  }

  public static String getFormattedTags(String tag) {
    String[] tags = tag.split(",");
    tags = Helper.UCWord(tags);
    String result = Helper.combinePlural(tags);
    return result;
  }

  public static class TagView {
    public static final int VIEW_AS_DUMMY = 99;
    public static final int VIEW_AS_TAG = 88;

    public boolean statePressed = false;
    public int typeView = VIEW_AS_TAG;

    private String tag;
    private int idTag;

    public TagView(String tag, int idTag) {
      this.tag = tag;
      this.idTag = idTag;
    }
  }

  public static String extractTohashtags(String[] tags) {
    String result = "";
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        result += "#" + tags[i];
      } else {
        result += " #" + tags[i];
      }
    }

    return result;
  }
}
