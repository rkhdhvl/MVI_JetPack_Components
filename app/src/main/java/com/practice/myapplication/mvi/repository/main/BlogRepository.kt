package com.practice.myapplication.mvi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.practice.myapplication.mvi.api.main.OpenApiMainService
import com.practice.myapplication.mvi.api.main.responses.BlogListSearchResponse
import com.practice.myapplication.mvi.models.AuthToken
import com.practice.myapplication.mvi.models.BlogPost
import com.practice.myapplication.mvi.persistence.BlogPostDao
import com.practice.myapplication.mvi.persistence.returnOrderedBlogQuery
import com.practice.myapplication.mvi.repository.JobManager
import com.practice.myapplication.mvi.repository.NetworkBoundResource
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.main.blog.state.BlogViewState
import com.practice.myapplication.mvi.util.ApiSuccessResponse
import com.practice.myapplication.mvi.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.practice.myapplication.mvi.util.DateUtils
import com.practice.myapplication.mvi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {
    private val TAG: String = "AppDebug"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder : String,
        page:Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>
            (
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                // view the database cache , update the value of the live and return
                withContext(Main)
                {
                    result.addSource(loadFromCache()){
                        viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        // checking if the query is exhausted
                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size )
                        {
                          viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState,null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
            val blogPostList : ArrayList<BlogPost> = ArrayList()
                for(blogPostResponse in response.body.results)
                {
                    blogPostList.add(
                        BlogPost(pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(
                                blogPostResponse.date_updated
                            ),
                            username = blogPostResponse.username)
                    )
                }
                updateLocalDb(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
              return openApiMainService.searchListBlogPosts(
                  "Token ${authToken.token!!}",
                  query = query,
                  ordering = filterAndOrder,
                  page = page
              )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
            // returning the data from cache based on the search query
                return blogPostDao.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page
                )
                    .switchMap {
                        object:LiveData<BlogViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if (cacheObject != null) {
                    // switch to the background thread for doing some database transaction
                    withContext(IO)
                    {
                        for (blogPost in cacheObject) {
                            try {
                          // launch each insert as a seperate job to executed in parallel
                             launch {
                                 Log.d(TAG, "inserting blog post : ${blogPost}")
                                 blogPostDao.insert(blogPost)
                             }
                            } catch (e: Exception) {
                                Log.d(
                                    TAG,
                                    "updateLocalDBError error updating cache on blog post with slug : ${blogPost.slug}"
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts",job)
            }
        }.asLiveData()
    }

}