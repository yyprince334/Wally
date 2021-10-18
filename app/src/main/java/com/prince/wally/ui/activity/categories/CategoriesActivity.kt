package com.prince.wally.ui.activity.categories

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.prince.wally.ui.activity.BaseActivity
import com.prince.wally.ui.activity.GalleryActivity
import com.prince.wally.util.Constants.PIC_EXTRA
import com.prince.wally.util.viewBinding

class CategoriesActivity : BaseActivity() {

    private val binding by viewBinding(ActivityCategoriesBinding::inflate)
    private val viewModel: CategoriesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.categories.observe(this) {
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@CategoriesActivity, 2)
                adapter = CategoriesAdapter(it) {
                    startActivity<GalleryActivity> { putExtra(PIC_EXTRA, it.categoryName) }
                }
            }
        }

        viewModel.progressIsVisible.observe(this) {
            binding.progressBar.isVisible = it
        }
    }
}