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
 * Created by lafran on 1/3/17.
 */

public class AdapterSharingData extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    ItemEventHelper.ItemEventInterface{

  private ArrayList<SharingModel> models;
  private ItemEventHelper itemEventHelper;

  public AdapterSharingData(ArrayList<SharingModel> models){
    this.models = models;
  }

  public void remove(SharingModel model){
    int index = models.indexOf(model);
    this.models.remove(model);
    notifyItemRemoved(index);
  }

  public void setItemEventHelper(ItemEventHelper itemEventHelper){
    this.itemEventHelper = itemEventHelper;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sharing_data, parent, false);
    return new SharingHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    SharingHolder holder = (SharingHolder) h;
    SharingModel model = models.get(position);
    holder.tv_label.setText(model.groupName);
    holder.itemView.setTag(model);
    itemEventHelper.attach(holder.itemView, null,0,position);
  }

  @Override public int getItemCount() {
    return models.size();
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    itemEventHelper.setAdapterOnClickItem(onClickItem);
  }

  class SharingHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_label) TextView tv_label;
    public SharingHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class SharingModel extends BaseModel{
    public String groupName;
    public SharingModel(String groupName){
      this.groupName = groupName;
    }
  }
}
