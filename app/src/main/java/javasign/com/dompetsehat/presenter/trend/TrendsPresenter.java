package javasign.com.dompetsehat.presenter.trend;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.trend.listfragment.ListTrendFragmentLineChart;
import javasign.com.dompetsehat.ui.activities.trend.listfragment.ListTrendFragmentPieChart;
import javasign.com.dompetsehat.ui.activities.trend.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelCategory;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelOverall;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Words;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/29/16.
 */

public class TrendsPresenter extends BasePresenter<TrendInterface> {
    private Context context;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    MCryptNew mcrypt = new MCryptNew();
    private final DataManager dataManager;
    private final SessionManager session;
    private GeneralHelper helper = GeneralHelper.getInstance();
    private ArrayList<Double> income;
    private ArrayList<Double> expense;
    private DbHelper db;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    final static String[] titles =  { "Minggu ini", "Bulan ini", "Bulan lalu", "3 Bulan lalu" };

    @Inject
    public TrendsPresenter(@ActivityContext Context context, DataManager dataManager, SessionManager session, DbHelper db) {
        this.context = context;
        this.dataManager = dataManager;
        this.session = session;
        this.db = db;
    }

    @Override public void attachView(TrendInterface mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    public void loadData(int MENU_ID){
        switch (MENU_ID){
            case R.id.byOverall:
                getOverall(MENU_ID);
                break;
            case R.id.byCategory:
                getCategory(MENU_ID);
                break;
            default:
                break;
        }
    }

    public void getOverall(int MENU_ID){
        getMvpView().onLoad(MENU_ID);
        compositeSubscription.add(getObservableOverall().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                fragmentModels ->{
                    getMvpView().setData(MENU_ID,fragmentModels);
                    getMvpView().onNext(MENU_ID);
                },
                throwable -> {
                    getMvpView().onError(MENU_ID);
                },
                ()->{
                    getMvpView().onComplete(MENU_ID);
                }
        ));
    }

    public void getCategory(int MENU_ID){
        getMvpView().onLoad(MENU_ID);
        compositeSubscription.add(getObservableCategory(MENU_ID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                fragmentModels->{
                    getMvpView().setData(MENU_ID,fragmentModels);
                    getMvpView().onNext(MENU_ID);
                },
                throwable -> {
                    getMvpView().onError(MENU_ID);
                },
                () -> {
                    getMvpView().onComplete(MENU_ID);
                }
        ));

    }

    public Observable<List<FragmentModel>> getObservableOverall(){

        List<FragmentModel> fragments = new ArrayList<>();
        for (String title : titles){
            float[] amounFlowIncome = getIncome(title);
            float[] amountFlowExpense = getExpense(title);
            float[] amountFlowNetIncome = getNet(amounFlowIncome,amountFlowExpense);
            ListTrendFragmentLineChart.DetailOverAll detailOverAll=  new ListTrendFragmentLineChart.DetailOverAll();
            detailOverAll.transaction = getTransaction(title);
            detailOverAll.avg_transaction = getAVGTransaction(title);
            detailOverAll.total_category = getTotalCategory(title);
            detailOverAll.total_expense = getTotalExpense(title);
            detailOverAll.total_income =  getTotalIncome(title);
            detailOverAll.balance = detailOverAll.total_income - detailOverAll.total_expense;

//            detailOverAll.transaction =
            Fragment fragment = ListTrendFragmentLineChart
                    .newInstance(generateTrendModelOverall(amounFlowIncome, amountFlowExpense, amountFlowNetIncome))
                    .setDetailOverAll(detailOverAll);
            FragmentModel fm = new FragmentModel(title,fragment);
            fragments.add(fm);
        }

        return Observable.just(fragments);
    }

    public float[] ArrayFloattoFloat(ArrayList<Float> floatList){
        int i = 0;
        float[] data = new float[floatList.size()];
        for (Float f : floatList) {
            data[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return data;
    }

    public ArrayList<Float> getFloat(String type,int days){
        HashMap<String,Double> datas =  db.getGroupAndSumCashflowBy(type,days);
        ArrayList<Float> values = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,0-(days-1));
        for (int i = 0; i < days; i++) {
            Date dt = calendar.getTime ();
            String val = df.format (dt);
            try {
                if(datas.containsKey(val)){
                    if(type.equalsIgnoreCase("DB"))
                        values.add(0-datas.get(val).floatValue());
                    else
                        values.add(datas.get(val).floatValue());
                }else {
                    values.add(Float.valueOf(0));
                }
            }catch (Exception e){
                Timber.e("error "+e);
            }

            calendar.add (Calendar.DAY_OF_YEAR, 1);
        }

        return values;
    }

    public float[] getIncome(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return ArrayFloattoFloat(getFloat("CR",7));
        }else if(title.equalsIgnoreCase(titles[1])){
            return ArrayFloattoFloat(getFloat("CR",30));
        }else if(title.equalsIgnoreCase(titles[2])){
            return ArrayFloattoFloat(getFloat("CR",60));
        }else if(title.equalsIgnoreCase(titles[3])){
            return ArrayFloattoFloat(getFloat("CR",90));
        }else {
            return null;
        }
    }

    public float getAVGTransaction(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return db.getCountCashByDaysAgo("All",7,session.getIdUser())/7;
        }else if(title.equalsIgnoreCase(titles[1])){
            return db.getCountCashByDaysAgo("All",30,session.getIdUser())/30;
        }else if(title.equalsIgnoreCase(titles[2])){
            return db.getCountCashByDaysAgo("All",60,session.getIdUser())/60;
        }else if(title.equalsIgnoreCase(titles[3])){
            return db.getCountCashByDaysAgo("All",90,session.getIdUser())/90;
        }else {
            return 0;
        }
    }

    public int getTransaction(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return db.getCountCashByDaysAgo("All",7,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[1])){
            return db.getCountCashByDaysAgo("All",30,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[2])){
            return db.getCountCashByDaysAgo("All",60,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[3])){
            return db.getCountCashByDaysAgo("All",90,session.getIdUser());
        }else {
            return 0;
        }
    }

    public int getTotalCategory(String title){
        if(title.equalsIgnoreCase(titles[0])){
        }else if(title.equalsIgnoreCase(titles[1])){
        }else if(title.equalsIgnoreCase(titles[2])){
        }else if(title.equalsIgnoreCase(titles[3])){
        }else {
            return 0;
        }
        return 0;
    }

    public float getTotalExpense(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return db.getTotalCashByDaysAgo("DB",7,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[1])){
            return db.getTotalCashByDaysAgo("DB",30,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[2])){
            return db.getTotalCashByDaysAgo("DB",60,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[3])){
            return db.getTotalCashByDaysAgo("DB",90,session.getIdUser());
        }else {
            return 0;
        }
    }

    public float getTotalIncome(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return db.getTotalCashByDaysAgo("CR",7,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[1])){
            return db.getTotalCashByDaysAgo("CR",30,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[2])){
            return db.getTotalCashByDaysAgo("CR",60,session.getIdUser());
        }else if(title.equalsIgnoreCase(titles[3])){
            return db.getTotalCashByDaysAgo("CR",90,session.getIdUser());
        }else {
            return 0;
        }
    }

    public float[] getExpense(String title){
        if(title.equalsIgnoreCase(titles[0])){
            return ArrayFloattoFloat(getFloat("DB",7));
        }else if(title.equalsIgnoreCase(titles[1])){
            return ArrayFloattoFloat(getFloat("DB",30));
        }else if(title.equalsIgnoreCase(titles[2])){
            return ArrayFloattoFloat(getFloat("DB",60));
        }else if(title.equalsIgnoreCase(titles[3])){
            return ArrayFloattoFloat(getFloat("DB",90));
        }else {
            return null;
        }
    }

    public float[] getNet(float[] amounFlowIncome,float[] amountFlowExpense){
        float[] floats = new float[amounFlowIncome.length];
        for (int i=0;i<floats.length;i++){
            floats[i] = amounFlowIncome[i]-amountFlowExpense[i];
        }
        return floats;
    }

    public Observable<List<FragmentModel>>getObservableCategory(int MENU_ID){
        List<FragmentModel> fragments = new ArrayList<>();
        fragments.add(new FragmentModel("Income", ListTrendFragmentPieChart.newInstance(generateTrendModelCategory("CR"))));
        fragments.add(new FragmentModel("Expense", ListTrendFragmentPieChart.newInstance(generateTrendModelCategory("DB"))));
        return Observable.just(fragments);
    }

    public List<TrendModelOverall> generateTrendModelOverall(float[] amounFlowIncome,float[] amountFlowExpense,float[] amountFlowNetIncome) {
        List<TrendModelOverall> models = new ArrayList<>();

        models.add(new TrendModelOverall("income", context.getResources().getColor(R.color.green_500),amounFlowIncome));
        models.add(new TrendModelOverall("expense", context.getResources().getColor(R.color.brown_300),amountFlowExpense));
        models.add(new TrendModelOverall("net income", context.getResources().getColor(R.color.orange_800),amountFlowNetIncome));

        return models;
    }

    public List<TrendModelCategory> generateTrendModelCategory(String type_mutasi) {
        List<TrendModelCategory> models = new ArrayList<>();
        ArrayList<Category> categories =  db.getCashGroupByCategory(String.valueOf(session.getIdUser()),type_mutasi);
        for (Category category:categories){
            float total = db.getTotalAmountCashByCategory(String.valueOf(category.getId_category()),type_mutasi,session.getIdUser());
            TrendModelCategory trendModelCategory = new TrendModelCategory(total, category);
            models.add(trendModelCategory);
        }
        return models;
    }

    public List<TrendModelCategory> generateTrendModelCategory(int count) {
        List<TrendModelCategory> models = new ArrayList<>();
        String[] colors = context.getResources().getStringArray(R.array.color_array);
        for (int i = 0; i < count; i++) {

            Category category = new Category();
            category.setName("embuh" + i);
            category.setIcon(DSFont.Icon.dsf_cosmetics.getFormattedName());
            category.setBgColor(colors[i]);

            TrendModelCategory trendModelCategory =
                    new TrendModelCategory(100 * Words.getRandomValue() * count, category);

            models.add(trendModelCategory);
        }

        return models;
    }



}
