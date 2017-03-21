package javasign.com.dompetsehat.presenter.plan;

import javasign.com.dompetsehat.ui.CommonInterface;

/**
 * Created by aves on 9/6/16.
 */

public interface DanaInterface extends CommonInterface {
    void setUmur(int umur);
    void setDanaDisiapkan(double dana);
    void setProsentase(double prosentase);
    void setCicilanPerbulan(double cicilanPerbulan);
    void setPendapatanBulanan(double pendapatanBulanan);
    void setDibayarLunas(double dibayarLunas);
    void setCicilanPertahun(double cicilanPertahun);
}
