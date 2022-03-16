package net.noimply.sentence.business.support

import android.app.Activity
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.database.ContentObserver
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*

// region { String-Encode }
val String.toUrlEncode: String
    get() {
        return try {
            URLEncoder.encode(this, "UTF-8")
        } catch (e: Exception) {
            ""
        }
    }

val String.toBase64: String
    get() {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }
// endregion


// region { View }
fun View.setOnClickWithDebounce(debounceTime: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            val elapsedRealtime = SystemClock.elapsedRealtime()
            if (elapsedRealtime - lastClickTime < debounceTime) {
                return
            } else {
                action()
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
// endregion


// region { activity }
fun <T : Parcelable> Activity.extraParcel(key: String): T? {
    return intent?.extras?.getParcelable(key)
}

fun Activity.extraString(key: String): String? {
    return intent?.extras?.getString(key)
}

fun Activity.extraInt(key: String): Int? {
    return intent?.extras?.getInt(key)
}

fun Activity.extraLong(key: String): Long? {
    return intent?.extras?.getLong(key)
}

fun Activity.extraBoolean(key: String): Boolean? {
    return intent?.extras?.getBoolean(key)
}

fun Activity.extraFloat(key: String): Float? {
    return intent?.extras?.getFloat(key)
}

val Activity?.isAlive: Boolean
    get() = !(this?.isFinishing ?: true)

fun Activity.open(intent: Intent, transition: Pair<Int, Int>? = null, close: Boolean = false, options: Bundle? = null) {
    startActivity(intent, options)
    transition?.let { overridePendingTransition(it.first, it.second) }
    if (close) {
        finish()
    }
}

// region { JSON }
@Throws(Exception::class)
fun JSONObject.toStringValue(name: String, default: String = ""): String {
    return when {
        this.has(name) && !this.isNull(name) -> this.getString(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toStringValue(firstName: String, secondName: String, default: String = ""): String {
    return if (this.has(firstName) && !this.isNull(firstName)) {
        this.getString(firstName)
    } else if (this.has(secondName) && !this.isNull(secondName)) {
        this.getString(secondName)
    } else {
        default
    }
}

@Throws(Exception::class)
fun JSONObject.toIntValue(name: String, default: Int = 0): Int {
    return when {
        this.has(name) && !this.isNull(name) -> this.getInt(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toFloatValue(name: String, default: Float = 0f): Float {
    return when {
        this.has(name) && !this.isNull(name) -> this.getDouble(name).toFloat()
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toLongValue(name: String, default: Long = 0L): Long {
    return when {
        this.has(name) && !this.isNull(name) -> this.getLong(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toDoubleValue(name: String, default: Double = 0.0): Double {
    return when {
        this.has(name) && !this.isNull(name) -> this.getDouble(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toBooleanValue(name: String, default: Boolean = false): Boolean {
    return when {
        this.has(name) && !this.isNull(name) -> {
            val innerValue = this.getString(name)
            when (innerValue.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> default
            }
        }
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONObjectValue(name: String): JSONObject? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONObject(name)
        else -> null
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONArrayValue(name: String): JSONArray? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONArray(name)
        else -> null
    }
}

fun JSONArray.isEmpty(): Boolean {
    return this.length() == 0
}

fun JSONArray.until(loop: (json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilAny(loop: (i: Any) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.get(i))
    }
}

fun JSONArray.until(feasibility: (feasible: Boolean) -> Unit, loop: (json: JSONObject) -> Unit) {
    // size
    val length = this.length()
    // call -> feasibility
    feasibility(length > 0)
    // call -> loop
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilWithIndex(loop: (index: Int, json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(i, this.getJSONObject(i))
    }
}
// endregion

fun Activity.openFortResult(intent: Intent, requestCode: Int, transition: Pair<Int, Int>? = null, options: Bundle? = null) {
    startActivityForResult(intent, requestCode, options)
    transition?.let { overridePendingTransition(it.first, it.second) }
}
// endregion


// region { activity - permission }
fun Activity.permissionGranted(permissionName: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23) {
        ContextCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Activity.permissionGranted(permissionNames: Array<out String>): Boolean {
    if (Build.VERSION.SDK_INT >= 23) {
        for (permissionName in permissionNames) {
            if (ContextCompat.checkSelfPermission(this, permissionName) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    } else {
        return true
    }
}

fun Activity.permissionRequest(permissionName: String, permissionRequestCode: Int) {
    if (Build.VERSION.SDK_INT >= 23) {
        ActivityCompat.requestPermissions(this, Array(1) { permissionName }, permissionRequestCode)
    }
}

fun Activity.permissionRequest(permissionNames: Array<out String>, permissionRequestCode: Int) {
    if (Build.VERSION.SDK_INT >= 23) {
        ActivityCompat.requestPermissions(this, permissionNames, permissionRequestCode)
    }
}

val Activity.permissionDrawOverlays: Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
// endregion


// region { context }
val Context.hostPackageName: String
    get() = this.applicationInfo.packageName

val Context.hostAppName: String
    get() = this.packageManager.getApplicationLabel(this.applicationInfo).toString()

val Context.hostAppVersionName: String
    get() = this.packageManager.getPackageInfo(this.packageName, 0).versionName

val Context.hostAppVersionCode: String
    get() {
        return try {
            if (packageManager != null && packageName != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    (packageManager!!.getPackageInfo(packageName!!, 0).versionCode).toString()
                } else {
                    packageManager!!.getPackageInfo(packageName!!, 0).longVersionCode.toString()
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

val Context.isScreenOn: Boolean
    get() {
        val powerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }

val Context.connectivityManager: ConnectivityManager
    get() = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.isAlwaysFinishActivitiesEnabled: Boolean
    get() {
        return Settings.Global.getInt(this.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0) != 0
    }
// endregion


// region { TypedArray }
fun TypedArray.use(block: TypedArray.() -> Unit) {
    try {
        block()
    } finally {
        this.recycle()
    }
}
// endregion


// region { String }
val String.toHtml: Spanned
    get() {
        return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(this)
        } else {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        }
    }
// endregion


// region { boolean/int }
fun Int.toBoolean(): Boolean = (this == 1)

fun Boolean.toInt(): Int = if (this) 1 else 0

fun Int.toLocale(inLocale: Locale = Locale.KOREA): String {
    return try {
        NumberFormat.getInstance(inLocale).format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Int.toLocaleOver(over: Int): String {
    return when {
        (over > 0 && this >= over) -> over.toLocale().plus("+")
        else -> this.toLocale()
    }
}
// endregion


// region { DP / Pixel }
val Int.toPX: Float get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Float.toPX: Float get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Int.toDP: Float get() = (this / Resources.getSystem().displayMetrics.density)
val Float.toDP: Float get() = (this / Resources.getSystem().displayMetrics.density)
fun Float.cube(): Float = this * this * this
fun Int.cube(): Int = this * this * this

fun Float.sqr(): Float = this * this
fun Int.sqr(): Int = this * this
// endregion


// region { meta-data }
fun Context.metaDataBundle(): Bundle? {
    return try {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData
    } catch (e: Exception) {
        null
    }
}


fun Context.metaDataValue(keyName: String): String? {
    return try {
        val bundle = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData
        val bundleValue = bundle[keyName]?.toString() ?: ""
        if (bundleValue.isEmpty()) null else bundleValue
    } catch (e: Exception) {
        null
    }
}


fun Context.metaDataVerify(keyName: String, callback: (value: String) -> Unit = {}) {
    metaDataValue(keyName)?.let {
        callback(it)
    } ?: run {
        throw Exception("AndroidManifest: $keyName is null or empty")
    }
}
// endregion


fun ContentResolver.registerObserver(uri: Uri, observer: (selfChange: Boolean) -> Unit): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}

val Context.layoutInflater: LayoutInflater get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}