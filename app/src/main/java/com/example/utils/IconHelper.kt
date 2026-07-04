package com.example.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

data class AppInfo(
    val label: String,
    val packageName: String,
    val launcherActivityClassName: String,
    val isSystemApp: Boolean,
    val firstInstallTime: Long
)

object IconHelper {

    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(mainIntent, 0)
        }

        val appList = mutableListOf<AppInfo>()
        val packageInfos = pm.getInstalledPackages(0).associateBy { it.packageName }

        for (resolveInfo in resolvedInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            val launcherActivityName = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(pm).toString()
            
            val appInfo = resolveInfo.activityInfo.applicationInfo
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            
            val installTime = packageInfos[packageName]?.firstInstallTime ?: System.currentTimeMillis()

            // Exclude our own app from the customizer list so the user doesn't accidentally customize our launcher icon inside the customizer (to prevent confusion).
            if (packageName != context.packageName) {
                appList.add(
                    AppInfo(
                        label = label,
                        packageName = packageName,
                        launcherActivityClassName = launcherActivityName,
                        isSystemApp = isSystem,
                        firstInstallTime = installTime
                    )
                )
            }
        }
        return appList.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
    }

    fun getAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }
        
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 192
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 192
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun createCircularBitmap(src: Bitmap): Bitmap {
        val size = Math.min(src.width, src.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
        }
        val rect = Rect(0, 0, size, size)
        val r = size / 2f
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(r, r, r, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        
        val srcRect = Rect(
            (src.width - size) / 2,
            (src.height - size) / 2,
            (src.width + size) / 2,
            (src.height + size) / 2
        )
        canvas.drawBitmap(src, srcRect, rect, paint)
        return output
    }

    fun createRoundedCornerBitmap(src: Bitmap, cornerRadiusPercent: Float): Bitmap {
        val size = Math.min(src.width, src.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
        }
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        val radius = size * (cornerRadiusPercent / 100f)
        
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        
        val srcRect = Rect(
            (src.width - size) / 2,
            (src.height - size) / 2,
            (src.width + size) / 2,
            (src.height + size) / 2
        )
        canvas.drawBitmap(src, srcRect, rect, paint)
        return output
    }

    fun applyFiltersToBitmap(
        src: Bitmap,
        brightness: Float,      // -100 to 100 (0 is default)
        contrast: Float,        // 0.5 to 2.0 (1.0 is default)
        saturation: Float,      // 0.0 to 2.0 (1.0 is default)
        opacity: Float,         // 0.0 to 1.0 (1.0 is default)
        hue: Float,             // -180 to 180 (0 is default)
        rotation: Float,        // 0 to 360 (0 is default)
        zoom: Float,            // 1.0 to 3.0 (1.0 is default)
        shapeType: String,      // "none", "circle", "rounded"
        cornerRadiusPercent: Float,
        backgroundColor: Int?,
        gradientColors: List<Int>?,
        borderWidthPercent: Float, // border width relative to size
        borderColor: Int?
    ): Bitmap {
        val size = Math.max(src.width, src.height).coerceAtLeast(192)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // Draw background solid/gradient if any
        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        if (backgroundColor != null) {
            val paintBg = Paint().apply {
                color = backgroundColor
                isAntiAlias = true
            }
            canvas.drawRect(rect, paintBg)
        } else if (gradientColors != null && gradientColors.size >= 2) {
            val shader = LinearGradient(
                0f, 0f, size.toFloat(), size.toFloat(),
                gradientColors.toIntArray(), null, Shader.TileMode.CLAMP
            )
            val paintBg = Paint().apply {
                this.shader = shader
                isAntiAlias = true
            }
            canvas.drawRect(rect, paintBg)
        }

        // Apply matrix for filters
        val cm = ColorMatrix()
        
        // 1. Brightness & Contrast
        // Contrast is scale, Brightness is translation offset
        val brightnessOffset = brightness * 2.55f // map -100..100 to -255..255
        val t = (1.0f - contrast) * 128f + brightnessOffset
        val contrastMatrix = floatArrayOf(
            contrast, 0f, 0f, 0f, t,
            0f, contrast, 0f, 0f, t,
            0f, 0f, contrast, 0f, t,
            0f, 0f, 0f, opacity, 0f
        )
        cm.set(ColorMatrix(contrastMatrix))

        // 2. Saturation
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)
        cm.postConcat(satMatrix)

        // 3. Hue rotation
        if (hue != 0f) {
            val hueMatrix = ColorMatrix()
            // Hue rotation can be set using standard rotates on R(0), G(1), B(2) channels
            hueMatrix.setRotate(0, hue)
            hueMatrix.setRotate(1, hue)
            hueMatrix.setRotate(2, hue)
            cm.postConcat(hueMatrix)
        }

        val paintImage = Paint().apply {
            colorFilter = ColorMatrixColorFilter(cm)
            isAntiAlias = true
        }

        // Draw the image onto background with transform (rotation/zoom)
        canvas.save()
        canvas.translate(size / 2f, size / 2f)
        canvas.rotate(rotation)
        canvas.scale(zoom, zoom)
        
        val srcRect = Rect(0, 0, src.width, src.height)
        val dstRect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
        canvas.drawBitmap(src, srcRect, dstRect, paintImage)
        canvas.restore()

        // Apply cropping (Circle or Rounded) if requested
        var croppedOutput = output
        if (shapeType == "circle") {
            croppedOutput = createCircularBitmap(output)
        } else if (shapeType == "rounded") {
            croppedOutput = createRoundedCornerBitmap(output, cornerRadiusPercent)
        }

        // Draw Border
        if (borderColor != null && borderWidthPercent > 0) {
            val finalWithBorder = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val borderCanvas = Canvas(finalWithBorder)
            borderCanvas.drawBitmap(croppedOutput, 0f, 0f, null)

            val borderPx = size * (borderWidthPercent / 100f)
            val borderPaint = Paint().apply {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = borderPx
                isAntiAlias = true
            }

            if (shapeType == "circle") {
                val radius = (size - borderPx) / 2f
                borderCanvas.drawCircle(size / 2f, size / 2f, radius, borderPaint)
            } else if (shapeType == "rounded") {
                val r = size * (cornerRadiusPercent / 100f)
                val borderRectF = RectF(borderPx / 2f, borderPx / 2f, size - borderPx / 2f, size - borderPx / 2f)
                borderCanvas.drawRoundRect(borderRectF, r, r, borderPaint)
            } else {
                borderCanvas.drawRect(borderPx / 2f, borderPx / 2f, size - borderPx / 2f, size - borderPx / 2f, borderPaint)
            }
            return finalWithBorder
        }

        return croppedOutput
    }

    fun pinShortcut(
        context: Context,
        customName: String,
        packageName: String,
        launcherActivityClassName: String,
        iconBitmap: Bitmap
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return false
            if (shortcutManager.isRequestPinShortcutSupported) {
                val launchIntent = Intent().apply {
                    setClassName(packageName, launcherActivityClassName)
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                // Generates a unique shortcut ID based on package and current timestamp
                val shortcutId = "shortcut_${packageName}_${System.currentTimeMillis()}"

                val pinShortcutInfo = ShortcutInfo.Builder(context, shortcutId)
                    .setIcon(Icon.createWithBitmap(iconBitmap))
                    .setShortLabel(customName)
                    .setIntent(launchIntent)
                    .build()

                return shortcutManager.requestPinShortcut(pinShortcutInfo, null)
            }
        }
        return false
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
}
