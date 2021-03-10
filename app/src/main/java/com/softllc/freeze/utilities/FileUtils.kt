package com.softllc.freeze.utilities

import android.content.Context
import android.content.ContextWrapper

import android.net.Uri
import java.io.*

class FileUtils(private var context: Context) {

    fun upload(photoId: String, inPath: String): String {

        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val mypath = File(directory, photoId)

        try {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(inPath))
            val fos = FileOutputStream(mypath)
            copy(inputStream!!, fos)
            fos.close()


        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mypath.absolutePath
    }


    private fun copy(instream: InputStream, out: OutputStream) {
        try {
            // Transfer bytes from in to out
            val buf = ByteArray(4096)
            var len = instream.read(buf)
            while (len > 0) {
                out.write(buf, 0, len)
                len = instream.read(buf)
            }

        } finally {
            out.close()
            instream.close()
        }
    }


}