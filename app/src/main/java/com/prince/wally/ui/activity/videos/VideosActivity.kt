package com.prince.wally.ui.activity.videos

import android.os.Bundle
import com.prince.wally.R
import com.prince.wally.databinding.ActivityVideosBinding
import com.prince.wally.ui.activity.BaseActivity
import com.prince.wally.util.Constants.ID_EXTRA
import com.prince.wally.util.startActivity
import com.prince.wally.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideosActivity : BaseActivity() {

    private val binding by viewBinding(ActivityVideosBinding::inflate)
    private val viewModel: VideosViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(R.string.videos)
        }

        viewModel.videos.observe(this) {
            binding.videosRecycler.apply {
                setHasFixedSize(true)
                it?.let {
                    adapter = VideosAdapter(it) {
                        startActivity<VideoActivity> { putExtra(ID_EXTRA, it) }
                    }
                }
            }
        }
    }
}