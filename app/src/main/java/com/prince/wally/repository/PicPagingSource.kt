package com.prince.wally.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prince.wally.model.remote.response.CommonPic

class PicPagingSource(
    private val repository: Repository,
    private val query: String
) : PagingSource<Int, CommonPic>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommonPic> {
        return try {
            val nextPage = params.key ?: 1
            val response = repository.getPictures(query, nextPage)
            LoadResult.Page(
                response,
                if (nextPage == 1) null else nextPage - 1,
                if (nextPage < response.size) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommonPic>): Int? {
        return null
    }
}