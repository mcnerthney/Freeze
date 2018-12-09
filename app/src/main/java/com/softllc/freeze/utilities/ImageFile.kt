package com.softllc.freeze.utilities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.fragment.findNavController
import com.softllc.freeze.FreezeFragmentDirections
import java.io.*
import java.lang.Exception
import java.util.*

class ImageFile(private var context: Context) {

    public fun upload(photoId: String, inPath: String): String {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val mypath = File(directory, photoId)
        Log.d("djm", "inpath $inPath")

            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(inPath))

                try {

                   // val exifInterface = ExifInterface(inputStream)
                   // Log.d("djm", "exit rotate ${exifInterface.rotationDegrees}")
                }
                catch (ex: Exception){

                }

                val fos = FileOutputStream(mypath)
                copy(inputStream, fos)
                fos.close()


            } catch (e: IOException) {
                e.printStackTrace()
            }

        return mypath.absolutePath
    }


    private fun copy(instream: InputStream, out: OutputStream) {
        try {

            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len = instream.read(buf)
                while (len > 0) {
                    out.write(buf, 0, len)
                    len = instream.read(buf)
                }
            } finally {
                out.close()
            }
        } finally {
            instream.close()
        }
    }


}