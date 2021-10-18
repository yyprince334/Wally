package com.prince.wally.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.prince.wally.databinding.FragmentGalleryBinding
import com.prince.wally.ui.activity.detail.DetailActivity
import com.prince.wally.ui.fragment.GalleryAdapter
import com.prince.wally.ui.fragment.GalleryViewModel
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.NetworkUtils.getNetworkLiveData
import com.prince.wally.util.startActivity
import com.prince.wally.util.viewBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity() {

    private val binding by viewBinding(FragmentGalleryBinding::inflate)
    private val viewModel: GalleryViewModel by viewModel()
    private val query by lazy { intent.getStringExtra(PIC_EXTRA).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = query
        }

        val adapter = GalleryAdapter { pic ->
            startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        adapter.addLoadStateListener {
            binding.progressBar.isVisible = it.source.refresh is LoadState.Loading
        }

        binding.picturesRecycler.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(query).collectLatest {
                adapter.submitData(it)
            }
        }

        getNetworkLiveData(applicationContext).observe(this) {
            binding.noInternetWarning.isVisible = !it
        }
    }
}