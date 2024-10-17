package com.znhst.xtzb.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

@Composable
fun calculateDpValue(radius: Dp): Int {
    // 使用 LocalDensity 来获取 dp 到 px 的转换率
    val density = LocalDensity.current

    // 将 Dp 转换为 px 并计算
    return with(density) {
        val radiusPx = radius.toPx() // 将 dp 转换为 px
        val result = radiusPx / 1.41  // 进行除法运算
        result.toInt()  // 取整
    }
}

fun saveBitmapToPrivateDir(context: Context, bitmap: Bitmap, fileName: String): String? {
    // 创建文件
    val file = File(context.filesDir, "$fileName.png")

    return try {
        // 打开输出流，将Bitmap写入文件
        val outputStream = FileOutputStream(file)
        bitmap.compress(CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Toast.makeText(context, "截图保存成功! ${file.absolutePath}", Toast.LENGTH_SHORT)
            .show()
        file.absolutePath // 返回文件路径
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "截图保存失败! ${e.message}", Toast.LENGTH_SHORT)
            .show()
        null // 返回null表示保存失败
    }
}

fun saveBitmapToPublicDir(context: Context, bitmap: Bitmap, fileName: String): String? {
    // 获取公有的Pictures目录
    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    // 创建文件
    val file = File(picturesDir, "$fileName.png")
    val file2 = File(context.filesDir, "$fileName.png")

    return try {
        // 打开输出流，将Bitmap写入文件
        val outputStream = FileOutputStream(file2)
        bitmap.compress(CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Toast.makeText(context, "截图保存成功! ${file2.absolutePath}", Toast.LENGTH_SHORT)
            .show()
        file.absolutePath // 返回文件路径
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "截图保存失败! ${e.message}", Toast.LENGTH_SHORT)
            .show()
        null // 返回null表示保存失败
    }
}

fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return uri?.let {
        var outputStream: OutputStream? = null
        try {
            outputStream = resolver.openOutputStream(it)
            if (outputStream != null) {
                bitmap.compress(CompressFormat.JPEG, 100, outputStream)
            }
            outputStream?.flush()
            Toast.makeText(context, "截图保存成功! $fileName", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "截图保存失败! ${e.message}", Toast.LENGTH_SHORT)
                .show()
            null
        } finally {
            outputStream?.close()
        }
        it
    }
}

fun addVideoToGalleryFromPrivateDir(context: Context, fileName: String): Uri? {
    // 获取私有文件目录的文件对象
    val videoFile = File(context.filesDir, "/records/$fileName")
    val videoTemp = File(context.filesDir, "/records/${fileName}_temp")
//    val tempFile = File(context.filesDir, "/records/ZB_Camera_BC5617671_1729179452340.mp4")

    val realFile = if(videoFile.exists()) videoFile else videoFile

    // 检查文件是否存在
    if (!realFile.exists()) {
        Toast.makeText(context, "文件不存在: ${realFile.absolutePath}", Toast.LENGTH_SHORT).show()
        Log.d("ez record not find file", realFile.absolutePath)
        return null
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, fileName)  // 视频文件名
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")  // 视频MIME类型
        put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)  // 公共视频目录
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        try {
            val outputStream = resolver.openOutputStream(uri)
            val inputStream = FileInputStream(realFile)

            // 将私有目录中的视频写入到 MediaStore
            inputStream.copyTo(outputStream!!)
            outputStream.close()
            inputStream.close()

            Toast.makeText(context, "录屏已添加到相册: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "录屏保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    return uri
}