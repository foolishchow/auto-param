package me.foolishchow.android.plugin.navigation.extensions

import java.io.File

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 3:17 PM
 */

fun File.deleteSelf() {
    deleteFile(this)
}

/**
 * 先根遍历序递归删除文件夹
 *
 * @param dirFile 要被删除的文件或者目录
 * @return 删除成功返回true, 否则返回false
 */
fun deleteFile(dirFile: File): Boolean {
    // 如果dir对应的文件不存在，则退出
    if (!dirFile.exists()) {
        return false
    }

    if (dirFile.isFile) {
        return dirFile.delete()
    } else {
        dirFile.listFiles()?.forEach { file ->
            deleteFile(file)
        }
    }
    return dirFile.delete()
}
