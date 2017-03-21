package javasign.com.dompetsehat.job;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by avesina on 12/29/16.
 */

public class DompetSehatJobCreator implements JobCreator {


  @Override public Job create(String tag) {
    switch (tag) {
      case SyncJob.TAG:
        return new SyncJob();
      default:
        return null;
    }
  }
}
