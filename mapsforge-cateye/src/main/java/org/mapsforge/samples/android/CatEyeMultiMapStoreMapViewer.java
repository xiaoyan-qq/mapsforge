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
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.greenrobot.eventbus.EventBus;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.overlay.Polygon;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.samples.android.fragment.CatEyeMainFragment;
import org.mapsforge.samples.android.util.SystemConstant;

import java.io.File;

public class CatEyeMultiMapStoreMapViewer extends DefaultTheme {

    private MultiMapDataStore multiMapDataStore;
    private MAP_DRAW_STATE draw_state = MAP_DRAW_STATE.DRAW_FINISH;

    private Polyline currentEditPolyLine;
    private Polygon currentEditPolygon;

    @Override
    public MapDataStore getMapFile() {
        return this.multiMapDataStore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        multiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        File worldFile=new File(getMapFileDirectory(), "world.map");
        if (worldFile.exists()){
            multiMapDataStore.addMapDataStore(new MapFile(worldFile), true, true);
        }
        File taiwanFile=new File(getMapFileDirectory(), "taiwan.map");
        if (taiwanFile.exists()){
            multiMapDataStore.addMapDataStore(new MapFile(taiwanFile), true, true);
        }
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
                    if (draw_state == MAP_DRAW_STATE.DRAW_FINISH) {
                        Message msg = new Message();
                        msg.what = SystemConstant.MSG_KEY_EVENT_MAP_TAP;
                        msg.obj = latLong;
                        EventBus.getDefault().post(msg);
                    } else if (draw_state == MAP_DRAW_STATE.DRAW_POINT) {
                        mapView.addLayer(Utils.createTappableMarker(CatEyeMultiMapStoreMapViewer.this,
                                R.drawable.marker_red, latLong));
                    } else if (draw_state == MAP_DRAW_STATE.DRAW_LINE) {
                        if (currentEditPolyLine != null) {
                            currentEditPolyLine.getLatLongs().add(latLong);
                            if (currentEditPolygon.getLatLongs().size() == 1) {//等于1时，绘制一个对应的点位marker
                                mapView.addLayer(Utils.createMarker(CatEyeMultiMapStoreMapViewer.this, R.drawable.point_orange, latLong));
                            }
                            mapView.getLayerManager().redrawLayers();
                        }
                    } else if (draw_state == MAP_DRAW_STATE.DRAW_POLYGON) {
                        if (currentEditPolygon != null) {
                            currentEditPolygon.getLatLongs().add(latLong);
                            if (currentEditPolygon.getLatLongs().size() == 1) {//等于1时，绘制一个对应的点位marker
                                mapView.addLayer(Utils.createMarker(CatEyeMultiMapStoreMapViewer.this, R.drawable.point_orange, latLong));
                            }
                            mapView.getLayerManager().redrawLayers();
                        }
                    }
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

        startFragment(CatEyeMainFragment.class);
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

    @Override
    protected int fragmentLayoutId() {
        return R.id.layer_main_root_fragment;
    }

    public enum MAP_DRAW_STATE {
        DRAW_POINT, DRAW_LINE, DRAW_POLYGON, DRAW_FINISH;
    }

    public MAP_DRAW_STATE getDraw_state() {
        return draw_state;
    }

    public void setDraw_state(MAP_DRAW_STATE draw_state) {
        this.draw_state = draw_state;
    }

    public void addNewPolyLineOverlay() {
        if (mapView != null) {
            currentEditPolyLine = new Polyline(Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN), 9, Style.STROKE), AndroidGraphicFactory.INSTANCE, true);
            mapView.getLayerManager().getLayers().add(currentEditPolyLine);
        }
    }

    public void addNewPolygonOverlay() {
        if (mapView != null) {
            currentEditPolygon = new Polygon(Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.RED), 9, Style.FILL), Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.RED), 9, Style.STROKE), AndroidGraphicFactory.INSTANCE, true);
            mapView.getLayerManager().getLayers().add(currentEditPolygon);
        }
    }


}
