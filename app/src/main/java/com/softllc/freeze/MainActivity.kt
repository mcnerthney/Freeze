/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softllc.freeze

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.R.attr.uri
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.softllc.freeze.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import java.io.IOException
import android.content.ContextWrapper
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProviders
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.runOnIoThread
import java.io.File
import java.io.FileOutputStream
import java.util.Collections.rotate




class MainActivity : AppCompatActivity() {

    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var photoViewModel: PhotoViewModel

    val REQUEST_GET_SINGLE_FILE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = InjectorUtils.providePhotoViewModelFactory(this, "new")
        photoViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoViewModel::class.java)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        // drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.freeze_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_photo -> launchGallery()
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("image/*");
        //intent.putExtra("crop", true);
        //intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
        drawerLayout.closeDrawers()
    }


    private fun loadBitmap ( data: Intent) {
        runOnIoThread {
            val selectedImage = data.data

            // method 1
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                val rotate = getOrientation(this, selectedImage)
                photoViewModel.setImageURI(saveToInternalStorage(bitmap))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun getOrientation(context: Context, photoUri: Uri): Int {
        /* it's on the external media. */
        val cursor = context.contentResolver.query(
            photoUri,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION), null, null, null
        )

        if (cursor!!.count !== 1) {
            return -1
        }

        cursor!!.moveToFirst()
        return cursor.getInt(0)
    }
    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotate(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> return rotate(bitmap, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> return rotate(bitmap, 270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap, true, false)

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap, false, true)

            else -> return bitmap
        }
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(applicationContext)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return mypath.absolutePath
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == REQUEST_GET_SINGLE_FILE) {
            if (resultCode == RESULT_OK) {
                if (intent != null && data != null) {

                    loadBitmap(data)

                    //val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    //)// Which columns to return
                    // Which rows to return (all rows)

                    //Log.i("Images Count", " count=" + cur!!.count)
                    //val imgPath = StorageUtil.imageGetPath(getActivity(), selectedImage);
                    //Log.d("TESTPATH", String.valueOf(imgPath));
                    //Picasso.with(context).load(selectedImage).into(imgSelectAvatar);
                    //filePath = imgPath;
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun useImage(uri: Uri?) {

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}