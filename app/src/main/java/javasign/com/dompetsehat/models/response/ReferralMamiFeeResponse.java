package javasign.com.dompetsehat.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by avesina on 10/25/16.
 */

public class ReferralMamiFeeResponse {
  public String status;
  public Data data;

  public class Data {
    public String StartPeriod;
    public String EndPeriod;
    public ReferralFeeDetails ReferralFeeDetails;

    public class ReferralFeeDetails {
      public List<ReferralFeeDetail> ReferralFeeDetail;

      public class ReferralFeeDetail {
        public String RefCode; //": "GFT890",
        public String Currency; //": "IDR",
        public double TotalFee; //": "318290",
        public double TotalSubcription; //": "0",
        public int Pending; //": "318290",
        public ReferralFeeHistoryDetails ReferralFeeHistoryDetails;

        public class ReferralFeeHistoryDetails {
          @SerializedName("ReferralFeeHistoryDetail") public List<ReferralFeeHistoryDetail> ReferralFeeHistoryDetail;
        }
      }
    }
  }
}



