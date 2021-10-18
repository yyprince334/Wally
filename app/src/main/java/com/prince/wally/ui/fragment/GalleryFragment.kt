package com.prince.wally.ui.fragment

import android.os.Bundle
import android.system.Os.bind
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.prince.wally.R
import com.prince.wally.databinding.FragmentGalleryBinding
import com.prince.wally.ui.activity.detail.DetailActivity
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.startActivity
import com.prince.wally.util.viewBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val binding by viewBinding(FragmentGalleryBinding::bind)
    private val viewModel: GalleryViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val request = (findNavController().currentDestination?.label).toString()

        val galleryAdapter = GalleryAdapter { pic ->
            requireActivity().startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        galleryAdapter.addLoadStateListener {
            binding.progressBar.isVisible = it.source.refresh is LoadState.Loading
        }

        binding.picturesRecycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(request).collectLatest {
                galleryAdapter.submitData(it)
            }
        }
    }
}