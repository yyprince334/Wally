package com.prince.wally.util

import android.app.Activity
import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.prince.wally.R
import com.prince.wally.model.remote.response.CommonPic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.reflect.KFunction3

inline fun <reified T : Activity> Activity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
}

fun Activity.getImageUri(pic: CommonPic?): Uri {
    val bitmap = getBitmap(pic)
    val bytes = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        contentResolver, bitmap, "Title", null
    )
    return Uri.parse(path)
}

fun Activity.getBitmap(pic: CommonPic?): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        bitmap = Glide.with(this)
            .asBitmap()
            .load(pic?.imageURL)
            .submit()
            .get()
    } catch (e: IOException) {
    }
    return bitmap
}

fun Context.isNetworkAvailable() = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

fun Context.loadImage(url: String, view: ImageView, progressBar: ProgressBar?, color: Int) =
    Glide.with(this).load(url)
        .placeholder(color)
        .thumbnail(0.1F)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(view)

fun Context.share(text: String?) {
    val intent = Intent().apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    }
    try {
        startActivity(Intent.createChooser(intent, getString(R.string.choose_share)))
    } catch (e: ActivityNotFoundException) {
        shortToast(getString(R.string.cant_share))
    }
}

fun Context.shortToast(message: String) = makeText(this, message, LENGTH_SHORT).show()

fun Activity.saveImage(url: String, progressBar: ProgressBar) = Glide.with(this)
    .asBitmap()
    .load(url)
    .into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        bitmap.saveImageQ(this@saveImage, progressBar)
                    } else {
                        bitmap.saveImage(this@saveImage, progressBar)
                    }
                }
            }
        }

        override fun onLoadStarted(placeholder: Drawable?) {
            super.onLoadStarted(placeholder)
            progressBar.visible()
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    })

private fun Bitmap.saveImage(activity: Activity, progressBar: ProgressBar) {
    val root = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
    val myDir = File("$root/Wallpapers")
    myDir.mkdirs()
    val randomInt = (0..10000).random()
    val fileName = "Image-$randomInt.jpg"
    val file = File(myDir, fileName)
    if (file.exists()) file.delete()
    try {
        val outputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: java.lang.Exception) {
    }
    MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null) { _, _ ->
    }
    activity.runOnUiThread {
        progressBar.gone()
        activity.shortToast(activity.getString(R.string.down_complete))
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(activity: Activity, progressBar: ProgressBar) {
    val values = ContentValues().apply {
        put(MIME_TYPE, "image/png")
        put(DATE_ADDED, System.currentTimeMillis() / 1000)
        put(DATE_TAKEN, System.currentTimeMillis())
        put(RELATIVE_PATH, "Pictures/" + "Wallpapers")
        put(IS_PENDING, true)
    }

    val uri: Uri? = activity.contentResolver.insert(EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        saveImageToStream(activity.contentResolver.openOutputStream(uri))
        values.put(IS_PENDING, false)
        activity.contentResolver.update(uri, values, null, null)
    }
    activity.runOnUiThread {
        progressBar.gone()
        activity.shortToast(activity.getString(R.string.down_complete))
    }
}

private fun Bitmap.saveImageToStream(outputStream: OutputStream?) {
    outputStream?.let {
        try {
            compress(Bitmap.CompressFormat.PNG, 100, it)
            it.close()
        } catch (e: Exception) {
        }
    }
}

fun View.visible() { visibility = VISIBLE }

fun View.gone() { visibility = GONE }

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: KFunction3<Context, Int, ViewGroup, View>
) = lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }