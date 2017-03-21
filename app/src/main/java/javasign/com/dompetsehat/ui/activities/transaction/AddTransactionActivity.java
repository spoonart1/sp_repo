package javasign.com.dompetsehat.ui.activities.transaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.category.AddCategoryInterface;
import javasign.com.dompetsehat.presenter.category.AddCategoryPresenter;
import javasign.com.dompetsehat.presenter.transaction.TransactionsInterface;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.ui.activities.RuntimePermissionsActivity;
import javasign.com.dompetsehat.ui.activities.account.AddAccountDompetActivity;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.tag.adapter.TagListAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.Categories;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/11/16.
 */
public class AddTransactionActivity extends RuntimePermissionsActivity
    implements AddCategoryInterface, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, TransactionsInterface {

  public final static String TYPE_ADD = "add";
  public final static String TYPE_EDIT = "edit";
  public final static String FROM = "from";

  public final static String KEY_TYPE = "type";
  public final static String KEY_TRANSACTION_TYPE = "cash_type";
  public final static String KEY_CASHFLOW_ID = "cash_id";

  @Bind(R.id.spinner) Spinner spinner;
  @Bind(R.id.icr_category) IconCategoryRounded icr_category;
  @Bind(R.id.et_cat_name) TextView et_cat_name;
  @Bind(R.id.tv_time) TextView tv_time;
  @Bind(R.id.et_place) EditText et_place;
  @Bind(R.id.et_amount) EditText et_amount;
  @Bind(R.id.et_note) MaterialEditText et_note;
  @Bind(R.id.ic_location) IconicsTextView ic_location;
  @Bind(R.id.list_item) RecyclerView recyclerView;
  @Bind(R.id.ic_menu) IconicsTextView ic_menu;
  @Bind(R.id.fab_delete) FloatingActionButton fab_delete;

  final LinearLayoutManager llm = new LinearLayoutManager(this);

  private TagListAdapter tagListAdapter;
  private HashMap<String, String> cashflow_tag = new HashMap<>();
  private Calendar c = Calendar.getInstance();
  private String mDate;
  private GoogleApiClient mGoogleApiClient;
  private Category category;
  private UserCategory user_category;
  private Cash cashflow;
  private MCryptNew mCryptNew = new MCryptNew();
  private Date initialDate;

  private DbHelper db;
  private SessionManager session;
  public static int REGUEST_CODE_CATEGORY = 2;
  public static int REGUEST_CODE_TAG = 3;
  public static int REGUEST_CODE_ADD_DOMPET = 4;
  private ArrayList<Product> dompets;
  private int fragmentPos = -1;

  @Inject AddCategoryPresenter categoryPresenter;
  @Inject TransactionsPresenter transactionsPresenter;

  RupiahCurrencyFormat format = new RupiahCurrencyFormat();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_transaction);
    ButterKnife.bind(this);
    db = DbHelper.getInstance(this);
    session = new SessionManager(this);
    getActivityComponent().inject(this);
    categoryPresenter.attachView(this);
    transactionsPresenter.attachView(this);

    Intent intent = getIntent();
    if (intent.getExtras().getString(KEY_TYPE).equals(TYPE_EDIT)) {
      if (intent.hasExtra(KEY_TYPE) && intent.hasExtra(KEY_CASHFLOW_ID)) {
        int cashflow_id = intent.getExtras().getInt(KEY_CASHFLOW_ID);
        populateEdit(cashflow_id);
      }
    } else {
      dompets = db.getAllDompet(session.getIdUser());
      if (dompets.size() == 0) {
        createDialog();
      }
      Timber.e("dompets " + dompets.size());
    }

    fab_delete.setVisibility(View.GONE);
    fab_delete.hide();
    fab_delete.setBackgroundTintList(
        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_400)));
    init();
  }

  public void createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(
        new ContextThemeWrapper(this, R.style.Theme_AppCompat_NoActionBar_FullScreen));
    builder.setTitle(getString(R.string.no_manual_account));
    builder.setMessage(getString(R.string.you_dont_have_manual_account));
    builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
      startActivityForResult(
          new Intent(AddTransactionActivity.this, AddAccountDompetActivity.class).putExtra(
              "add-mode", false).putExtra(Words.ID_VENDOR, 6), REGUEST_CODE_ADD_DOMPET);
    });
    builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
      dialogInterface.dismiss();
      finish();
    });
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  @Override public void onPermissionsGranted(int requestCode) {
    try {
      PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
      Intent intent = intentBuilder.build(AddTransactionActivity.this);
      // Start the intent by requesting a result,
      // identified by a request code.
      startActivityForResult(intent, 1);
    } catch (GooglePlayServicesRepairableException e) {
      Timber.e("onPermissionsGranted " + e);
    } catch (GooglePlayServicesNotAvailableException e) {
      Timber.e("onPermissionsGranted " + e);
    }
  }

  @OnClick(R.id.fab_delete) void onDelete(View v) {
    if (cashflow != null) {
      fab_delete.hide();
      Helper.hideKeyboard(this.getCurrentFocus(), v);
      final DeleteUtils deleteUtils = beginPendingRemoval(cashflow);
      Helper.deleteWithConfirmation(v, this, v1 -> deleteUtils.cancelRemoval(), null);
    }
  }

  private DeleteUtils beginPendingRemoval(Cash item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, transactionsPresenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        fab_delete.show();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new DeleteTransactionEvent(getApplicationContext()));
        setResult(RESULT_OK);
        finish();
      }
    });
    return deleteUtils;
  }

  @Override protected void onResume() {
    super.onResume();
  }

  private void init() {
    llm.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(llm);

    mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API)
        .addApi(Places.PLACE_DETECTION_API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
    loadData();
  }

  public void populateEdit(int cashflow_id_local) {
    categoryPresenter.loadCashflow(cashflow_id_local);
  }

  private void loadData() {
    if (getIntent().getExtras().getString(KEY_TYPE).equals(TYPE_EDIT)) {
      int cashflow_id = getIntent().getExtras().getInt(KEY_CASHFLOW_ID);
      String vendorName = db.getVendorName(cashflow_id, 4);
      spinner.setAdapter(new ArrayAdapter(this, R.layout.spinner_product_item,
          new String[] { vendorName.toUpperCase() }));
    } else {
      String[] products = new String[dompets.size()];
      int index = 0;
      for (final Product dompet : dompets) {
        products[index] = mCryptNew.decrypt(dompet.getName()).toUpperCase();
        index += 1;
      }
      ArrayAdapter userAdapter = new ArrayAdapter(this, R.layout.spinner_product_item, products);
      spinner.setAdapter(userAdapter);
    }

    setInitialDate(c.getTime());
    mDate = Helper.setSimpleDateFormat(c.getTime(), GeneralHelper.FORMAT_MONTH_MM);
    tv_time.setText(
        Words.days[c.get(Calendar.DAY_OF_WEEK) - 1] + ",\n" + Helper.setSimpleDateFormat(
            initialDate, GeneralHelper.FORMAT_DD_MM_YYYY_SLICED));

    Words.setButtonToListen(ic_menu, et_amount);
    RupiahCurrencyFormat.getInstance().formatEditText(et_amount);
    loadTag();
    if (getIntent().hasExtra(KEY_TRANSACTION_TYPE)) {
      String cashtype = getIntent().getStringExtra(KEY_TRANSACTION_TYPE);
      fragmentPos = getIntent().getIntExtra(Words.ID, -1);
      if (cashtype.equalsIgnoreCase("CR")) setCategory(db.getCategoryByID(25)); //modal
    }

    if (getIntent().hasExtra(Words.MONTH)) {
      int month = getIntent().getIntExtra(Words.MONTH, 1) - 1;
      int year = getIntent().getIntExtra(Words.YEAR, 2040);
      Calendar cl = Calendar.getInstance();
      int currentMonth = cl.get(Calendar.MONTH);
      int currentYear = cl.get(Calendar.YEAR);

      if (currentMonth == month && currentYear == year) return;

      cl.set(year, month, 1);
      setInitialDate(cl.getTime());

      tv_time.setText(
          Words.days[cl.get(Calendar.DAY_OF_WEEK) - 1] + ",\n" + Helper.setSimpleDateFormat(
              initialDate, GeneralHelper.FORMAT_DD_MM_YYYY_SLICED));
    }
  }

  private SlideDateTimeListener listener = new SlideDateTimeListener() {
    @Override public void onDateTimeSet(Date date) {
      tv_time.setText(
          Helper.setSimpleDateFormat(date, "EEEE, \n" + GeneralHelper.FORMAT_DD_MM_YYYY_SLICED));
      mDate = Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_MONTH_MM);
      setInitialDate(date);
    }
  };

  @OnClick(R.id.tv_time) void openCalendar(View v) {
    //TODO : last update lafran
    SlideDateTimePicker.Builder builder =
        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(listener)
            .setInitialDate(initialDate)
            .setIsDateOnly(true)
            .setIs24HourTime(true)
            .setIndicatorColor(Color.WHITE);

    SlideDateTimePicker slideDateTimePicker = builder.build();
    slideDateTimePicker.show();
  }

  @OnClick(R.id.ll_category) void pickCategory(View v) {
    String type = "DB";
    if (category != null) {
      if (category.getBelong_to().equalsIgnoreCase("income")) {
        type = "CR";
      } else if (category.getBelong_to().equalsIgnoreCase("expense")) {
        type = "DB";
      } else {
        type = "DB";
        if (cashflow != null) {
          type = cashflow.getType();
        }
      }
      if (category.getId_category() != 0) {
        if (cashflow != null) {
          if (cashflow.getProduct().getAccount() != null) {
            if (cashflow.getProduct().getAccount().getIdvendor() != 6) {
              startActivity(
                  new Intent(this, CategoryActivity.class).putExtra(CategoryActivity.KEY_CATEGORY,
                      type));
              return;
            } else {
              startActivity(new Intent(this, CategoryActivity.class).putExtra(
                  CategoryActivity.ACTIVE_CATEGORY, type));
              return;
            }
          }
        }
      }
    }
    if (cashflow == null) {
      startActivityForResult(
          new Intent(this, CategoryActivity.class).putExtra(CategoryActivity.ACTIVE_CATEGORY, type),
          REGUEST_CODE_CATEGORY);
    } else {
      type = cashflow.getType();
      startActivityForResult(
          new Intent(this, CategoryActivity.class).putExtra(CategoryActivity.KEY_CATEGORY, type),
          REGUEST_CODE_CATEGORY);
    }
  }

  @OnClick(R.id.ic_menu) void saveThis(View v) {
    saveData();
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  private void saveData() {
    Intent intent = getIntent();
    String type = null;
    if (category != null) {
      Timber.e("belong " + category.getBelong_to());
      if (category.getBelong_to().equalsIgnoreCase("expense")) {
        type = "DB";
      } else if (category.getBelong_to().equalsIgnoreCase("income")) {
        type = "CR";
      } else {
        type = "DB";
        if (intent.getExtras().getString(KEY_TYPE).equals(TYPE_EDIT)) {
          type = cashflow.getType();
        }
      }
    }

    String amount = et_amount.getText().toString();

    amount = RupiahCurrencyFormat.clearRp(amount);
    if (et_amount.getText().toString().equalsIgnoreCase("")) {
      et_amount.setError(getString(R.string.error_amount_if_blank));
    } else if (Double.valueOf(amount) <= 0) {
      et_amount.setError(getString(R.string.error_amount_if_less_zero));
    } else if (spinner.getCount() == 0) {
      Toast.makeText(this, getString(R.string.you_dont_have_account), Toast.LENGTH_LONG).show();
    } else if (this.category == null || type == null) {
      Toast.makeText(this, getString(R.string.you_must_select_category), Toast.LENGTH_LONG).show();
    }
    /*else if (product.getBalance() < Double.valueOf(debetAmount) && type.equalsIgnoreCase("DB")) {
      Toast.makeText(this, "Insufficient funds :(", Toast.LENGTH_SHORT).show();
    }*/
    else {
      Integer product_id = null;
      if (dompets != null) {
        Product product = dompets.get(spinner.getSelectedItemPosition());
        if (product != null) {
          product_id = product.getId_product();
        }
      }
      if (intent.hasExtra(KEY_TYPE) && intent.hasExtra(KEY_CASHFLOW_ID)) {
        if (intent.getExtras().getString(KEY_TYPE).equals(TYPE_EDIT)) {
          if (product_id == null) {
            product_id = this.cashflow.getProduct_id();
          }
          update_cashflow(type, amount, product_id);
          return;
        }
      }
      save_cashflow(type, amount, product_id);
      Helper.trackThis(this, "User telah berhasil menambahkan transaksi baru");
    }
    transactionsPresenter.checkNotif();
  }

  @OnClick(R.id.ll_location) void pickLocation(View v) {
    Helper.trackThis(this, "User memilih merchant/toko/lokasi dari google place");
    requestAppPermissions(new String[] {
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    }, R.string.runtime_permissions_txt, 1);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

      // The user has selected a place. Extract the name and address.
      final Place place = PlacePicker.getPlace(this, data);
      final CharSequence name = place.getName();
      final CharSequence address = place.getAddress();
      final CharSequence attributions = place.getAttributions();
      //if (attributions == null) {
      //  attributions = "";
      //}
      et_place.setText(name);
      try {
        String categoryById = Categories.getCategoryById(place.getPlaceTypes().get(0));
        icr_category.setIconCode(categoryById);
        category = db.getCategoryByIcon(categoryById);
        icr_category.setIconCode(category.getIcon());
        icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
        et_cat_name.setText(category.getName());
      } catch (Exception e) {
        Timber.e("ERROR " + e);
        icr_category.setIconCode(Categories.getCategoryById(4));
        category = db.getCategoryByID(4);
      }

      //placePhotosAsync(place.getId_user_category());
      //et_cat_name.setText(category.getName());
    } else if (requestCode == REGUEST_CODE_TAG && resultCode == Activity.RESULT_OK) {
      String tag_name = data.getExtras().getString("tag_name");
      TagListAdapter.TagView tv = new TagListAdapter.TagView(tag_name, 99);
      tagListAdapter.AddTag(tv);
      llm.scrollToPosition(0);
    } else if (requestCode == REGUEST_CODE_ADD_DOMPET && resultCode == Activity.RESULT_OK) {
      dompets = db.getAllDompet(session.getIdUser());
      loadData();
    }
  }

  private void loadTag() {
    ArrayList<TagListAdapter.TagView> tagViews = new ArrayList<>();
    setDefaultTag(tagViews);
    //tagViews.get(0).statePressed = true;

    tagListAdapter = new TagListAdapter(this, tagViews, true);
    recyclerView.setAdapter(tagListAdapter);
    tagListAdapter.setOnTagSelected((v, idTag, isSelected) -> {
      IconicsTextView btn = (IconicsTextView) v;
      String tag = btn.getText().toString().toLowerCase();
      if (isSelected) {
        if (!cashflow_tag.containsKey(tag)) {
          cashflow_tag.put(tag.toString().toLowerCase(), tag);
        }
      } else {
        cashflow_tag.remove(tag.toString().toLowerCase());
      }
    });
  }

  private void loadTagFromData(List<String> tags, List<String> selectedTag) {
    Timber.e("Here ");
    ArrayList<TagListAdapter.TagView> tagViews = new ArrayList<TagListAdapter.TagView>();
    for (int i = 0; i < tags.size(); i++) {
      TagListAdapter.TagView tv = new TagListAdapter.TagView(tags.get(i), tagViews.size());
      tv.typeView = TagListAdapter.TagView.VIEW_AS_TAG;
      if (selectedTag.contains(tags.get(i))) {
        tv.statePressed = true;
      }
      tagViews.add(tv);
    }

    tagListAdapter = new TagListAdapter(this, tagViews, true);
    recyclerView.setAdapter(tagListAdapter);
    tagListAdapter.notifyDataSetChanged();
    tagListAdapter.setOnTagSelected((v, idTag, isSelected) -> {
      TextView btn = (TextView) v;
      String tag = btn.getText().toString().toLowerCase();
      if (isSelected) {
        if (!cashflow_tag.containsKey(tag)) {
          cashflow_tag.put(tag.toString().toLowerCase(), tag);
        }
      } else {
        cashflow_tag.remove(tag.toString().toLowerCase());
      }
    });
  }

  private void setDefaultTag(ArrayList<TagListAdapter.TagView> tagViews) {
    String[] tags_default = getResources().getStringArray(R.array.default_tags);
    String[] tags_db = db.getAllTag(session.getIdUser());
    String[] tags = tags_default;
    if (tags_db != null) {
      if (tags_db.length > 0) {
        tags = Helper.merge(tags_default, tags_db);
      }
    }
    for (int i = 0; i < tags.length; i++) {
      TagListAdapter.TagView tv = new TagListAdapter.TagView(tags[i], tagViews.size());
      tv.typeView = TagListAdapter.TagView.VIEW_AS_TAG;
      tagViews.add(tv);
    }
  }

  @Override public void onStart() {
    super.onStart();
    if (mGoogleApiClient != null) mGoogleApiClient.connect();
  }

  private void save_cashflow(String type_mutasi, String amount, int id_product) {
    Cash cash = new Cash();
    cash.setCash_id(-1);
    cash.setProduct_id(id_product);

    String merchant = TextUtils.isEmpty(et_place.getText()) ? "" : et_place.getText().toString();
    cash.setDescription(et_note.getText().toString());
    cash.setCashflow_rename(et_note.getText().toString());

    cash.setNote(merchant);
    cash.setCash_tag(CashflowTagToString(cashflow_tag));
    cash.setCategory_id(category.getId_category());
    if (user_category != null) {
      cash.setUser_category_id(user_category.getId_user_category());
    }
    cash.setAmount(Float.valueOf(amount));
    cash.setType(type_mutasi);
    cash.setStatus("unread");
    cash.setCash_date(mDate);
    cash.setCreated_at(Helper.setSimpleDateFormat(c.getTime(), GeneralHelper.FORMAT_LENGKAP));
    cash.setUpdated_at(Helper.setSimpleDateFormat(c.getTime(), GeneralHelper.FORMAT_LENGKAP));
    cash.setTanggal(Integer.valueOf(
        Helper.setSimpleDateFormat(Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_DD)));
    cash.setBulan(Integer.valueOf(
        Helper.setSimpleDateFormat(Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_MM)));
    cash.setTahun(Integer.valueOf(
        Helper.setSimpleDateFormat(Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_YYYY)));
    cash.setHari(
        Helper.setSimpleDateFormat(Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_EEEE));

    db.newTransaction(cash);
    db.updateSaldo(id_product, Float.valueOf(amount), type_mutasi);
    db.badgesCheck(session.getIdUser());
    MyCustomApplication.getRxBus()
        .send(new AddTransactionEvent(
                getApplicationContext(), cash, true)
            .setIdentifier(fragmentPos)
        );
    if (getIntent().hasExtra(FROM)) {
      Toast.makeText(AddTransactionActivity.this,
          "heloo " + getIntent().getExtras().getString(FROM), Toast.LENGTH_LONG).show();
      if (getIntent().getExtras().getString(FROM).equalsIgnoreCase("service")) {
        startActivity(new Intent(AddTransactionActivity.this, NewMainActivity.class));
      }
    }
    finish();
  }

  private void update_cashflow(String type_mutasi, String amount, Integer id_product) {
    if (this.cashflow != null) {
      ProgressDialog dialog = new ProgressDialog(this);
      dialog.setMessage("Sedang mengupdate...");
      dialog.show();
      runOnUiThread(() -> {
        float amount_before = this.cashflow.getAmount();
        Cash cash = this.cashflow;
        if (id_product != null) {
          cash.setProduct_id(id_product);
        }
        String merchant =
            TextUtils.isEmpty(et_place.getText()) ? "" : et_place.getText().toString();
        //cash.setDescription(et_note.getText().toString());
        cash.setCashflow_rename(et_note.getText().toString());
        if (et_note.getText().length() == 0) {
          cash.setCashflow_rename("-");
        }

        if (category == null) {
          et_cat_name.setError(getString(R.string.error_category_if_blank));
          return;
        } else if (category.getId_category() == 0) {
          et_cat_name.setError(getString(R.string.error_category_if_blank));
          return;
        }

        cash.setNote(merchant);
        cash.setCash_tag(CashflowTagToString(cashflow_tag));
        cash.setCategory_id(category.getId_category());
        if (user_category != null) {
          cash.setUser_category_id(user_category.getId_user_category());
        }
        cash.setAmount(Float.valueOf(amount));
        cash.setType(type_mutasi);
        cash.setStatus("read");
        cash.setCash_date(mDate);
        cash.setCreated_at(Helper.setSimpleDateFormat(c.getTime(), GeneralHelper.FORMAT_LENGKAP));
        cash.setUpdated_at(Helper.setSimpleDateFormat(c.getTime(), GeneralHelper.FORMAT_LENGKAP));
        cash.setTanggal(Integer.valueOf(Helper.setSimpleDateFormat(
            Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_DD)));
        cash.setBulan(Integer.valueOf(Helper.setSimpleDateFormat(
            Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_MM)));
        cash.setTahun(Integer.valueOf(Helper.setSimpleDateFormat(
            Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_YYYY)));
        cash.setHari(Helper.setSimpleDateFormat(
            Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, mDate),
            GeneralHelper.FORMAT_EEEE));

        db.UpdateCash(cash);
        db.updateSaldoAfterEditTransaction(id_product, Float.valueOf(amount_before), type_mutasi);
        db.updateSaldo(id_product, Float.valueOf(amount), type_mutasi);
        db.badgesCheck(session.getIdUser());
        MyCustomApplication.getRxBus()
            .send(new AddTransactionEvent(getApplicationContext(), cash, false));
        finish();
        dialog.dismiss();
      });
    }
  }

  public String CashflowTagToString(HashMap<String, String> tags) {
    String casflow_tag = "";
    boolean first = true;
    for (String key : tags.keySet()) {
      if (first) {
        first = false;
        casflow_tag = tags.get(key);
      } else {
        casflow_tag = casflow_tag + "," + tags.get(key);
      }
    }
    return casflow_tag;
  }

  @Override public void onConnected(Bundle bundle) {

  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  @Override public void setCategory(Category category) {
    setIconCategory(category.getName(), category);
  }

  @Override public void setUserCategory(UserCategory userCategory) {
    this.user_category = userCategory;
    setIconCategory(userCategory.getName(), userCategory.getParentCategory());
  }

  @Override public void setIconCategory(String textlabel, Category category) {
    runOnUiThread(() -> {
      this.category = category;
      if (category.getId_category() != 0) {
        if (category.getBelong_to().equalsIgnoreCase("income")) {
          et_amount.setTextColor(Helper.getAccentColor(this, 2));
        } else {
          et_amount.setTextColor(Helper.getAccentColor(this, 1));
        }
        icr_category.setIconCode(category.getIcon());
        icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
        et_cat_name.setText(textlabel);
      } else {
        emptyCtegory();
      }
    });
  }

  public void emptyCtegory() {
    icr_category.setIconCode(DSFont.Icon.dsf_general.getFormattedName());
    icr_category.setBackgroundColorIcon(Color.GRAY);
    et_cat_name.setText(getString(R.string.select_category));
  }

  @Override public void setCashflow(Cash cashflow) {
    if (cashflow != null) {
      Handler handler = new Handler(getMainLooper());
      Runnable runnable = new Runnable() {
        @Override public void run() {
          AddTransactionActivity.this.cashflow = cashflow;
          if (cashflow.getProduct().getAccount().getIdvendor() != 0) {
            fab_delete.setVisibility(View.VISIBLE);
            fab_delete.show();
          }
          if (cashflow.getProduct().getAccount() != null) {
            if (cashflow.getProduct().getAccount().getIdvendor() != 6) {
              et_amount.setEnabled(false);
            }
          }
          if (cashflow.getType().equalsIgnoreCase("CR")) {
            et_amount.setTextColor(Helper.getAccentColor(AddTransactionActivity.this, 1));
          } else {
            et_amount.setTextColor(Helper.getAccentColor(AddTransactionActivity.this, 2));
          }
          String rename = cashflow.getDescription();
          if (!TextUtils.isEmpty(cashflow.getCashflow_rename())) {
            rename = cashflow.getCashflow_rename();
          }
          if (rename.equalsIgnoreCase("-")) {
            et_note.setText("");
          } else {
            et_note.setText(rename);
          }
          et_amount.setText(format.toRupiahFormatSimple(cashflow.getAmount()));
          et_place.setText(cashflow.getNote());
          setCategory(cashflow.getCategory());
          if (cashflow.getUserCategory() != null) {
            setUserCategory(cashflow.getUserCategory());
          }
          if (dompets != null) {
            int index = dompets.indexOf(cashflow.getProduct());
            Timber.e("avesina index");
            spinner.setSelection(index);
          } else {
            if (cashflow.getProduct().getAccount().getIdvendor() == 6) {
              spinner.setEnabled(true);
              dompets = db.getAllDompet(session.getIdUser());
              String[] products = new String[dompets.size()];
              int index = 0;
              int index_selected = 0;
              for (final Product dompet : dompets) {
                products[index] = mCryptNew.decrypt(dompet.getName());
                if (dompet.getId_product() == cashflow.getProduct().getId_product()) {
                  index_selected = index;
                }
                index += 1;
              }
              ArrayAdapter userAdapter =
                  new ArrayAdapter(AddTransactionActivity.this, R.layout.spinner_product_item,
                      products);
              spinner.setAdapter(userAdapter);
              spinner.setSelection(index_selected);
            }
          }
          spinner.setEnabled(false);
          cashflow_tag.clear();
          String tags = cashflow.getCash_tag();
          try {
            Date date =
                Helper.setInputFormatter(GeneralHelper.FORMAT_MONTH_MM, cashflow.getCash_date());
            if (date != null) {
              initialDate = date;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            mDate = Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_MONTH_MM);
            tv_time.setText(
                Words.days[c.get(Calendar.DAY_OF_WEEK) - 1] + ",\n" + Helper.setSimpleDateFormat(
                    c.getTime(), GeneralHelper.FORMAT_DD_MM_YYYY_SLICED));
          } catch (Exception e) {
            Timber.e("ERROR " + e);
          }
          if (!tags.equalsIgnoreCase("")) {
            List<String> strings_tag = Arrays.asList(tags.split(","));
            List<String> strings_tag_defaut =
                Arrays.asList(getResources().getStringArray(R.array.default_tags));
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.addAll(strings_tag_defaut);
            for (String tag : strings_tag) {
              if (!stringArrayList.contains(tag)) {
                stringArrayList.add(0, tag);
              }
              cashflow_tag.put(tag, tag);
            }
            loadTagFromData(stringArrayList, strings_tag);
          } else {
            tagListAdapter.getTagViews().get(0).statePressed = false;
          }
        }
      };
      handler.post(runnable);
    }
  }

  @Override protected void onDestroy() {
    if (categoryPresenter != null) {
      categoryPresenter.detachView();
    }
    if (transactionsPresenter != null) {
      transactionsPresenter.detachView();
    }
    super.onDestroy();
  }

  public void setInitialDate(Date initialDate) {
    this.initialDate = initialDate;
    mDate = Helper.setSimpleDateFormat(initialDate, GeneralHelper.FORMAT_MONTH_MM);
  }

  @Override public void setAdapterTransaction(List<FragmentModel> fragmentModels, int position,
      boolean isCardView) {

  }

  @Override public void setTimeline(TimelineView timelineView, boolean isBroadcasted) {

  }

  @Override public void initTags(List<String> tags) {

  }

  @Override public void setTransaction(ArrayList<Transaction> transactions) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }
}
