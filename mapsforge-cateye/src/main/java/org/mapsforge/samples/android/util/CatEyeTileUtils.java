package org.mapsforge.samples.android.util;

import android.content.Context;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;
import org.mapsforge.map.layer.download.tilesource.CatEyeTileTMSSource;
import org.mapsforge.map.layer.download.tilesource.CatEyeTileXYZSource;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.view.MapView;

import java.util.List;

/**
 * Created by zhangdezhi1702 on 2018/1/17.
 */

public class CatEyeTileUtils {
    public static TileDownloadLayer createTileDownloadLayer(TileCache tileCache, TILE_SOURCE_TYPE tile_source_type, String name, String baseUrl, boolean isAlpha, String extension, MapViewPosition mapViewPosition) {
        AbstractTileSource tileSource = null;
        if (tile_source_type == TILE_SOURCE_TYPE.TILE_SOURCE_TMS) {
            CatEyeTileTMSSource tmsTileSource = new CatEyeTileTMSSource(new String[]{
                    "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                    8080);
            tmsTileSource.setName(name).setAlpha(isAlpha)
                    .setBaseUrl(baseUrl).setExtension(extension)
                    .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                    .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
            tileSource = tmsTileSource;
        } else/* if (tile_source_type==TILE_SOURCE_TYPE.TILE_SOURCE_XYZ)*/ {
            CatEyeTileXYZSource xyzTileSource = new CatEyeTileXYZSource(new String[]{
                    "54.223.166.139"/*, "b.tile.openstreetmap.fr", "c.tile.openstreetmap.fr"*/},
                    8080);
            xyzTileSource.setName(name).setAlpha(isAlpha)
                    .setBaseUrl(baseUrl).setExtension(extension)
                    .setParallelRequestsLimit(8).setProtocol("http").setTileSize(SystemConstant.MAX_TILE_SIZE)
                    .setZoomLevelMax((byte) 12).setZoomLevelMin((byte) 0);
            tileSource = xyzTileSource;
        }

        tileSource.setUserAgent("Mapsforge Samples");

        return new TileDownloadLayer(tileCache,
                mapViewPosition, tileSource,
                AndroidGraphicFactory.INSTANCE);
    }

    public static TileCache createTileCache(Context mContext, MapView mapView, List<TileCache> tileCaches, String cacheId) {
        int tileSize = mapView.getModel().displayModel.getTileSize();
        TileCache tileCache = AndroidUtil.createTileCache(mContext, cacheId, tileSize,
                1.0f, mapView.getModel().frameBufferModel.getOverdrawFactor());
        tileCaches.add(tileCache);
        return tileCache;
    }

    public enum TILE_SOURCE_TYPE {
        TILE_SOURCE_TMS, TILE_SOURCE_XYZ
    }
}
