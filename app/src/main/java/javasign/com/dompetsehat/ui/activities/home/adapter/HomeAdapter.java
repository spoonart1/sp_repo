package javasign.com.dompetsehat.ui.activities.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.utils.State;

/**
 * Created by bastianbentra on 8/2/16.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

  public interface OnNextClickListener{
    void onClick(View v, int pos, String fragmentTagTarget);
    void onIconHelpClick(View v, int pos, String fragmentTagTarget);
  }

  private String[] headertexts;
  private String[] bodytexts;
  private String[] footertexts;
  private String[] fragmentTagsTarget;
  private int[] images = new int[]{R.drawable.myfin,R.drawable.myreferral};
  private OnNextClickListener onNextClickListener;

  public HomeAdapter(Context context){
    this.headertexts = context.getResources().getStringArray(R.array.app_options_wording_header);
    this.bodytexts = context.getResources().getStringArray(R.array.app_options_wording_body);
    this.footertexts = context.getResources().getStringArray(R.array.app_options_wording_footer);
    this.fragmentTagsTarget = new String[]{ State.FLAG_FRAGMENT_TIMELINE, State.FLAG_FRAGMENT_FINPLAN};
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View loopView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_app_opts, parent, false);
    Holder holder = new Holder(loopView);
    return holder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final Holder h = (Holder) holder;
    String headerWord = headertexts[position];
    String bodyWord = bodytexts[position];
    String footerWord = footertexts[position];

    h.ic_image.setImageResource(images[position]);

    h.header_text.setText(headerWord);
    h.body_text.setText(bodyWord);
    h.footer_text.setText(footerWord);

    h.content.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(onNextClickListener == null)
          return;

        onNextClickListener.onClick(v, position, fragmentTagsTarget[position]);
      }
    });

    h.ic_help.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(onNextClickListener == null)
          return;

        onNextClickListener.onIconHelpClick(v, position, fragmentTagsTarget[position]);
      }
    });

  }

  public void setOnNextClickListener(OnNextClickListener onNextClickListener){
    this.onNextClickListener = onNextClickListener;
  }

  @Override public int getItemCount() {
    if(MyCustomApplication.showInvestasi()) {
      return headertexts.length;
    }else{
      return headertexts.length-1;
    }
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.header_text) TextView header_text;
    @Bind(R.id.body_text) TextView body_text;
    @Bind(R.id.footer_text) TextView footer_text;
    @Bind(R.id.ic_image) ImageView ic_image;
    @Bind(R.id.ll_content) View content;
    @Bind(R.id.ic_help) IconicsTextView ic_help;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

  }
}
