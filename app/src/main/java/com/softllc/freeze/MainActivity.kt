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
import android.os.Parcelable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.softllc.freeze.data.Photo
import com.softllc.freeze.utilities.ImageFile
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.runOnIoThread
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var photoListViewModel: PhotoListViewModel

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            Log.d("djm handleSendImage", it.toString())
            val photoId = UUID.randomUUID().toString()
            runOnIoThread {
                val fileName = ImageFile(this).upload(photoId, it.toString())
                photoListViewModel.addPhoto(photoId, fileName)
            }
            // navigate to photo fragment
            //val direction = FreezeFragmentDirections.actionFreezeFragmentToPhotoFragment(photoId)
            //navController.navigate(direction)
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // Update UI to reflect multiple images being shared
            runOnIoThread {
                for (i in it.reversed()) {
                    Log.d("djm handleSendMult", i.toString())
                    val photoId = UUID.randomUUID().toString()

                    val fileName = ImageFile(this).upload(photoId, i.toString())
                    photoListViewModel.addPhoto(photoId, fileName)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            //val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            //keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }


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
                R.id.delete_all -> deleteGallery()
            }
            drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }

        FreezeApp.locked.observe(this, Observer { locked ->
            when (locked) {
                true -> appbar.visibility = GONE
                false -> appbar.visibility = VISIBLE
            }
        })

        val factory = InjectorUtils.providePhotoListViewModelFactory(this)
        photoListViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoListViewModel::class.java)
        photoListViewModel.photos.observe(this, Observer { photos ->

        })

        processIntent(intent)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        if (intent != null) {
            when {
                intent.action == Intent.ACTION_SEND -> {
                    if (intent.type?.startsWith("image/") == true) {
                        handleSendImage(intent) // Handle single image being sent
                    }
                }
                intent.action == Intent.ACTION_SEND_MULTIPLE
                        == true -> {
                    handleSendMultipleImages(intent) // Handle multiple images being sent
                }
                else -> {
                    // Handle other intents, such as being started from the home screen
                }
            }
            intent.replaceExtras(Bundle());
            intent.setAction("");
            intent.setData(null);
            intent.setFlags(0);
        }
    }

    private fun deleteGallery() {
        val photos = photoListViewModel.photos?.value
        if (photos != null) {
            runOnIoThread {
                for (photo in photos) {
                    File(photo.imageUrl).delete()
                    InjectorUtils.getPhotoRepository(this).delete(photo)
                }

            }


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

