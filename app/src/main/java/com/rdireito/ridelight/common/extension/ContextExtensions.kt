package com.rdireito.ridelight.common.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.openAppSettingsDetails() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.data = Uri.parse("package:$packageName")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
        Intent.FLAG_ACTIVITY_NO_HISTORY or
        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    startActivity(intent)
}