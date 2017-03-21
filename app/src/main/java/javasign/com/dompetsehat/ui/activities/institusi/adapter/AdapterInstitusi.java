package javasign.com.dompetsehat.ui.activities.institusi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.List;
import javasign.com.dompetsehat.R;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class AdapterInstitusi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnInstitusiClick{
    void onClick(View v, Institusi institusi);
  }

  private List<Institusi> institusiList;
  private OnInstitusiClick onInstitusiClick;


  public AdapterInstitusi(List<Institusi> institusiList){
    this.institusiList = institusiList;
  }

  public void setOnInstitusiClick(OnInstitusiClick onInstitusiClick){
    this.onInstitusiClick = onInstitusiClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_institusi, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    Institusi ins = institusiList.get(position);

    h.icon.setText(ins.icon);
    h.icon.setTextColor(ins.color);
    h.tv_label.setText(ins.label);
    h.tv_long_desc.setText(ins.longDesc);

    h.container.setTag(ins);
    h.container.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if(onInstitusiClick == null)
          return;

        Institusi in = (Institusi) v.getTag();
        onInstitusiClick.onClick(v, in);
      }
    });
  }

  @Override public int getItemCount() {
    return institusiList.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_long_desc) TextView tv_long_desc;
    @Bind(R.id.container) View container;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class Institusi{
    public int idVendor;
    public String icon;
    public String label;
    public int color;
    public String longDesc;
  }
}
