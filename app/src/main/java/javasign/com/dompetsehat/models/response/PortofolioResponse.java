package javasign.com.dompetsehat.models.response;

import com.google.gson.Gson;
import java.util.List;

/**
 * Created by avesina on 10/25/16.
 */

public class PortofolioResponse {
  public String status;//": "success",
  public String message;
  public Data data;

  public class Data {
    public String CifNum;//": "P000166985",
    public String ClientNum;//": "0000000166145",
    public String PortfolioNum; //": "P000166985",
    public String CliFirstNm;//": "RUDI",
    public String CliLastNm; //": "SANTOSO",
    public int CliRegStat; //": 1,
    public portfolio Portfolio;

    public class portfolio {
      public investmentAccounts InvestmentAccounts;
      public class investmentAccounts{
        public List<investmentAccount> InvestmentAccount;
        public class investmentAccount{
          public String AccountNo;//":"A000247904",
          public String FundId;//":"033",
          public String FundName;//":"MANULIFE DANA CAMPURAN II",
          public String FundVersion;//":"01",
          public String FundCurrencyCode;//":"88",
          public String FundCurrency;//":"IDR",
          public String FundCategoryCode;//":"B",
          public String FundCategory;//":"Balance Fund",
          public double UnitOnHand;//":"825.6615613260124670",
          public double NavPerUnit;//":"2422.30",
          public String FundLastValuationDate;//":"2016-08-26T00:00:00",
          public double AccountBalance;//":"1999999.9999",
          public int ExchangeRate;//":"1"

          @Override public String toString() {
            return new Gson().toJson(this);
          }
        }
      }
    }

    @Override public String toString() {
      return new Gson().toJson(this);
    }
  }
}
