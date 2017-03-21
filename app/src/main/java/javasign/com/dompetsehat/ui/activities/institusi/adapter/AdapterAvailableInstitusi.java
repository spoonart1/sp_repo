package javasign.com.dompetsehat.ui.activities.institusi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 1/18/17.
 */

public class AdapterAvailableInstitusi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnItemClick{
    void onClick(View view);
  }

  private ArrayList<Model> models;
  private OnItemClick itemClick;

  public AdapterAvailableInstitusi(ArrayList<Model> models) {
    this.models = models;
  }

  public AdapterAvailableInstitusi setOnItemClick(OnItemClick onItemClick){
    this.itemClick = onItemClick;
    return this;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_available_institusi, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    Model m = models.get(position);
    h.icon.setText(m.iconVendor);
    h.header_text.setTextColor(m.headerTextColor);
    h.header_text.setText(m.headerTextTitle);

    h.itemView.setOnClickListener(v -> {
      if(itemClick == null)
        return;

      itemClick.onClick(v);
    });
  }

  @Override public int getItemCount() {
    return models.size();
  }

  class Holder extends RecyclerView.ViewHolder {
    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.header_text) TextView header_text;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class Model{
    public int headerTextColor;
    public String iconVendor;
    public String headerTextTitle;
    public Model(String headerTextTitle, int headerTextColor, String iconVendor){
      this.headerTextTitle = headerTextTitle;
      this.headerTextColor = headerTextColor;
      this.iconVendor = iconVendor;
    }
  }
}
