package javasign.com.dompetsehat.presenter.reminder;

import java.util.List;

import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;

/**
 * Created by aves on 9/5/16.
 */

public interface ReminderInterface extends CommonInterface {
    void setHeaderData(int count, double totalBill, double totalBalance);
    void setAdapter(List<ReminderModel> reminderModels);
    void setAlarm(Alarm alarm,Category category,UserCategory userCategory);
}
