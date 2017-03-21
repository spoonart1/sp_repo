package javasign.com.dompetsehat.presenter.transaction;

import java.util.ArrayList;
import java.util.List;

import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;

/**
 * Created by aves on 8/18/16.
 */

public interface TransactionsInterface extends CommonInterface {
    void setAdapterTransaction(List<FragmentModel> fragmentModels,int position, boolean isCardView);
    void setTimeline(TimelineView timelineView, boolean isBroadcast);
    void initTags(List<String> tags);
    void setTransaction(ArrayList<Transaction> transactions);
}
