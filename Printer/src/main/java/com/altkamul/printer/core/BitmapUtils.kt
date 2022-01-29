package com.altkamul.printer.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

/**
 * Created by sotra@altakamul.tr on 3/2/2021.
 */
object BitmapUtils {

      fun getBitmap(filePath: String?): Bitmap {
        return BitmapFactory.decodeFile(filePath)
    }

      fun getBitmap(context : Context , drawable: Int): Bitmap {
        return drawableToBitmap(ContextCompat.getDrawable(context, drawable))
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}