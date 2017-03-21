package javasign.com.dompetsehat.ui.fragments.referral.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/19/16.
 */
public class AdapterReferral extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final int VIEW_IS_MENU = -1;
  private final int VIEW_IS_FOOTER = 2;
  private List<ModelView> modelViewList;

  private Context context;
  public String kode_referral;
  private ClipboardManager clipboard;
  public String REFERRAL_CODE = "referral_code";

  public AdapterReferral(String[] labels, String[] icons, int[] colors) {
    modelViewList = new ArrayList<>();
    addFooterView();
    for (int i = 0; i < labels.length; i++) {
      ModelView modelView = new ModelView();
      modelView.icon = icons[i];
      modelView.label = labels[i];
      modelView.color = colors[i];
      modelViewList.add(modelView);
    }
  }

  public void setKode_referral(String kode_referral) {
    this.kode_referral = kode_referral;
  }

  private void addFooterView() {
    ModelView modelView = new ModelView();
    modelView.typeView = VIEW_IS_FOOTER;
    modelView.label =
        "Bagikan kode Referral ke teman-teman Anda, dan nikmati komisi setiap kali teman Anda berinvestasi di Reksa Dana Manulife.";
    modelViewList.add(modelView);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    if(clipboard == null) {
      clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }
    View view = null;
    switch (viewType) {
      case VIEW_IS_MENU:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_referral, parent, false);
        return new Holder(view);
      case VIEW_IS_FOOTER:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_referral_footer, parent, false);
        return new FooterHolder(view);
    }

    return null;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    final ModelView modelView = modelViewList.get(position);

    switch (modelView.typeView) {
      case VIEW_IS_MENU:
        Holder h = (Holder) holder;
        h.label.setText(modelView.label);
        switch (position){
          case 1 : //share
            h.label.setTextColor(ContextCompat.getColor(context, R.color.green_manulife));
            h.label.setBackgroundResource(R.drawable.button_manulife_template);
            break;
          case 2: //copy
            h.label.setTextColor(ContextCompat.getColor(context, R.color.yellow_manulife));
            h.label.setBackgroundColor(ContextCompat.getColor(context, R.color.green_manulife));
            break;
        }
        //h.leftIcon.setText(modelView.icon);
        //h.leftIcon.setTextColor(modelView.color);
        if(position == 1) {
          h.itemView.setOnClickListener(v -> {
            //switch (position){
            //  case 0:
            //    // fb
            //    Helper.shareFB(context,kode_referral);
            //    break;
            //  case 1:
            //    // twitter
            //    break;
            //  case 2:
            //    // wa
            //    Helper.shareWA(context,kode_referral);
            //    break;
            //  case 3:
            //    // email
            //    break;
            //}
            Helper.trackThis(context, "User memilih menu "+modelView.label);
            Helper.shareText(context, kode_referral);
            Helper.showCustomSnackBar(v, LayoutInflater.from(context), modelView.label);
          });
        }

        if (position == modelViewList.size() - 1) {
          h.icon.setVisibility(View.GONE);
          h.itemView.setOnClickListener(
              v -> {
                ClipData clip = ClipData.newPlainText(REFERRAL_CODE,kode_referral);
                clipboard.setPrimaryClip(clip);
                Helper.showCustomSnackBar(v, LayoutInflater.from(context), "kode tersalin!");
              });
        } else {
          //h.icon.setVisibility(View.VISIBLE);
        }

        break;
      case VIEW_IS_FOOTER:
        FooterHolder fh = (FooterHolder) holder;
        fh.label.setText(modelView.label);
        break;
    }
  }

  @Override public int getItemCount() {
    return modelViewList.size();
  }

  @Override public int getItemViewType(int position) {
    return modelViewList.get(position).typeView;
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.left_icon) IconicsTextView leftIcon;
    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.label) TextView label;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      icon.setText(DSFont.Icon.dsf_right_chevron_thin.getFormattedName());
    }
  }

  class FooterHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.img) ImageView img;
    @Bind(R.id.label) TextView label;

    public FooterHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  class ModelView {
    int typeView = VIEW_IS_MENU;
    String icon;
    String label;
    int color;
  }
}
