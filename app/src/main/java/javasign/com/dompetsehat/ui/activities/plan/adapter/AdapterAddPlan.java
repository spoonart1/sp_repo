package javasign.com.dompetsehat.ui.activities.plan.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;

/**
 * Created by bastianbentra on 8/2/16.
 */
public class AdapterAddPlan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnNextClickListener {
    void onClick(View v, int pos);
  }

  private String[] headertexts;
  private String[] footertexts;
  private int[] colorAccents;
  private int[] images = new int[] {
      R.drawable.pensiun, R.drawable.darurat, R.drawable.dana_kuliah, R.drawable.custom
  };
  private OnNextClickListener onNextClickListener;

  public AdapterAddPlan(Context context) {
    this.colorAccents = new int[] {
        ContextCompat.getColor(context,R.color.dana_pensiun_color),
        ContextCompat.getColor(context,R.color.dana_darurat_color),
        ContextCompat.getColor(context,R.color.dana_kuliah_color),
        ContextCompat.getColor(context,R.color.dana_kustom_color)
    };
    this.headertexts =
        context.getResources().getStringArray(R.array.add_plan_option_wording_header);
    this.footertexts =
        context.getResources().getStringArray(R.array.add_plan_option_wording_footer);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View loopView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_add_plan_option, parent, false);
    Holder holder = new Holder(loopView);
    return holder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final Holder h = (Holder) holder;
    String headerWord = headertexts[position];
    String footerWord = footertexts[position];

    h.ic_image.setImageResource(images[position]);
    h.ic_image.setColorFilter(colorAccents[position]);

    h.header_text.setText(headerWord);
    h.header_text.setTextColor(colorAccents[position]);
    h.footer_text.setText(footerWord);

    h.content.setOnClickListener(v -> {
      if (onNextClickListener == null) return;

      onNextClickListener.onClick(v, position);
    });
  }

  public void setOnNextClickListener(OnNextClickListener onNextClickListener) {
    this.onNextClickListener = onNextClickListener;
  }

  @Override public int getItemCount() {
    return headertexts.length;
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.header_text) TextView header_text;
    @Bind(R.id.footer_text) TextView footer_text;
    @Bind(R.id.ic_image) ImageView ic_image;
    @Bind(R.id.ll_content) View content;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
