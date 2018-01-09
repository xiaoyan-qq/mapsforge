package org.mapsforge.samples.android.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkb.fragment.rigger.annotation.Puppet;

/**
 * Created by zhangdezhi1702 on 2018/1/8.
 */
@Puppet
public abstract class BaseNoFragment extends Fragment {
    protected View rootView;
    protected abstract void initView(View rootView);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (rootView!=null){
            initView(rootView);
        }
    }
}
