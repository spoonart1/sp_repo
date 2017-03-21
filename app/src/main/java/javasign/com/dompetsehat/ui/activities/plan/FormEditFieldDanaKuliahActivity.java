package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.plan.DanaInterface;
import javasign.com.dompetsehat.presenter.plan.DanaPresenter;
import javasign.com.dompetsehat.ui.activities.plan.pojo.DanaKuliahEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by lafran on 9/27/16.
 */

public class FormEditFieldDanaKuliahActivity extends BaseActivity implements DanaInterface {

  protected static final String PREFIX_MAIN = "field";

  public final int usia_maksimal = 15;
  public final int semester_maksimal = 16;

  public static final String FIELD_CONTAINER_ID = "container";
  public static final String FIELD_WITH_ICON_INFO = "information";
  public static final String PREFIX_MESSAGE = PREFIX_MAIN + "Msg";
  public static final String PREFIX_ID = PREFIX_MAIN + "Id";
  public static final String PREFIX_HINT = PREFIX_MAIN + "Hint";
  public static final String PREFIX_HINT_ADDITIONAL = PREFIX_MAIN + "HintAdditional";
  public static final String PREFIX_INPUT_TYPE = PREFIX_MAIN + "input-type";
  public static final String PREFIX_INPUT_TYPE_WITH_CURRENCY = PREFIX_MAIN + "input-type-currency";
  public static final String FIELD_COUNT = "row";

  @Bind(R.id.btn_save) Button btn_save;
  @Bind(R.id.container) LinearLayout container;
  @Bind({ R.id.et_first_field, R.id.et_second_field, R.id.et_third_field }) MaterialEditText[] mets;
  @Bind(R.id.tv_note) TextView tv_note;
  @Bind(R.id.tv_result_value) TextView tv_result_value;
  @Bind(R.id.ll_note) View ll_note;
  @Bind({ R.id.ic_edit1, R.id.ic_edit2, R.id.ic_edit3 }) View[] ic_edits;

  private RxBus rxBus = MyCustomApplication.getRxBus();
  private int fieldRow = 0;
  private Builder[] builders;
  private int mContainer;
  private Intent intent;
  @Inject DanaPresenter presenter;
  RupiahCurrencyFormat format = new RupiahCurrencyFormat();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_form_edit_field_dana_kuliah);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle("");
    init();
    ButterKnife.bind(this);
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    this.intent = getIntent();
    initWithProperties();
  }

  private void initWithProperties() {

    builders = extractBundle(getIntent().getExtras());

    RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

    if (builders != null) {
      btn_save.setVisibility(View.VISIBLE);

      TextView[] fields = new TextView[getFieldRow()];
      for (int i = 0; i < getFieldRow(); i++) {
        mets[i].setText(builders[i].fieldMsg);
        mets[i].setFloatingLabelText(builders[i].fieldHint);
        mets[i].setVisibility(View.VISIBLE);
        mets[i].setInputType(builders[i].inputType);
        mets[i].setHint(builders[i].fieldHintAdditional);

        if (builders[i].isCurrency) format.formatEditText(mets[i]);

        ic_edits[i].setVisibility(builders[i].isShowingInfoBtn ? View.VISIBLE : View.GONE);

        fields[i] = mets[i];
      }

      updateNote();
      setListener(fields);
    }
  }

  private void setListener(TextView[] views) {
    Words.setButtonToListen(btn_save, views);
    for (int i = 0; i < mets.length; i++) {
      mets[i].addTextChangedListener(new CustomTextWatcher(mets[i]));
    }
  }

  @OnClick(R.id.btn_save) void callRxBus() {
    int[] viewIdToFill = new int[builders.length];
    String[] messagesToSend = new String[builders.length];
    for (int i = 0; i < builders.length; i++) {
      viewIdToFill[i] = builders[i].fieldId;
      messagesToSend[i] = mets[i].getText().toString();
    }

    for (MaterialEditText e : mets) {
      if (!TextUtils.isEmpty(e.getError())) {
        return;
      }
    }

    rxBus.send(
        new DanaKuliahEvent(viewIdToFill).withMessages(messagesToSend).withContainerId(mContainer));
    finish();
  }

  private Builder[] extractBundle(Bundle b) {
    if (b == null) return null;

    fieldRow = b.getInt(FIELD_COUNT, 0);
    mContainer = b.getInt(FormEditFieldDanaKuliahActivity.FIELD_CONTAINER_ID, -1);

    boolean isShowingInfoButton = b.getBoolean(FIELD_WITH_ICON_INFO, false);

    Builder[] builders = new Builder[fieldRow];

    String title = b.getString(Words.TITLE, "");
    setTitle(title);

    for (int i = 1; i <= builders.length; i++) {

      int viewid = b.getInt(PREFIX_ID + i);
      int inputType = b.getInt(PREFIX_INPUT_TYPE + i, InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
      String message = b.getString(PREFIX_MESSAGE + i);
      String hint = b.getString(PREFIX_HINT + i);
      String hintAdditional = b.getString(PREFIX_HINT_ADDITIONAL + i, "");

      boolean isCurrency = b.getBoolean(PREFIX_INPUT_TYPE_WITH_CURRENCY + i, false);

      Builder bd = new Builder(viewid, message, hint, inputType).withCurrencyFormat(isCurrency)
          .withAdditionalHint(hintAdditional)
          .withInformationButtonShowing(isShowingInfoButton);
      builders[i - 1] = bd;
    }

    return builders;
  }

  private void updateNote() {
    String note = null;
    ll_note.setVisibility(View.VISIBLE);
    switch (mContainer) {
      case R.id.ll_1:

        try {
          if (Integer.valueOf(mets[1].getText().toString()) > usia_maksimal) {
            mets[1].setError(
                getString(R.string.maksimum_age) + " " + usia_maksimal + " " + getString(
                    R.string.year));
          }
        } catch (Exception e) {

        }

        int usia = TextUtils.isEmpty(mets[1].getText()) ? 0
            : 18 - Integer.valueOf(mets[1].getText().toString());

        note = getString(R.string.note_college_time_left)
            + " "
            + usia
            + " "
            + getString(R.string.year)
            + ".";

        tv_note.setText(note);
        tv_result_value.setVisibility(View.GONE);

        break;
      case R.id.ll_2:
        if (!intent.hasExtra(DanaKuliahActivity.TAHUN_PELUNASAN)) {
          finish();
          return;
        }

        try {
          if (Integer.valueOf(mets[0].getText().toString()) > semester_maksimal) {
            mets[0].setError("Maksimal " + semester_maksimal + " semester");
          }
        } catch (Exception e) {

        }

        int tahun_pelunasan = intent.getExtras().getInt(DanaKuliahActivity.TAHUN_PELUNASAN);
        int lamaKuliah = TextUtils.isEmpty(mets[0].getText()) ? 0
            : Integer.valueOf(mets[0].getText().toString());
        double biaya_kuliah = TextUtils.isEmpty(mets[1].getText()) ? 0.0
            : Double.valueOf(RupiahCurrencyFormat.clearRp(mets[1].getText().toString()));

        double uang_saku = TextUtils.isEmpty(mets[2].getText()) ? 0.0
            : Double.valueOf(RupiahCurrencyFormat.clearRp(mets[2].getText().toString()));

        note = getString(R.string.note_college_time_total) + " " + lamaKuliah + " semester";

        double value = ((lamaKuliah * 6) * uang_saku) + biaya_kuliah;

        tv_result_value.setText(
            format.toRupiahFormatSimple(presenter.hitungInflasi(tahun_pelunasan, value)));

        tv_note.setText(note);
        tv_result_value.setVisibility(View.VISIBLE);
        break;
    }
  }

  @OnClick({ R.id.ic_edit1, R.id.ic_edit2, R.id.ic_edit3 }) void showInfo(View v) {
    String message = "";
    String title = "";
    switch (v.getId()) {
      case R.id.ic_edit1:
        title = Words.toTitleCase(builders[0].fieldHint);
        message = getString(R.string.lorem_ipsum);
        break;

      case R.id.ic_edit2:
        title = Words.toTitleCase(builders[1].fieldHint);
        message = getString(R.string.lorem_ipsum);
        break;

      case R.id.ic_edit3:
        title = Words.toTitleCase(builders[2].fieldHint);
        message = getString(R.string.lorem_ipsum);
        break;
    }

    Helper.showSimpleInformationDialog(getSupportFragmentManager(), title, message);
  }

  @Override public void setUmur(int umur) {

  }

  @Override public void setDanaDisiapkan(double dana) {

  }

  @Override public void setProsentase(double prosentase) {

  }

  @Override public void setCicilanPerbulan(double cicilanPerbulan) {

  }

  @Override public void setPendapatanBulanan(double pendapatanBulanan) {

  }

  @Override public void setDibayarLunas(double dibayarLunas) {

  }

  @Override public void setCicilanPertahun(double cicilanPertahun) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  class Builder {

    int fieldId;
    int inputType;
    String fieldHint;
    String fieldHintAdditional;
    String fieldMsg;
    boolean isCurrency = false;
    boolean isShowingInfoBtn = false;

    Builder(int fieldId, String fieldMsg, String fieldHint, int inputType) {
      this.fieldId = fieldId;
      this.fieldMsg = fieldMsg;
      this.fieldHint = fieldHint;
      this.inputType = inputType;
    }

    Builder withCurrencyFormat(boolean isCurrency) {
      this.isCurrency = isCurrency;
      return this;
    }

    Builder withAdditionalHint(String fieldHintAdditional) {
      this.fieldHintAdditional = fieldHintAdditional;
      return this;
    }

    Builder withInformationButtonShowing(boolean show) {
      this.isShowingInfoBtn = show;
      return this;
    }
  }

  private int getFieldRow() {
    return fieldRow;
  }

  private class CustomTextWatcher implements TextWatcher {
    private EditText mEditText;

    public CustomTextWatcher(EditText e) {
      mEditText = e;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
      updateNote();
    }
  }

  @Override protected void onDestroy() {
    if(presenter!= null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
