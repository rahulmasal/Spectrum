package com.switcher.fiveg.tile

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.TileService

class NetworkModeTileService : TileService() {
    override fun onClick() {
        super.onClick()
        try {
            val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndCollapse(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndCollapse(intent)
        }
    }
}
