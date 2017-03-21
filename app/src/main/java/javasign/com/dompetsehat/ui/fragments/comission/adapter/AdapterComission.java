package javasign.com.dompetsehat.ui.fragments.comission.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.fragments.comission.pojo.Comission;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class AdapterComission extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<Comission> comissions;
  private Context context;
  public RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  public AdapterComission(List<Comission> comissions){
    this.comissions = comissions;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comission, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    Comission comission = comissions.get(position);

    if(position == getItemCount() -1) {
      h.btn_see_all.setVisibility(View.GONE);

      h.btn_see_all.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {

        }
      });
    }
    else {
      h.btn_see_all.setVisibility(View.GONE);
    }

    setZebraColoring(h.itemView, position);

    h.tv_date.setText(comission.date);

    if(comission.debetAmount > 0 || comission.debetAmount < 0) {
      h.tv_value_debet.setText(format.toRupiahFormatSimple(comission.debetAmount,true));
    }

    Timber.e("comission.comissionAmount "+comission.comissionAmount);
    if(comission.comissionAmount > 0 || comission.comissionAmount < 0) {
      h.tv_value_comission.setText(format.toRupiahFormatSimple(comission.comissionAmount,true));
    }
  }

  protected void setZebraColoring(View targetview, int position){
    if(position % 2 == 0){
      targetview.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
    }
    else {
      targetview.setBackgroundColor(Color.WHITE);
    }
  }

  @Override public int getItemCount() {
    return comissions.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_date) TextView tv_date;
    @Bind(R.id.tv_value_comission) TextView tv_value_comission;
    @Bind(R.id.tv_value_debet) TextView tv_value_debet;
    @Bind(R.id.btn_see_all) Button btn_see_all;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
