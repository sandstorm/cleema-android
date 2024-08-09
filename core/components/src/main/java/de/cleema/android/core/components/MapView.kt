package de.cleema.android.components.map/*
 * Created by Kumpels and Friends on 2022-11-10
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import de.cleema.android.core.components.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapView(
    coordinates: de.cleema.android.core.models.Coordinates,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

            MapView(context).apply {
                setTileSource(TileSourceFactory.WIKIMEDIA)
            }
        },
        update = { view ->
            val mapController = view.controller
            mapController.setZoom(14.5)

            val startPoint = GeoPoint(coordinates.latitude, coordinates.longitude)
            mapController.setCenter(startPoint)
            val marker = Marker(view)
            marker.icon = view.context.getDrawable(R.drawable.location_marker)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            view.overlays.add(marker)
            marker.setOnMarkerClickListener { _, _ ->
                onClick()
                true
            }
        }
    )
}
