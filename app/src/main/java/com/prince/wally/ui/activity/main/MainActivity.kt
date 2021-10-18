package com.prince.wally.ui.activity.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.inflate
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.prince.wally.R
import com.prince.wally.databinding.ActivityMainBinding
import com.prince.wally.ui.activity.GalleryActivity
import com.prince.wally.ui.activity.SearchActivity
import com.prince.wally.ui.activity.categories.CategoriesActivity
import com.prince.wally.ui.activity.favorites.FavoritesActivity
import com.prince.wally.ui.activity.videos.VideosActivity
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.NetworkUtils.getNetworkLiveData
import com.prince.wally.util.shortToast
import com.prince.wally.util.startActivity
import com.prince.wally.util.viewBinding
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var currentNavController: LiveData<NavController>? = null
    private var backButtonPressedTwice = false
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) setupBottomNavigationBar()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null

        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
                if (::reviewInfo.isInitialized) reviewManager.launchReviewFlow(this, reviewInfo)
            }
        }

        getNetworkLiveData(applicationContext).observe(this) {
            binding.noInternetWarning.isVisible = !it
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> startActivity<SearchActivity>()
            R.id.action_gallery -> startActivity<CategoriesActivity>()
            R.id.action_videos -> startActivity<VideosActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.bmw, R.navigation.audi, R.navigation.mercedes)

        val controller = binding.bottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aston -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.aston)) }
            R.id.nav_bentley -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.bentley)) }
            R.id.nav_bugatti -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.bugatti)) }
            R.id.nav_ferrari -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.ferrari)) }
            R.id.nav_lambo -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.lambo_walp)) }
            R.id.nav_mclaren -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.mclaren)) }
            R.id.nav_porsche -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.porsche)) }
            R.id.nav_rolls -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.rolls)) }
            R.id.nav_favorites-> startActivity<FavoritesActivity>()
        }
        binding.drawerLayout.closeDrawer(START)
        return true
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(START) -> binding.drawerLayout.closeDrawer(START)
            else -> {
                when (currentNavController?.value?.currentDestination?.label) {
                    getString(R.string.bmw) -> {
                        if (backButtonPressedTwice) {
                            super.onBackPressed()
                        } else {
                            backButtonPressedTwice = true
                            shortToast(getString(R.string.press_back))
                            lifecycleScope.launchWhenStarted {
                                delay(2000)
                                backButtonPressedTwice = false
                            }
                        }
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }
}