package dc.danh.sofuser.controller.network;

import dc.danh.sofuser.model.ReputationObject;
import dc.danh.sofuser.model.SOFUserObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SOFService {
    @GET("users?site=stackoverflow")
    Call<SOFUserObject> getSOFUserList(@Query("page") int page, @Query("pagesize") int pagesize);

    @GET("users/{userid}/reputation-history?site=stackoverflow")
    Call<ReputationObject> getSOFUserReputationHistory(@Path("userid") int userid, @Query("page") int page, @Query("pagesize") int pagesize);
}
