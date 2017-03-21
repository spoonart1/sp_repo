package javasign.com.dompetsehat.presenter.account;

import android.view.View;
import java.util.ArrayList;

import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;

/**
 * Created by aves on 9/7/16.
 */

public interface AccountInterface extends MvpView {
    void setTotalBalance(double totalBalance);
    void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList);
    void setSimpleAdapter(ArrayList<CellAccount> products);
    void setTotalAccount(int totalAccount);
    void deleteSuccess();
}
