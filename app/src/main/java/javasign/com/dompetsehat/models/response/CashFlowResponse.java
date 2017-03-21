package javasign.com.dompetsehat.models.response;

import java.util.List;

import javasign.com.dompetsehat.models.json.account;

/**
 * Created by aves on 8/12/16.
 */

public class CashFlowResponse extends ParentResponse {
    public Response response;
    public class Response{
        public int user_id;
        public List<account> accounts;
    }
}
