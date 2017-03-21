package javasign.com.dompetsehat.ui.activities.tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.tag.TagInterface;
import javasign.com.dompetsehat.presenter.tag.TagPresenter;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/12/16.
 */
public class AddTagActivity extends BaseActivity implements TagInterface{

  @Bind(R.id.ic_menu) IconicsTextView ic_menu;
  @Bind(R.id.et_tag_name) MaterialEditText et_tag_name;
  @Inject TagPresenter presenter;

  public static final int MODE_EDIT = 1;
  public static final int MODE_ADD = 2;
  private int MODE = MODE_ADD;
  private String tag_before;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_tag);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    presenter.attachView(this);

    setTitle(getString(R.string.add_tag_title));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  private void init() {
    Words.setButtonToListen(ic_menu,et_tag_name);
    if(getIntent().getExtras() != null){
      if(getIntent().hasExtra("mode")){
        MODE = getIntent().getExtras().getInt("mode");
      }
      if(getIntent().hasExtra("tag")){
        tag_before = getIntent().getExtras().getString("tag");
        et_tag_name.setText(tag_before);
      }
    }
  }

  @OnClick(R.id.ic_back) void onBack(View v){
    finish();
  }

  @OnClick(R.id.ic_menu) void doSave(View v){
    if(MODE == MODE_ADD) {
      save();
    }else{
      update();
    }
  }

  private void save() {
    Intent resultIntent = new Intent();
    resultIntent.putExtra("tag_name",et_tag_name.getText().toString());
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }

  private void update() {
    if(!tag_before.equalsIgnoreCase(et_tag_name.getText().toString())) {
      presenter.updateTags(tag_before, et_tag_name.getText().toString());
    }
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override public void setTags(ArrayList<String> tags) {

  }

  @Override public void finishDelete() {

  }

  @Override public void finishUpdate() {
    finish();
  }

  @Override public void onError(String msg) {

  }
}
