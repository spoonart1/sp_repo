package javasign.com.dompetsehat.ui.activities.tag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.tag.TagInterface;
import javasign.com.dompetsehat.presenter.tag.TagPresenter;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by avesina on 2/2/17.
 */

public class TagActivity extends BaseActivity implements TagInterface {
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Inject TagPresenter presenter;
  private BlankView bv;
  private ItemEventHelper<ReminderModel> itemEventHelper;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tags);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    bv = new BlankView(ButterKnife.findById(this, R.id.rootview),
        DSFont.Icon.dsf_reminder_2.getFormattedName(),
        "Anda belum memiliki kostum tagar, silahkan tambah tagar di "
            + "menu Tambah Transaksi dan tambahkan ke transaksi Anda.");
    setTitle("Kelola Tagar");
    itemEventHelper = ItemEventHelper.attachToActivity(this, "");
    presenter.attachView(this);
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    ButterKnife.findById(this, R.id.ic_menu).setVisibility(View.GONE);
    ButterKnife.findById(this, R.id.ic_search).setVisibility(View.GONE);
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    presenter.loadTags();
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @Override public void setTags(ArrayList<String> tags) {
    runOnUiThread(() -> {
      recyclerView.setAdapter(new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext())
              .inflate(android.R.layout.simple_list_item_2, parent, false);
          SimpleViewHolder viewHolder = new SimpleViewHolder(view);
          return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
          SimpleViewHolder h = (SimpleViewHolder) holder;
          h.textView.setText(tags.get(position));
          h.itemView.setTag(tags.get(position));
          h.itemView.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(TagActivity.this).setMessage(
                "Apakah Anda yakin ingin menghapus tagar?").create();
            alertDialog.show();
          });
          itemEventHelper.attach(h.itemView, h.textView, 0, position);
        }

        @Override public int getItemCount() {
          return tags.size();
        }
      });
      itemEventHelper.setAdapterOnClickItem(new ItemEventHelper.OnClickItem<String>() {
        @Override public void onClick(View v, String item, int section, int position) {

        }

        @Override public void onLongPressed(View v, String item, int section, int position) {
          itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
            @Override public void onCancel() {

            }

            @Override public void onDelete() {
              presenter.deleteTags(item.toString());
            }

            @Override public void onEdit() {
              startActivityForResult(
                  new Intent(TagActivity.this, AddTagActivity.class).putExtra("mode",
                      AddTagActivity.MODE_EDIT).putExtra("tag", item.toString()), 1);
            }
          });
        }
      });
      Helper.checkIfBlank(bv, tags.isEmpty());
    });
  }

  @Override public void finishDelete() {
    presenter.loadTags();
  }

  @Override public void finishUpdate() {

  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    presenter.loadTags();
  }

  @Override public void onError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  public class SimpleViewHolder extends RecyclerView.ViewHolder {
    @Bind(android.R.id.text1) TextView textView;

    public SimpleViewHolder(View v) {
      super(v);
      ButterKnife.bind(this, v);
      v.setBackgroundResource(R.drawable.item_transition_background);
    }
  }
}
