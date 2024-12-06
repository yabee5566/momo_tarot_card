package com.onean.momo.ext

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

val Context.application get() = this.applicationContext as Application

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toSp: Float
    get() = (this / Resources.getSystem().displayMetrics.scaledDensity)

fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density

fun Context.inputManager(): InputMethodManager {
    return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}

fun Context.screenHeight(): Int = resources.displayMetrics.heightPixels

fun Context.screenWidth(): Int = resources.displayMetrics.widthPixels

fun Context.hideKeyboard(view: View) {
    inputManager().hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    inputManager().toggleSoftInputFromWindow(view.windowToken, 0, 0)
}

fun Context.connectivityManager(): ConnectivityManager = getSystemService()!!

fun Context.activityManager(): ActivityManager = getSystemService()!!

fun Context.toast(resourceId: Int) {
    toast(getString(resourceId))
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.shortToast(resourceId: Int) {
    shortToast(getString(resourceId))
}

fun Context.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.isPhotoPickerAvailable(): Boolean {
    return ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)
}

fun Context.isSupportPipMode(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
}

fun Context.hasPipPermission(): Boolean {
    val appsOps = getSystemService<AppOpsManager>() ?: return false
    return runCatching {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                appsOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }

            else -> {
                appsOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
        }
    }.getOrDefault(false)
}

fun Context.launchPipSettingPage() = startActivity(
    Intent(
        ACTION_PICTURE_IN_PICTURE_SETTINGS,
        Uri.parse("package:$packageName")
    )
)

private const val ACTION_PICTURE_IN_PICTURE_SETTINGS = "android.settings.PICTURE_IN_PICTURE_SETTINGS"
