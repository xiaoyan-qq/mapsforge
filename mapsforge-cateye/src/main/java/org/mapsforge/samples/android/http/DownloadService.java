package org.mapsforge.samples.android.http;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by zhangdezhi1702 on 2018/1/8.
 */

public interface DownloadService {
    @POST("/service/man/userInfo/login")
    Call<Observable> downloadTile(@Body RequestBody body);
}
