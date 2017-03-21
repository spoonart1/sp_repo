package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.presenter.plan.DanaPresenter;
import javasign.com.dompetsehat.ui.activities.plan.pojo.DanaKuliahEvent;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

/**
 * Created by lafran on 9/27/16.
 */

public class DanaKuliahActivity extends BundlePlanActivity {

  @Bind({ R.id.icr_number1, R.id.icr_number2, R.id.icr_number3 }) IconCategoryRounded[] numbers;
  @Bind({ R.id.layout1, R.id.layout2, R.id.layout3 }) View[] contents;
  @Bind(R.id.tv_note3) TextView tv_note3;

  //step1
  @Bind(R.id.tv_kid_name) TextView tv_kid_name;
  @Bind(R.id.tv_age) TextView tv_age;

  //step2
  @Bind(R.id.tv_target)TextView tv_target;
  @Bind(R.id.tv_biaya)TextView tv_biaya;
  @Bind(R.id.tv_uang_saku)TextView tv_uang_saku;
  private final int umur_kuliah = 18;

  public static final String NAMA_ANAK = "nama_anak";
  public static final String UMUR = "umur";
  public static final String BIAYA_KULIAH = "biaya_kuliah";
  public static final String LAMA_KULIAH = "lama_kuliah";
  public static final String UANG_SAKU = "uang_saku";
  public static final String TAHUN_PELUNASAN = "tahun";

  String name = "";
  int age = 0;
  int target = 0;
  double biaya = 0;
  double uang_saku = 0;
  int tahun_pelunasan = 0;

  double total_biaya_saat_ini = 0;
  double total_biaya_besok = 0;

  RupiahCurrencyFormat format = new RupiahCurrencyFormat();


  @Bind({R.id.tv_note3, R.id.tv_kid_name,
      R.id.tv_age, R.id.tv_target, R.id.tv_biaya, R.id.tv_uang_saku}) TextView[] allFields;

  @Bind(R.id.btn_next) View btn_next;

  @Inject DanaPresenter presenter;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private SessionManager session;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dana_kuliah);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    session = new SessionManager(getActivityComponent().context());
    presenter.attachView(this);

    setTitle(getString(R.string.plan_education_title));

    Helper.trackThis(this, "user klik dana kuliah");
    init();
    initSession();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    saveToDraft();
    finish();
  }

  private void init() {


    rxBus.toObserverable().ofType(DanaKuliahEvent.class).subscribe(new SimpleObserver<DanaKuliahEvent>() {
      @Override public void onNext(DanaKuliahEvent danaKuliahEvent) {
        View container = ButterKnife.findById(DanaKuliahActivity.this, danaKuliahEvent.view_containerId);
        for(int i=0;i<danaKuliahEvent.fieldsId.length;i++){
          TextView field = ButterKnife.findById(DanaKuliahActivity.this, danaKuliahEvent.fieldsId[i]);
          field.setText(danaKuliahEvent.messages[i]);
        }

        setNoteMessage(danaKuliahEvent.view_containerId);
        //container.setVisibility(View.VISIBLE);

        container.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            setPageIsFinish(getPageByContainerId(v.getId()));
            if(v.getId() == R.id.ll_2) {
              setPageIsFinish(getPageByContainerId(R.id.ll_3));
            }
          }
        });

        setPageIsChecked(danaKuliahEvent.view_containerId);
        if(danaKuliahEvent.view_containerId == R.id.ll_2){
          setPageIsChecked(R.id.ll_3);
        }

      }
    });

    Words.setButtonToListen(btn_next, allFields);
  }

  private void initSession() {
    HashMap<String, String> map = session.getDanaKuliah();
    tv_kid_name.setText(map.get(SessionManager.NAMA_ANAK));
    tv_age.setText(map.get(SessionManager.USIA));
    tv_target.setText(map.get(SessionManager.SEMESTER));
    tv_biaya.setText(map.get(SessionManager.BIAYA_KULIAH));
    tv_uang_saku.setText(map.get(SessionManager.UANG_SAKU));
  }

  private void setPageIsChecked(int containerId){
    IconCategoryRounded target = numbers[getPageByContainerId(containerId) - 1];
    target.setBackgroundColorIcon(Helper.GREEN_DOMPET_COLOR);
    target.setIconCode(DSFont.Icon.dsf_check.getFormattedName());
    target.withStrokeColor(Color.TRANSPARENT, 0);
    target.setIconTextColor(Color.WHITE);
  }

  @OnClick(R.id.btn_next) void onSave() {
    saveToDraft();

    Intent intent = new Intent();
    intent.putExtra(FinishingPlanActivity.TYPE,FinishingPlanActivity.TYPE_DANA_KULIAH);
    intent.putExtra(NAMA_ANAK, name);
    intent.putExtra(UMUR, age);
    intent.putExtra(LAMA_KULIAH, target);
    intent.putExtra(BIAYA_KULIAH, biaya);
    intent.putExtra(UANG_SAKU, uang_saku);
    intent.putExtra(SetupPlanActivity.DANA_DISIAPKAN, total_biaya_besok);
    intent.putExtra(SetupPlanActivity.BULAN_PERSIAPAN,tahun_pelunasan * 12);

    Helper.goTo(this, SetupPlanActivity.class,intent);
  }

  private void saveToDraft(){
    if(!TextUtils.isEmpty(tv_uang_saku.getText()))
      uang_saku = Double.parseDouble(RupiahCurrencyFormat.clearRp(tv_uang_saku.getText().toString()));
    if(!TextUtils.isEmpty(tv_age.getText()))
      age = Integer.parseInt(tv_age.getText().toString());
    if(!TextUtils.isEmpty(tv_target.getText()))
      target = Integer.parseInt(tv_target.getText().toString());
    if(!TextUtils.isEmpty(tv_biaya.getText()))
      biaya = Double.parseDouble(RupiahCurrencyFormat.clearRp(tv_biaya.getText().toString()));
    if(!TextUtils.isEmpty(tv_uang_saku.getText()))
      uang_saku = Double.parseDouble(RupiahCurrencyFormat.clearRp(tv_uang_saku.getText().toString()));

    session.saveDanaKuliah(name, age, target, biaya, uang_saku);
  }

  private void setNoteMessage(int containerId){

    switch (containerId){
      case R.id.ll_1:
        name = tv_kid_name.getText().toString();
        age = Integer.valueOf(tv_age.getText().toString());
        tahun_pelunasan = umur_kuliah-age;
        Timber.e("name "+name+" age "+age+" tahunpelunasan "+tahun_pelunasan);
        break;

      case R.id.ll_2:
        target = Integer.valueOf(tv_target.getText().toString())*6;
        biaya = Double.valueOf(RupiahCurrencyFormat.clearRp(tv_biaya.getText().toString()));
        uang_saku = Double.valueOf(RupiahCurrencyFormat.clearRp(tv_uang_saku.getText().toString()));
        total_biaya_saat_ini = ((target)*uang_saku)+biaya;
        total_biaya_besok = presenter.hitungInflasi(tahun_pelunasan,total_biaya_saat_ini);
        contents[contents.length-1].setVisibility(View.VISIBLE);
        String note = "Prakiraan total dana kuliah anak Anda hingga lulus di masa depan sebagai berikut";
        TextView tv_result_value_3 = ButterKnife.findById(this, R.id.tv_result_value3);
        tv_result_value_3.setText(format.toRupiahFormatSimple(total_biaya_besok));
        tv_note3.setText(note);
        break;
    }
  }

  @OnClick({ R.id.ic_edit1, R.id.ic_edit2 }) void getForm(View v) {
    Bundle b = new Bundle();
    Intent intent = new Intent(this, FormEditFieldDanaKuliahActivity.class);
    switch (v.getId()){
      case R.id.ic_edit1:

        intent.putExtra(Words.TITLE, getString(R.string.field_form_edit_title));
        intent.putExtra(FormEditFieldDanaKuliahActivity.FIELD_CONTAINER_ID, R.id.ll_1);

        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_MESSAGE +"1", tv_kid_name.getText().toString()); //message
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_ID+"1", tv_kid_name.getId()); //viewid
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT+"1", getString(R.string.field_form_edit_1)); //hint

        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_MESSAGE +"2", tv_age.getText().toString());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_ID+"2", tv_age.getId());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT+"2", getString(R.string.field_form_edit_2));
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT_ADDITIONAL+"2", "0 "+getString(R.string.year)); //hint edittextfield
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE+"2", InputType.TYPE_CLASS_NUMBER);

        intent.putExtra(FormEditFieldDanaKuliahActivity.FIELD_COUNT, 2);

        break;

      case R.id.ic_edit2:

        intent.putExtra(Words.TITLE, "Biaya pendidikan");
        intent.putExtra(FormEditFieldDanaKuliahActivity.FIELD_CONTAINER_ID, R.id.ll_2);
        intent.putExtra(FormEditFieldDanaKuliahActivity.FIELD_WITH_ICON_INFO, false);

        // additional data
        intent.putExtra(TAHUN_PELUNASAN,tahun_pelunasan);

        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_MESSAGE +"1", tv_target.getText().toString()); //message
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_ID+"1", tv_target.getId()); //viewid
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT+"1", getString(R.string.field_form_edit_college_1)); //hint floating
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT_ADDITIONAL+"1", "0 Semester"); //hint edittextfield
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE+"1", InputType.TYPE_CLASS_NUMBER);

        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_MESSAGE +"2", tv_biaya.getText().toString());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_ID+"2", tv_biaya.getId());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT+"2", getString(R.string.field_form_edit_college_2));
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT_ADDITIONAL+"2", "Rp 0"); //hint edittextfield
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE+"2", InputType.TYPE_CLASS_NUMBER);
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE_WITH_CURRENCY+"2", true);

        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_MESSAGE +"3", tv_uang_saku.getText().toString());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_ID+"3", tv_uang_saku.getId());
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT+"3", getString(R.string.field_form_edit_college_3));
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_HINT_ADDITIONAL+"3", "Rp 0"); //hint edittextfield
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE+"3", InputType.TYPE_CLASS_NUMBER);
        intent.putExtra(FormEditFieldDanaKuliahActivity.PREFIX_INPUT_TYPE_WITH_CURRENCY+"3", true);

        intent.putExtra(FormEditFieldDanaKuliahActivity.FIELD_COUNT, 3);
        break;
    }

    intent.putExtras(b);
    startActivity(intent);
  }

  private void setPageIsFinish(int page) {
    int index = limiter(page - 1, 0, numbers.length - 1);

    if(contents[index].getVisibility() == View.VISIBLE) {
      contents[index].setVisibility(View.GONE);
      return;
    }

    contents[index].setVisibility(View.VISIBLE);
  }

  protected int limiter(int value, int minValue, int maxValue) {
    if (value <= minValue) {
      return minValue;
    } else if (value >= maxValue) {
      return maxValue;
    } else {
      return value;
    }
  }

  private int getPageByContainerId(int id){
    switch (id){
      case R.id.ll_1:
        return 1;
      case R.id.ll_2:
        return 2;
      case R.id.ll_3:
        return 3;
      default:
        return 1;
    }
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
