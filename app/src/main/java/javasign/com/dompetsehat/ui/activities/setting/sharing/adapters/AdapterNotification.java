package javasign.com.dompetsehat.ui.activities.setting.sharing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.utils.ItemEventHelper;

/**
 * Created by lafran on 1/4/17.
 */

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.NotifHolder> implements
    ItemEventHelper.ItemEventInterface{

  private ItemEventHelper itemEventHelper;
  private ArrayList<NotifModel> notifModels;

  public AdapterNotification(ArrayList<NotifModel> notifModels) {
    this.notifModels = notifModels;
  }

  public void setItemEventHelper(ItemEventHelper itemEventHelper){
    this.itemEventHelper = itemEventHelper;
  }

  public void remove(NotifModel model){
    int index = notifModels.indexOf(model);
    notifModels.remove(model);
    notifyItemRemoved(index);
  }

  @Override public NotifHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notification, parent, false);
    return new NotifHolder(view);
  }

  @Override public void onBindViewHolder(NotifHolder holder, int position) {
    NotifModel model = notifModels.get(position);
    holder.itemView.setTag(model);

    holder.tv_label.setText(model.label);
    holder.tv_desc.setText(model.description);
    holder.tv_time.setText(model.timeDate);

    getItemEventHelper().attach(holder.itemView, null,0,position);
  }

  @Override public int getItemCount() {
    return notifModels.size();
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    getItemEventHelper().setAdapterOnClickItem(onClickItem);
  }

  class NotifHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_desc) TextView tv_desc;
    @Bind(R.id.tv_time) TextView tv_time;
    public NotifHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class NotifModel extends BaseModel{
    String label;
    String description;
    String timeDate;
    public NotifModel(String label, String description, String timeDate){
      this.label = label;
      this.description = description;
      this.timeDate = timeDate;
    }
  }
}
