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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.softllc.freeze.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.runOnIoThread
import java.io.*


class MainActivity : AppCompatActivity() {

    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var photoViewModel: PhotoViewModel

    val REQUEST_GET_SINGLE_FILE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            //val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            //keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }


        val factory = InjectorUtils.providePhotoViewModelFactory(this, "new")
        photoViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoViewModel::class.java)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
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
            drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }


        FreezeApp.locked.observe(this,  Observer { locked ->
            when (locked ) {
                true -> appbar.visibility = GONE
                false -> appbar.visibility = VISIBLE
            }
        })
    }
    private fun launchCamera () {
        val camIntent = Intent("android.media.action.IMAGE_CAPTURE");
        camIntent.setComponent(ComponentName("com.sec.android.app.camera", "com.sec.android.app.camera.Camera"));
        startActivity(camIntent);

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


    private fun cachePicture (data: Intent) {
        runOnIoThread {
            val selectedImage = data.data

            // method 1
            try {
                val inputStream = contentResolver.openInputStream(selectedImage)
                val cw = ContextWrapper(applicationContext)
                val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
                // Create imageDir
                val mypath = File(directory, "profile.jpg")

                val fos = FileOutputStream(mypath)
                copy(inputStream, fos)

                photoViewModel.setImageURI(mypath.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun copy(instream: InputStream, out: OutputStream) {
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


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == REQUEST_GET_SINGLE_FILE) {
            if (resultCode == RESULT_OK) {
                if (intent != null && data != null) {

                    cachePicture(data)

                 }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

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

