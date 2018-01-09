package org.mapsforge.samples.android.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.samples.android.R;
import org.mapsforge.samples.android.view.OfflineMapRectDrawView;

/**
 * Created by zhangdezhi1702 on 2018/1/8.
 */

public class CatEyeOfflineRectDrawFragment extends BaseNoFragment {
    private OfflineMapRectDrawView mOfflineMapRectDrawView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cateye_offline_rect_draw_fragment, container,false);
        return rootView;
    }

    @Override
    public void initView(View rootView) {
        mOfflineMapRectDrawView=rootView.findViewById(R.id.layer_offline_map_rect_draw_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
