package org.mapsforge.samples.android.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.fragment.NoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.samples.android.R;
import org.mapsforge.samples.android.util.SystemConstant;

/**
 * Created by zhangdezhi1702 on 2017/12/20.
 */

public class CatEyeMainFragment extends NoFragment {
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cateye_main_root_fragment, null);
        return rootView;
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
}
