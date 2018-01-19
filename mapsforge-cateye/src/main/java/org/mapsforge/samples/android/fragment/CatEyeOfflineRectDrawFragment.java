package org.mapsforge.samples.android.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vondear.rxtools.view.RxProgressBar;
import com.vondear.rxtools.view.RxSeekBar;
import com.vondear.rxtools.view.RxToast;
import com.vondear.rxtools.view.dialog.RxDialog;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;
import org.mapsforge.map.util.LayerUtil;
import org.mapsforge.samples.android.CatEyeMainActivity;
import org.mapsforge.samples.android.R;
import org.mapsforge.samples.android.util.SystemConstant;
import org.mapsforge.samples.android.view.OfflineMapRectDrawView;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhangdezhi1702 on 2018/1/8.
 */

public class CatEyeOfflineRectDrawFragment extends BaseNoFragment {
    private OfflineMapRectDrawView mOfflineMapRectDrawView;
    private Button btn_draw_ok;
    private AbstractTileSource tileSource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cateye_offline_rect_draw_fragment, container, false);
        tileSource = ((CatEyeMainActivity) getActivity()).getCityTMSTileSource();
        return rootView;
    }

    @Override
    public void initView(View rootView) {
        mOfflineMapRectDrawView = rootView.findViewById(R.id.layer_offline_map_rect_draw_view);
        btn_draw_ok = rootView.findViewById(R.id.btn_offline_rect_draw_ok);
        btn_draw_ok.setOnClickListener(offlineRectConfirmListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    View.OnClickListener offlineRectConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOfflineMapRectDrawView != null) {
                RectF rectF = mOfflineMapRectDrawView.getDrawRect();
                if (rectF != null) {
                    LatLong leftTopLatLong = ((CatEyeMainActivity) getActivity()).getCurrentMapView().getMapViewProjection().fromPixels(rectF.left, rectF.top);
                    LatLong rightBottomLatLong = ((CatEyeMainActivity) getActivity()).getCurrentMapView().getMapViewProjection().fromPixels(rectF.right, rectF.bottom);
                    BoundingBox boundingBox = new BoundingBox(leftTopLatLong.latitude > rightBottomLatLong.latitude ? rightBottomLatLong.latitude : leftTopLatLong.latitude,
                            leftTopLatLong.longitude > rightBottomLatLong.longitude ? rightBottomLatLong.longitude : leftTopLatLong.longitude,
                            leftTopLatLong.latitude > rightBottomLatLong.latitude ? leftTopLatLong.latitude : rightBottomLatLong.latitude,
                            leftTopLatLong.longitude > rightBottomLatLong.longitude ? leftTopLatLong.longitude : rightBottomLatLong.longitude);

                    showSelectLevelDialog(boundingBox);
                } else {
                    Toast.makeText(getContext(), "请在界面上绘制矩形框", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    int successDownloadSize=0;
    Set<URL> errorURLSet=new HashSet<>();
    private void showSelectLevelDialog(final BoundingBox boundingBox) {
        //对话框提示用户选择要下载的数据级别范围
        RxDialog rxDialog = new RxDialog(getContext());
        rxDialog.setFullScreen();
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tile_download_progress, null);
        final RxSeekBar seekBar = dialogView.findViewById(R.id.rx_range_seekbar_level);
        seekBar.setValue(0f, ((CatEyeMainActivity) getActivity()).getCurrentMapView().getModel().mapViewPosition.getZoomLevel());
        Button btn_ok = dialogView.findViewById(R.id.button_tile_level_confirm);

        final View downloadLevelLayer = dialogView.findViewById(R.id.layer_tile_download_level_select);
        final RxProgressBar rxProgressBar = dialogView.findViewById(R.id.rxpb_download_progressbar);
        btn_ok.setOnClickListener(new View.OnClickListener() {//确认选中的等级
            @Override
            public void onClick(View view) {
                float[] currentRange=seekBar.getCurrentRange();
                if (currentRange.length>1){
                    int minLevel = (int) (currentRange[0]<=currentRange[1]?currentRange[0]:currentRange[1]);
                    int maxLevel = (int) (currentRange[0]<=currentRange[1]?currentRange[1]:currentRange[0]);
                    if (minLevel <= maxLevel) {
                        final Set<Tile> tileList = new HashSet<>();
                        for (int i = minLevel; i <= maxLevel; i++) {
                            tileList.addAll(LayerUtil.getTiles(boundingBox, (byte) i, SystemConstant.MAX_TILE_SIZE));
                        }
                        if (tileList.size() > SystemConstant.MAX_TILE_SIZE) {//要下载的tile数量超过了1024，提示用户数量过大
                            RxToast.error("下载的tile数量为" + tileList.size() + ",超过" + SystemConstant.MAX_TILE_SIZE + ",请重新规划");
                        } else {
                            //开始下载tileList的数据
                            downloadLevelLayer.setVisibility(View.GONE);
                            rxProgressBar.setVisibility(View.VISIBLE);

                            final Iterator<Tile> tileIterator= tileList.iterator();
                            Flowable flowable=Flowable.create(new FlowableOnSubscribe<Tile>() {
                                @Override
                                public void subscribe(FlowableEmitter<Tile> emitter){
                                    long requestedSize=emitter.requested();
                                    while (requestedSize>0&&tileIterator.hasNext()){
                                        emitter.onNext(tileIterator.next());
                                    }
                                    emitter.onComplete();
                                }
                            }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).map(new Function<Tile, URL>() {
                                @Override
                                public URL apply(Tile tile) throws Exception {
                                    return tileSource.getTileUrl(tile);
                                }
                            });

                            Subscriber<URL> subscriber=new Subscriber<URL>() {
                                @Override
                                public void onSubscribe(Subscription s) {

                                }

                                @Override
                                public void onNext(URL url) {
                                    RequestParams requestParams = new RequestParams(url.toString());
                                    String path=url.getPath();
                                    requestParams.setSaveFilePath(path);
                                    x.http().post(requestParams, new Callback.ProgressCallback<String>() {
                                        @Override
                                        public void onWaiting() {

                                        }

                                        @Override
                                        public void onStarted() {

                                        }

                                        @Override
                                        public void onLoading(long total, long current, boolean isDownloading) {

                                        }

                                        @Override
                                        public void onSuccess(String result) {
                                            successDownloadSize++;
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {

                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {

                                        }

                                        @Override
                                        public void onFinished() {

                                        }
                                    });
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            };
                        }
                    } else {
                        RxToast.error("选择的级别不符合规则");
                    }
                }
            }
        });
        rxDialog.setContentView(dialogView);
        rxDialog.show();
    }
}
