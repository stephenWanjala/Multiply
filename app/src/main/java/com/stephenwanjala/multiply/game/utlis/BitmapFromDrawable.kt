package com.stephenwanjala.multiply.game.utlis

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberBitmapFromDrawable(@DrawableRes resId: Int): ImageBitmap {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, resId) ?: return ImageBitmap(1, 1)

    return if (drawable is BitmapDrawable) {
        drawable.bitmap.asImageBitmap()
    } else {
        // Convert VectorDrawable to Bitmap
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap.asImageBitmap()
    }
}
