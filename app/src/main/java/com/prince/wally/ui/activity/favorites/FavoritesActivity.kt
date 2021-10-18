package com.prince.wally.ui.activity.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.inflate
import com.google.gson.Gson
import com.prince.wally.R
import com.prince.wally.databinding.ActivityFavoritesBinding
import com.prince.wally.model.local.Favorite.Favorite.Column.url
import com.prince.wally.model.remote.response.CommonPic
import com.prince.wally.ui.activity.BaseActivity
import com.prince.wally.ui.activity.detail.DetailActivity
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.viewBinding

class FavoritesActivity : BaseActivity() {

    private val binding by viewBinding(ActivityFavoritesBinding::inflate)
    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(R.string.favorites_toolbar)
        }

        viewModel.favorites.observe(this) { list ->
            FavoritesAdapter {
                Gson().fromJson(it.hit, CommonPic::class.java).apply {
                    startActivity<DetailActivity> {
                        putExtra(PIC_EXTRA, CommonPic(url, width, height, tags, imageURL, fullHDURL, id, videoId))
                    }
                }
            }.apply {
                binding.picturesRecycler.adapter = this
                submitList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.favorites_menu, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)

        viewModel.isEmpty.observe(this) {
            menuItem.isVisible = !it
            binding.emptyTv.isVisible = it
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_remove_all) viewModel.deleteAll()
        return super.onOptionsItemSelected(item)
    }
}