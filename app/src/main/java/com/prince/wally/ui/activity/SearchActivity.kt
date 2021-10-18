package com.prince.wally.ui.activity

import android.os.Bundle
import android.view.View.inflate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.prince.wally.databinding.ActivitySearchBinding
import com.prince.wally.ui.activity.detail.DetailActivity
import com.prince.wally.ui.fragment.GalleryAdapter
import com.prince.wally.ui.fragment.GalleryViewModel
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.startActivity
import com.prince.wally.util.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : BaseActivity() {

    private val binding by viewBinding(ActivitySearchBinding::inflate)
    private val viewModel: GalleryViewModel by viewModel()
    private lateinit var galleryAdapter: GalleryAdapter
    private var isFirstRequest = true
    private var isDelayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener { onBackPressed() }

        galleryAdapter = GalleryAdapter { pic ->
            startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (viewModel.q != query) {
                    viewModel.q = query
                    getPictures()
                }
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (viewModel.q != query) {
                    viewModel.q = query
                    getPictures()
                }
                return true
            }
        })

        binding.searchView.onActionViewExpanded()
    }

    private fun getPictures() {
        lifecycleScope.launchWhenStarted {
            if (isFirstRequest) {
                isFirstRequest = false
                viewModel.getPicListStream().collectLatest { galleryAdapter.submitData(it) }
            } else {
                if (!isDelayed) {
                    isDelayed = true
                    delay(2000)
                    isDelayed = false
                    viewModel.getPicListStream().collectLatest { galleryAdapter.submitData(it) }
                }
            }
        }
    }
}