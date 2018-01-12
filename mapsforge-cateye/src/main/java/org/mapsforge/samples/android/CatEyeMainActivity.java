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
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jkb.fragment.rigger.annotation.Puppet;
import com.jkb.fragment.rigger.rigger.Rigger;

import org.greenrobot.eventbus.EventBus;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.CatEyeTileTMSSource;
import org.mapsforge.map.layer.download.tilesource.CatEyeTileXYZSource;
import org.mapsforge.map.layer.overlay.Polygon;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.samples.android.fragment.CatEyeMainFragment;
import org.mapsforge.samples.android.util.SystemConstant;

import java.io.File;

@Puppet(containerViewId = R.id.layer_main_root_fragment)
public class CatEyeMainActivity extends DefaultTheme {

    private MultiMapDataStore multiMapDataStore;
    private MAP_DRAW_STATE draw_state = MAP_DRAW_STATE.DRAW_FINISH;

    private Polyline currentEditPolyLine;
    private Polygon currentEditPolygon;

    private CheckBox chk_tms_world, chk_xyz_world, chk_tms_city, chk_xyz_city, chk_xyz_gujiao;
    private TileDownloadLayer worldTMSLayer, worldXYZLayer, cityTMSLayer, cityXYZLayer, gujiaoXYZLayer;
    private CatEyeTileTMSSource worldTMSTileSource, cityTMSTileSource;
    private CatEyeTileXYZSource worldXYZTileSource, cityXYZTileSource, gujiaoXYZTileSource;

    @Override
    public MapDataStore getMapFile() {
        return this.multiMapDataStore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        multiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        File worldFile = new File(getMapFileDirectory(), "world.map");
        if (worldFile.exists()) {
            multiMapDataStore.addMapDataStore(new MapFile(worldFile), true, true);
        }
        File taiwanFile = new File(getMapFileDirectory(), "taiwan.map");
        if (taiwanFile.exists()) {
            multiMapDataStore.addMapDataStore(new MapFile(taiwanFile), true, true);
        }
        super.onCreate(savedInstanceState);

        mapView.setGestureDetector(new GestureDetector(CatEyeMainActivity.this, new GestureDetector.OnGestureListener() {
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
                        mapView.addLayer(Utils.createTappableMarker(CatEyeMainActivity.this,
                                R.drawable.marker_red, latLong));
                    } else if (draw_state == MAP_DRAW_STATE.DRAW_LINE) {
                        if (currentEditPolyLine != null) {
                            currentEditPolyLine.getLatLongs().add(latLong);
                            if (currentEditPolyLine.getLatLongs().size() == 1) {//等于1时，绘制一个对应的点位marker
                                mapView.addLayer(Utils.createMarker(CatEyeMainActivity.this, R.drawable.point_orange, latLong));
                            }
                            mapView.getLayerManager().redrawLayers();
                        }
                    } else if (draw_state == MAP_DRAW_STATE.DRAW_POLYGON) {
                        if (currentEditPolygon != null) {
                            currentEditPolygon.getLatLongs().add(latLong);
                            if (currentEditPolygon.getLatLongs().size() == 1) {//等于1时，绘制一个对应的点位marker
                                mapView.addLayer(Utils.createMarker(CatEyeMainActivity.this, R.drawable.point_orange, latLong));
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

//        startFragment(CatEyeMainFragment.class);
        CatEyeMainFragment catEyeMainFragment=new CatEyeMainFragment();
        catEyeMainFragment.setArguments(new Bundle());
        Rigger.getRigger(CatEyeMainActivity.this).startFragment(catEyeMainFragment);
    }

    private void initTMSWorldLayerCheckBox() {
        worldTMSTileSource = new CatEyeTileTMSSource(new String[]{
                "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                8080);

        worldTMSTileSource.setName("worldTMS").setAlpha(true)
                .setBaseUrl("/tms/1.0.0/world_satellite_raster@EPSG:900913@jpeg/").setExtension("jpeg")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
        worldTMSTileSource.setUserAgent("Mapsforge Samples");

        CatEyeMainActivity.this.worldTMSLayer = new TileDownloadLayer(this.tileCaches.get(1),
                CatEyeMainActivity.this.mapView.getModel().mapViewPosition, worldTMSTileSource,
                AndroidGraphicFactory.INSTANCE);

        chk_tms_world = findViewById(R.id.chk_main_mapview_tms_world);
        chk_tms_world.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapView.getLayerManager().getLayers().add(CatEyeMainActivity.this.worldTMSLayer);
                    worldTMSLayer.start();
                    setMaxTextWidthFactor();
                } else {
                    mapView.getLayerManager().getLayers().remove(CatEyeMainActivity.this.worldTMSLayer);
                    setMaxTextWidthFactor();
                }
            }
        });
    }

    private void initXYZWorldLayerCheckBox() {
        worldXYZTileSource = new CatEyeTileXYZSource(new String[]{
                "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                8080);

        worldXYZTileSource.setName("worldXYZ").setAlpha(true)
                .setBaseUrl("/xyz/world/").setExtension("jpeg")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
        worldXYZTileSource.setUserAgent("Mapsforge Samples");

        CatEyeMainActivity.this.worldXYZLayer = new TileDownloadLayer(this.tileCaches.get(2),
                CatEyeMainActivity.this.mapView.getModel().mapViewPosition, worldXYZTileSource,
                AndroidGraphicFactory.INSTANCE);

        chk_xyz_world = findViewById(R.id.chk_main_mapview_xyz_world);
        chk_xyz_world.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapView.getLayerManager().getLayers().add(CatEyeMainActivity.this.worldXYZLayer);
                    worldXYZLayer.start();
                    setMaxTextWidthFactor();
                } else {
                    mapView.getLayerManager().getLayers().remove(CatEyeMainActivity.this.worldXYZLayer);
                    setMaxTextWidthFactor();
                }
            }
        });
    }

    private void initTMSCityLayerCheckBox() {
        cityTMSTileSource = new CatEyeTileTMSSource(new String[]{
                "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                8080);

        cityTMSTileSource.setName("cityTMS").setAlpha(true)
                .setBaseUrl("/tms/1.0.0/china_city_polygon@EPSG:900913@png/").setExtension("png")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
        cityTMSTileSource.setUserAgent("Mapsforge Samples");

        CatEyeMainActivity.this.cityTMSLayer = new TileDownloadLayer(this.tileCaches.get(3),
                CatEyeMainActivity.this.mapView.getModel().mapViewPosition, cityTMSTileSource,
                AndroidGraphicFactory.INSTANCE);

        chk_tms_city = findViewById(R.id.chk_main_mapview_tms_city);
        chk_tms_city.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapView.getLayerManager().getLayers().add(CatEyeMainActivity.this.cityTMSLayer);
                    cityTMSLayer.start();
                    setMaxTextWidthFactor();
                } else {
                    mapView.getLayerManager().getLayers().remove(CatEyeMainActivity.this.cityTMSLayer);
                    setMaxTextWidthFactor();
                }
            }
        });
    }

    private void initXYZCityLayerCheckBox() {
        cityXYZTileSource = new CatEyeTileXYZSource(new String[]{
                "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                8080);

        cityXYZTileSource.setName("cityXYZ").setAlpha(true)
                .setBaseUrl("/xyz/city/").setExtension("json")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
        cityXYZTileSource.setUserAgent("Mapsforge Samples");

        CatEyeMainActivity.this.cityXYZLayer = new TileDownloadLayer(this.tileCaches.get(4),
                CatEyeMainActivity.this.mapView.getModel().mapViewPosition, cityXYZTileSource,
                AndroidGraphicFactory.INSTANCE);

        chk_xyz_city = findViewById(R.id.chk_main_mapview_xyz_city);
        chk_xyz_city.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapView.getLayerManager().getLayers().add(CatEyeMainActivity.this.cityXYZLayer);
                    cityXYZLayer.start();
                    setMaxTextWidthFactor();
                } else {
                    mapView.getLayerManager().getLayers().remove(CatEyeMainActivity.this.cityXYZLayer);
                    setMaxTextWidthFactor();
                }
            }
        });
    }

    private void initXYZGujiaoLayerCheckBox() {
        gujiaoXYZTileSource = new CatEyeTileXYZSource(new String[]{
                "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                8080);

        gujiaoXYZTileSource.setName("cityXYZ").setAlpha(true)
                .setBaseUrl("/xyz/dem/").setExtension("tif")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
        gujiaoXYZTileSource.setUserAgent("Mapsforge Samples");

        CatEyeMainActivity.this.gujiaoXYZLayer = new TileDownloadLayer(this.tileCaches.get(5),
                CatEyeMainActivity.this.mapView.getModel().mapViewPosition, gujiaoXYZTileSource,
                AndroidGraphicFactory.INSTANCE);

        chk_xyz_gujiao = findViewById(R.id.chk_main_mapview_xyz_gujiao);
        chk_xyz_gujiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapView.getLayerManager().getLayers().add(CatEyeMainActivity.this.gujiaoXYZLayer);
                    gujiaoXYZLayer.start();
                    setMaxTextWidthFactor();
                } else {
                    mapView.getLayerManager().getLayers().remove(CatEyeMainActivity.this.gujiaoXYZLayer);
                    setMaxTextWidthFactor();
                }
            }
        });
    }

    @Override
    protected void createLayers() {
        super.createLayers();
        initTMSWorldLayerCheckBox();
        initXYZWorldLayerCheckBox();
        initTMSCityLayerCheckBox();
        initXYZCityLayerCheckBox();
        initXYZGujiaoLayerCheckBox();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.cateye_main_mapview;
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


    @Override
    protected void createTileCaches() {
        super.createTileCaches();
        this.tileCaches.add(createTileCacheTMSWorld());
        this.tileCaches.add(createTileCacheXYZWorld());
        this.tileCaches.add(createTileCacheTMSCity());
        this.tileCaches.add(createTileCacheXYZCity());
        this.tileCaches.add(createTileCacheXYZGuJiao());
    }

    protected TileCache createTileCacheTMSWorld() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        return AndroidUtil.createTileCache(this, getPersistableId() + "worldTMS", tileSize,
                getScreenRatio(), this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    protected TileCache createTileCacheXYZWorld() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        return AndroidUtil.createTileCache(this, getPersistableId() + "worldXYZ", tileSize,
                getScreenRatio(), this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    protected TileCache createTileCacheTMSCity() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        return AndroidUtil.createTileCache(this, getPersistableId() + "cityTMS", tileSize,
                getScreenRatio(), this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    protected TileCache createTileCacheXYZCity() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        return AndroidUtil.createTileCache(this, getPersistableId() + "cityXYZ", tileSize,
                getScreenRatio(), this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    protected TileCache createTileCacheXYZGuJiao() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        return AndroidUtil.createTileCache(this, getPersistableId() + "gujiaoXYZ", tileSize,
                getScreenRatio(), this.mapView.getModel().frameBufferModel.getOverdrawFactor());
    }

    public CatEyeTileTMSSource getCityTMSTileSource() {
        return cityTMSTileSource;
    }
}
