package javasign.com.dompetsehat.ui.fragments.finplan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.Portofolio;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class AdapterPortofolio extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnProtofolioClick{ void onItemClick(View v, Portofolio portofolio, int pos); }

  private List<Portofolio> portofolios;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private OnProtofolioClick onProtofolioClick;


  public AdapterPortofolio(List<Portofolio> portofolios){
    this.portofolios = portofolios;
  }

  public AdapterPortofolio setOnPortofolioClick(OnProtofolioClick onPortofolioClick){
    this.onProtofolioClick = onPortofolioClick;
    return this;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_portofolio, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    Holder h = (Holder) holder;
    Portofolio porto = portofolios.get(position);

    h.v_divider.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    h.tv_title.setText(porto.title);
    h.tv_values_saldo.setText(format.toRupiahFormatSimple(porto.totalSaldoAkhir,true));
    h.tv_total.setText(Helper.DecimalToString(porto.jumlahUnit,4));
    h.tv_date.setText(porto.date);

    h.itemView.setTag(porto);
    h.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(onProtofolioClick == null) return;

        Portofolio porto = (Portofolio) v.getTag();
        onProtofolioClick.onItemClick(v, porto, position);
      }
    });
  }

  @Override public int getItemCount() {
    return portofolios.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_values_saldo) TextView tv_values_saldo;
    @Bind(R.id.tv_total) TextView tv_total;
    @Bind(R.id.tv_date) TextView tv_date;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
