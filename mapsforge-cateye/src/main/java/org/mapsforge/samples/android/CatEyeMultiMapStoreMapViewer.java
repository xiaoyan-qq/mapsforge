/*
 * Copyright 2014-2015 Ludwig M Brinckmann
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.samples.android;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.reader.MapFile;

import java.io.File;

public class CatEyeMultiMapStoreMapViewer extends DefaultTheme {

    private MultiMapDataStore multiMapDataStore;

    @Override
    public MapDataStore getMapFile() {
        return this.multiMapDataStore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        multiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        multiMapDataStore.addMapDataStore(new MapFile(new File(getMapFileDirectory(), "world.map")), true, true);
        multiMapDataStore.addMapDataStore(new MapFile(new File(getMapFileDirectory(), "taiwan.map")), false, false);

        super.onCreate(savedInstanceState);

        mapView.setGestureDetector(new GestureDetector(CatEyeMultiMapStoreMapViewer.this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                if (motionEvent != null && motionEvent.getPointerCount() < 2) {
                    LatLong latLong = mapView.getMapViewProjection().fromPixels(motionEvent.getX(), motionEvent.getY());
                    Toast.makeText(CatEyeMultiMapStoreMapViewer.this, "用户点击:" + latLong.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        }));
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.cateye_main_mapview);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.cateye_main_mapview;
    }
}
