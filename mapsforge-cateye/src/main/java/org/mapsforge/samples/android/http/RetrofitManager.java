package org.mapsforge.samples.android.http;

import org.mapsforge.samples.android.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by zhangdezhi1702 on 2018/1/11.
 */

public class RetrofitManager {
    private String baseUrl;
    public RetrofitManager instance;

    public RetrofitManager getInstance(String baseUrl) {
        if (instance == null) {
            instance = new RetrofitManager();
        }
        return instance;
    }

    public Retrofit getDefaultRetrofit() {
        if (StringUtils.isEmpty(baseUrl)) {
            return null;
        }
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        return new Converter<ResponseBody, String>() {
                            @Override
                            public String convert(ResponseBody value) throws IOException {
                                return value.toString();
                            }
                        };
                    }

                    @Override
                    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
                        return new Converter<RequestBody, RequestBody>() {

                            @Override
                            public RequestBody convert(RequestBody value) throws IOException {
                                return value;
                            }
                        };
                    }
                }).build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
