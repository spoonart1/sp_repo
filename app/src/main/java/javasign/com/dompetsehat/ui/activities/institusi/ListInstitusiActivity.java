package javasign.com.dompetsehat.ui.activities.institusi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.institusi.adapter.AdapterListInstitusi;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class ListInstitusiActivity extends BaseActivity {

  @Bind(R.id.recycleview) RecyclerView recyclerView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_institusi);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(getString(R.string.list_of_institution));

    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    setData();
  }

  private void setData() {
    List<AdapterListInstitusi.InstitusiModel> institusiList = new ArrayList<>();
    institusiList.add(generateItem("MAMI SAHAM", AdapterListInstitusi.Status.belum_terhubung));
    institusiList.add(generateItem("MAMI SAHAM UTAMA", AdapterListInstitusi.Status.terhubung));
    institusiList.add(generateItem("MAMI SAHAM ANDALAN", AdapterListInstitusi.Status.terhubung));

    AdapterListInstitusi adapter = new AdapterListInstitusi(institusiList);

    adapter.setOnListClick((v, model) -> {

    });

    recyclerView.setAdapter(adapter);
  }

  private AdapterListInstitusi.InstitusiModel generateItem(String label, AdapterListInstitusi.Status status) {
    AdapterListInstitusi.InstitusiModel ins = new AdapterListInstitusi.InstitusiModel();
    ins.name = label;
    ins.status = status;
    return ins;
  }
}
