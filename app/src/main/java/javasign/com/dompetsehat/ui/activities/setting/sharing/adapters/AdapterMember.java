package javasign.com.dompetsehat.ui.activities.setting.sharing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.utils.ItemEventHelper;

/**
 * Created by lafran on 1/4/17.
 */

public class AdapterMember extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    ItemEventHelper.ItemEventInterface{

  public interface OnActionDeleteListener{
    void onTrashClick(View v, Member item);
  }

  private ArrayList<Member> members;
  private ItemEventHelper itemEventHelper;
  private OnActionDeleteListener deleteListener;

  public AdapterMember(ArrayList<Member> members){
    this.members = members;
  }

  public void addMember(Member member){
    members.add(0, member);
    notifyItemInserted(0);
  }

  public void remove(Member member){
    int index = members.indexOf(member);
    members.remove(member);
    notifyItemRemoved(index);
  }

  public void setItemEventHelper(ItemEventHelper itemEventHelper){
    this.itemEventHelper = itemEventHelper;
  }

  public void setDeleteListener(OnActionDeleteListener deleteListener){
    this.deleteListener = deleteListener;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_member, parent, false);
    return new MemberHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    MemberHolder mh = (MemberHolder) holder;
    Member member = members.get(position);
    mh.itemView.setTag(member);

    mh.tv_label.setText(member.name);
    mh.tv_status.setText(member.status);

    /*mh.btn_delete.setTag(member);
    mh.btn_delete.setOnClickListener(v -> {
      if(deleteListener != null){
        Member m = (Member) v.getTag();
        deleteListener.onTrashClick(v, m);
      }
    });*/

    itemEventHelper.attach(mh.itemView, null,0,position);
  }

  @Override public int getItemCount() {
    return members.size();
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    itemEventHelper.setAdapterOnClickItem(onClickItem);
  }

  class MemberHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_status) TextView tv_status;
    //@Bind(R.id.btn_delete) IconicsTextView btn_delete;
    public MemberHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class Member extends BaseModel{
    public int id;
    public String name;
    public String status;
    public HashMap<String,Boolean> permission;
    public Member(int id,String name,HashMap<String,Boolean> permission,String status){
      this.id = id;
      this.name = name;
      this.status = status;
      this.permission = permission;
    }
  }
}
