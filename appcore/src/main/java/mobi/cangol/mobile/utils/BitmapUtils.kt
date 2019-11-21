/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils

import android.app.Activity
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import mobi.cangol.mobile.logging.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.floor

/**
 * Bitmap 工具类
 *
 * @author Cangol
 */
object BitmapUtils {

    /**
     * 添加水印文字
     *
     * @param src
     * @param left
     * @param top
     * @param content
     * @param alpha
     * @return Bitmap
     */
    @JvmStatic
    fun addWatermark(src: Bitmap, left: Float, top: Float, content: String, color: Int,
                     size: Int, alpha: Int): Bitmap {
        val w = src.width
        val h = src.height
        val dst = Bitmap.createBitmap(w, h, Config.ARGB_8888)
        val canvas = Canvas(dst)
        val p = Paint()
        p.isAntiAlias = true
        canvas.drawBitmap(src, 0f, 0f, p)
        val font = Typeface.create("sans", Typeface.BOLD)
        p.isAntiAlias = true
        p.textAlign = Paint.Align.CENTER
        p.color = color
        p.typeface = font
        p.textSize = size.toFloat()
        p.alpha = alpha * 255 / 100
        canvas.drawText(content, left, top, p)
        canvas.save()
        canvas.restore()
        return dst
    }

    /**
     * 添加水印图片
     *
     * @param src
     * @param left
     * @param top
     * @param img
     * @param alpha
     * @return
     */
    @JvmStatic
    fun addWatermark(src: Bitmap, left: Float, top: Float, img: Bitmap, alpha: Int): Bitmap {
        val w = src.width
        val h = src.height
        val dst = Bitmap.createBitmap(w, h, Config.ARGB_8888)
        val canvas = Canvas(dst)
        val p = Paint()
        p.isAntiAlias = true
        canvas.drawBitmap(src, 0f, 0f, p)
        p.alpha = alpha * 255 / 100
        canvas.drawBitmap(img, left, top, p)
        canvas.save()
        canvas.restore()
        return dst
    }

    /**
     * 在src上按progress绘制pro
     *
     * @param src
     * @param pro
     * @param progress
     * @return
     */
    @JvmStatic
    fun addProgress(src: Bitmap, pro: Bitmap, progress: Int): Bitmap {
        val w = pro.width
        val h = pro.height
        val dst = Bitmap.createBitmap(w, h, Config.ARGB_8888)
        val canvas = Canvas(dst)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(pro, 0f, pro.height * (100 - progress) / 100.0f, null)
        canvas.save()
        canvas.restore()
        return dst
    }

    /**
     * 设置图片透明度
     *
     * @param src
     * @param num
     * @return
     */
    @JvmStatic
    fun setAlpha(src: Bitmap, num: Int): Bitmap {
        var sourceImg = src
        var number = num
        val argb = IntArray(sourceImg.width * sourceImg.height)
        sourceImg.getPixels(argb, 0, sourceImg.width, 0, 0, sourceImg.width, sourceImg.height)
        number = number * 255 / 100

        for (i in argb.indices) {
            argb[i] = number shl 24 or (argb[i] and 0x00FFFFFF)
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.width, sourceImg.height, Config.ARGB_8888)

        return sourceImg
    }

    /**
     * 转为图片
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    @JvmStatic
    fun roundedCornerBitmap(bitmap: Bitmap, roundPx: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap
                .height, Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0xbdbdbe
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /**
     * 原图添加倒影
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun createReflectionImageWithOrigin(bitmap: Bitmap): Bitmap {
        val reflectionGap = 4
        val width = bitmap.width
        val height = bitmap.height

        val matrix = Matrix()
        matrix.preScale(1f, -1f)

        val reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false)

        val bitmapWithReflection = Bitmap.createBitmap(width,
                height + height / 2, Config.ARGB_8888)

        val canvas = Canvas(bitmapWithReflection)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val deafalutPaint = Paint()
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (height + reflectionGap).toFloat(),
                deafalutPaint)
        canvas.drawBitmap(reflectionImage, 0f, (height + reflectionGap).toFloat(), null)
        val paint = Paint()
        val shader = LinearGradient(0f, bitmap.height.toFloat(), 0f,
                (bitmapWithReflection.height + reflectionGap).toFloat(), 0x70ffffff,
                0x00ffffff, TileMode.CLAMP)
        paint.shader = shader
        // Set the Transfer threadType to be porter duff and destination in
        paint.xfermode = PorterDuffXfermode(Mode.DST_IN)
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (bitmapWithReflection.height + reflectionGap).toFloat(), paint)
        return bitmapWithReflection
    }

    /**
     * 创建倒影
     *
     * @param originalImage
     * @return
     */
    @JvmStatic
    fun createReflectedImage(originalImage: Bitmap): Bitmap {
        val reflectionGap = 4  //倒影和原图片间的距离
        val width = originalImage.width
        val height = originalImage.height

        val matrix = Matrix()
        matrix.preScale(1f, -1f)

        //倒影部分
        val reflectionImage = Bitmap.createBitmap(originalImage,
                0, height / 2, width, height / 2, matrix, false)
        //要返回的倒影图片
        val bitmapWithReflection = Bitmap.createBitmap(width,
                height + height / 2, Config.ARGB_8888)

        val canvas = Canvas(bitmapWithReflection)
        //画原来的图片
        canvas.drawBitmap(originalImage, 0f, 0f, null)

        val defaultPaint = Paint()
        //倒影和原图片间的距离
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (height + reflectionGap).toFloat(), defaultPaint)
        //画倒影部分
        canvas.drawBitmap(reflectionImage, 0f, (height + reflectionGap).toFloat(), null)

        val paint = Paint()
        val shader = LinearGradient(0f, originalImage.height.toFloat(),
                0f, (bitmapWithReflection.height + reflectionGap).toFloat(),
                0x70ffffff, 0x00ffffff,
                TileMode.MIRROR)
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(Mode.DST_IN)
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (bitmapWithReflection.height + reflectionGap).toFloat(), paint)
        return bitmapWithReflection
    }

    /**
     * 获取宽度
     *
     * @param filepath
     * @return
     */
    @JvmStatic
    fun getWidth(filepath: String): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        return options.outWidth
    }

    /**
     * 获取高度
     *
     * @param filepath
     * @return
     */
    @JvmStatic
    fun getHeight(filepath: String): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        return options.outHeight
    }

    /**
     * 获取宽度,高度
     *
     * @param filepath
     * @return
     */
    @JvmStatic
    fun getSize(filepath: String): IntArray {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        return intArrayOf(options.outWidth, options.outHeight)
    }

    /**
     * 缩放图片到新大小(无损)
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    @JvmStatic
    fun scale(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    /**
     * 缩放图片按最大宽高(无损)
     *
     * @param filepath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    @JvmStatic
    fun scaleFile(filepath: String, maxWidth: Int, maxHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        options.inJustDecodeBounds = false
        options.inSampleSize = computeSampleSize(options, -1, maxWidth * maxHeight)
        return BitmapFactory.decodeFile(filepath, options)
    }

    /**
     * 压缩图片按指定精度
     *
     * @param image   图片
     * @param quality 精度 推荐大于65
     * @return
     */
    @JvmStatic
    fun compressImage(image: Bitmap, quality: Int): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size)
    }

    /**
     * 压缩图片 按最大图片存储大小
     *
     * @param image
     * @param maxSize 最大图片存储大小 (单位b)
     * @return
     */
    @JvmStatic
    fun compressImage(image: Bitmap, maxSize: Long): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var quality = 100
        while (baos.toByteArray().size > maxSize) {
            baos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            quality -= 10
            if (quality <= 10) {
                break
            }
        }
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size)
    }

    /**
     * 重置图片文件大小
     *
     * @param filepath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    @JvmStatic
    fun resizeBitmap(filepath: String, maxWidth: Int, maxHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        val originWidth = options.outWidth
        val originHeight = options.outHeight
        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            //do nothings
        } else {
            val wb = 1.0f * originWidth / maxWidth
            val hb = 1.0f * originHeight / maxHeight
            if (originWidth > maxWidth || originHeight > maxHeight) {
                if (wb >= hb) {
                    val i = floor(originWidth * 1.0 / maxWidth).toInt()
                    options.inSampleSize = i

                } else {
                    val i = floor(originHeight * 1.0 / maxHeight).toInt()
                    options.inSampleSize = i
                }
            }
        }
        return BitmapFactory.decodeFile(filepath, options)
    }

    /**
     * 重置图片大小
     *
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    @JvmStatic
    fun resizeBitmap(src: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var bitmap = src
        val originWidth = bitmap.width
        val originHeight = bitmap.height
        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            return bitmap
        }
        val width: Int
        val height: Int
        val wb = 1.0f * originWidth / maxWidth
        val hb = 1.0f * originHeight / maxHeight
        // 若图片过宽, 则保持长宽比缩放图片
        if (originWidth > maxWidth || originHeight > maxHeight) {
            if (wb > hb) {
                width = maxWidth
                val i = originWidth * 1.0 / maxWidth
                height = floor(originHeight / i).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            } else {
                height = maxHeight
                val i = originHeight * 1.0 / maxHeight
                width = floor(originWidth / i).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            }
        }
        return bitmap
    }

    /**
     * 计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    @JvmStatic
    fun computeSampleSize(options: BitmapFactory.Options,
                          minSideLength: Int, maxNumOfPixels: Int): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels)

        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }

        return roundedSize
    }

    /**
     * 计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    @JvmStatic
    fun computeInitialSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()

        val lowerBound = if (maxNumOfPixels == -1)
            1
        else
            Math.ceil(Math.sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == -1)
            128
        else
            Math.min(Math.floor(w / minSideLength),
                    Math.floor(h / minSideLength)).toInt()

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound
        }

        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }

    /**
     * 根据数字，创建一张带有数字的图片
     *
     * @param number   数字
     * @param textSize 字体大小
     * @param bitmap   图片
     * @return
     */
    @JvmStatic
    fun createAlbumIcon(number: Int, textSize: Int, bitmap: Bitmap): Bitmap {
        if (number == 0) {
            return bitmap
        }
        val len = getNumberLength(number)
        val x = len * textSize

        val width = bitmap.width
        val height = bitmap.height
        val contactIcon = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        val canvas = Canvas(contactIcon)

        val iconPaint = Paint()
        iconPaint.isDither = true
        iconPaint.isFilterBitmap = true
        val src1 = Rect(0, 0, width, height)
        val dst1 = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, src1, dst1, iconPaint)
        val bgPaint = Paint()
        bgPaint.color = Color.RED
        canvas.drawRect((width - x / 2 - 6).toFloat(), 0f, (width + x / 2 + 6).toFloat(), textSize.toFloat(), bgPaint)
        val countPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DEV_KERN_TEXT_FLAG)
        countPaint.color = Color.WHITE
        countPaint.textSize = textSize.toFloat()
        countPaint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(number.toString(), (width - x / 2 - 5).toFloat(), (textSize - 2).toFloat(), countPaint)
        return contactIcon
    }

    /**
     * 统计数字位数
     */
    @JvmStatic
    fun getNumberLength(number: Int): Int {
        var num = number
        var count = 0
        while (num != 0) {
            num /= 10
            count++
        }
        return count
    }

    /**
     * bitmap To File
     *
     * @param bm   图片
     * @param path 路径
     */
    @JvmStatic
    fun bitmap2File(bm: Bitmap, path: String) {
        val file = File(path)
        try {
            val result = file.createNewFile()
            if (result) {
                val out = FileOutputStream(file)
                if (bm.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush()
                    out.close()
                }
            }
        } catch (e: IOException) {
            Log.d(e.message)
        }

    }

    /**
     * Bitmap to Bytes
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap?): ByteArray {
        if (null == bitmap) {
            return ByteArray(0)
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * bitmap to Drawable
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun bitmap2Drawable(bitmap: Bitmap): Drawable {
        return BitmapDrawable(bitmap)
    }

    /**
     * drawable to Bitmap
     *
     * @param drawble
     * @return
     */
    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        return if (null != drawable) {
            (drawable as BitmapDrawable).bitmap
        } else null
    }

    /**
     * Bytes to Bitmap
     *
     * @param b
     * @return
     */
    @JvmStatic
    fun bytes2Bitmap(b: ByteArray): Bitmap? {
        return if (b.isNotEmpty()) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            null
        }
    }

    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    @JvmStatic
    fun screenShot(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val b1 = view.drawingCache
        // 获取状态栏高度
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        // 获取屏幕长和高
        val displayMetrics = activity.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return b
    }

    /**
     * Bitmap转成String
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()// outputstream
        bitmap.compress(CompressFormat.JPEG, 100, baos)
        val bmByte = baos.toByteArray()// 转为byte数组
        try {
            baos.close()
        } catch (e: IOException) {
            Log.d(e.message)
        } finally {
            bitmap.recycle()
        }
        return Base64.encodeToString(bmByte, Base64.DEFAULT)
    }

    /**
     * 图片文件转string
     *
     * @param imagePath
     * @return
     */
    @JvmStatic
    fun imageFileToString(imagePath: String): String {
        var bitmap: Bitmap? = BitmapFactory.decodeFile(imagePath)
        val baos = ByteArrayOutputStream()
        bitmap!!.compress(CompressFormat.JPEG, 100, baos)
        try {
            baos.close()
        } catch (e: IOException) {
            Log.d(e.message)
        } finally {
            bitmap.recycle()
        }
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    /**
     * String转成Bitmap
     *
     * @param string
     */
    @JvmStatic
    fun stringToBitmap(string: String): Bitmap? {
        var bitmap: Bitmap?
        val bitmapArray: ByteArray
        return try {
            bitmapArray = Base64.decode(string, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
            bitmap
        } catch (e: Exception) {
            Log.d(e.message)
            null
        }

    }
}
