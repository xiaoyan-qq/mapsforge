package org.mapsforge.samples.android.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jkb.fragment.rigger.rigger.Rigger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.samples.android.CatEyeMultiMapStoreMapViewer;
import org.mapsforge.samples.android.R;
import org.mapsforge.samples.android.util.SystemConstant;

/**
 * Created by zhangdezhi1702 on 2017/12/20.
 */

public class CatEyeMainFragment extends BaseNoFragment {
    private Button btn_drawPoint, btn_drawLine, btn_drawPolygon, btn_drawFinish, btn_donwload;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        rootView = inflater.inflate(R.layout.cateye_main_root_fragment, container, false);
        return rootView;
    }

    @Override
    protected void initView(View rootView) {
        btn_drawPoint = rootView.findViewById(R.id.btn_main_root_fragment_drawPoint);
        btn_drawLine = rootView.findViewById(R.id.btn_main_root_fragment_drawLine);
        btn_drawPolygon = rootView.findViewById(R.id.btn_main_root_fragment_drawPolygon);
        btn_drawFinish = rootView.findViewById(R.id.btn_main_root_fragment_drawFinish);
        btn_donwload = rootView.findViewById(R.id.btn_main_root_fragment_download);

        btn_drawPoint.setOnClickListener(clickListener);
        btn_drawLine.setOnClickListener(clickListener);
        btn_drawPolygon.setOnClickListener(clickListener);
        btn_drawFinish.setOnClickListener(clickListener);
        btn_donwload.setOnClickListener(downloadListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onGetNewEvent(Message msg) {
        switch (msg.what) {
            case SystemConstant.MSG_KEY_EVENT_MAP_TAP://用户单机地图
                LatLong latLong = (LatLong) msg.obj;
                Toast.makeText(getContext(), "用户点击:" + latLong.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_main_root_fragment_drawPoint) {//绘制点
                ((CatEyeMultiMapStoreMapViewer) getActivity()).setDraw_state(CatEyeMultiMapStoreMapViewer.MAP_DRAW_STATE.DRAW_POINT);
            } else if (view.getId() == R.id.btn_main_root_fragment_drawLine) {//绘制线
                ((CatEyeMultiMapStoreMapViewer) getActivity()).setDraw_state(CatEyeMultiMapStoreMapViewer.MAP_DRAW_STATE.DRAW_LINE);
                ((CatEyeMultiMapStoreMapViewer) getActivity()).addNewPolyLineOverlay();
            } else if (view.getId() == R.id.btn_main_root_fragment_drawPolygon) {//绘制面
                ((CatEyeMultiMapStoreMapViewer) getActivity()).setDraw_state(CatEyeMultiMapStoreMapViewer.MAP_DRAW_STATE.DRAW_POLYGON);
                ((CatEyeMultiMapStoreMapViewer) getActivity()).addNewPolygonOverlay();
            } else if (view.getId() == R.id.btn_main_root_fragment_drawFinish) {//绘制结束
                ((CatEyeMultiMapStoreMapViewer) getActivity()).setDraw_state(CatEyeMultiMapStoreMapViewer.MAP_DRAW_STATE.DRAW_FINISH);
            }
        }
    };

    View.OnClickListener downloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            BoundingBox boundingBox=new BoundingBox(33.6,80,55.0,118.0);
//            Set<Tile> tileList=LayerUtil.getTiles(boundingBox,(byte) 7,1024);
//            for (Tile tile:tileList){
//                try {
//                    Log.e("CatEye",((CatEyeMultiMapStoreMapViewer)getActivity()).getCityTMSTileSource().getTileUrl(tile).toString());
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }
            CatEyeOfflineRectDrawFragment catEyeOfflineRectDrawFragment = new CatEyeOfflineRectDrawFragment();
            Bundle bundle=new Bundle();
            catEyeOfflineRectDrawFragment.setArguments(bundle);
            Rigger.getRigger(CatEyeMainFragment.this).startFragment(catEyeOfflineRectDrawFragment);
//            try {
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://api.github.com")
//                        .addConverterFactory(new Converter.Factory() {
//                            @Override
//                            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//                                return new Converter<ResponseBody, String>() {
//                                    @Override
//                                    public String convert(ResponseBody value) throws IOException {
//                                        return value.toString();
//                                    }
//                                };
//                            }
//
//                            @Override
//                            public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//                                return new Converter<RequestBody, RequestBody>() {
//
//                                    @Override
//                                    public RequestBody convert(RequestBody value) throws IOException {
//                                        return value;
//                                    }
//                                };
//                            }
//                        })
//                        .build();
//
//                GitHubService service = retrofit.create(GitHubService.class);
//                Call<String> stringCall=service.listRepos("xiaoyan159");
//                stringCall.enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        Log.d("TAG",response.message());
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        Log.e("TAG",t.getMessage());
//                    }
//                });
//            }catch (Exception e){
//                Log.e("TAG",e.getMessage());
//            }

        }
    };
}
