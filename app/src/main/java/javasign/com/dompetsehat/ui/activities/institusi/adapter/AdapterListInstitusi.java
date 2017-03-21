package javasign.com.dompetsehat.ui.activities.institusi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class AdapterListInstitusi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public enum Status{
    terhubung(0), belum_terhubung(1);

    int label;
    int color;
    private Status(int type){
      if(type == 0){
        label = R.string.connected;
        color = Helper.GREEN_DOMPET_COLOR;
      }
      else {
        label = R.string.not_connected;
        color = Color.LTGRAY;
      }
    }
  }

  public interface OnListClick{
    void onItemClick(View v, InstitusiModel model);
  }

  private List<InstitusiModel> institusiModels;
  private OnListClick listClick;
  private Context context;

  public AdapterListInstitusi(List<InstitusiModel> institusiModels){
    this.institusiModels = institusiModels;
  }

  public void setOnListClick(OnListClick listClick){
    this.listClick = listClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_institusi, parent, false);
    return new Holder(v);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    InstitusiModel model = institusiModels.get(position);
    h.itemView.setTag(model);
    h.tv_label.setText(model.name);
    h.tv_status.setText(context.getString(model.status.label));
    h.tv_status.setBackgroundColor(model.status.color);

    h.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(listClick == null) return;

        InstitusiModel model = (InstitusiModel) v.getTag();
        listClick.onItemClick(v, model);
      }
    });
  }

  @Override public int getItemCount() {
    return institusiModels.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_status) TextView tv_status;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class InstitusiModel{
    public String name;
    public Status status;
  }
}
