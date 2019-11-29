package com.practice.myapplication.mvi.repository.main

import androidx.lifecycle.LiveData
import com.practice.myapplication.mvi.api.auth.network_responses.BlogCreateUpdateResponse
import com.practice.myapplication.mvi.api.main.OpenApiMainService
import com.practice.myapplication.mvi.models.AuthToken
import com.practice.myapplication.mvi.models.BlogPost
import com.practice.myapplication.mvi.persistence.BlogPostDao
import com.practice.myapplication.mvi.repository.JobManager
import com.practice.myapplication.mvi.repository.NetworkBoundResource
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.Response
import com.practice.myapplication.mvi.ui.ResponseType
import com.practice.myapplication.mvi.ui.main.create_blog.state.CreateBlogViewState
import com.practice.myapplication.mvi.util.AbsentLiveData
import com.practice.myapplication.mvi.util.ApiSuccessResponse
import com.practice.myapplication.mvi.util.DateUtils
import com.practice.myapplication.mvi.util.GenericApiResponse
import com.practice.myapplication.mvi.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogRepository: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("CreateBlogRepository") {
    private val TAG: String = "AppDebug"

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            override suspend fun createCacheRequestAndReturn() {
                // not applicable in this case, since we are not doing anything related to cache
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                if (!response.body.response.equals(RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) {
                    val updateBlogPost = BlogPost(
                        response.body.pk,
                        response.body.title,
                        response.body.slug,
                        response.body.body,
                        response.body.image,
                        DateUtils.convertServerStringDateToLong(response.body.date_updated),
                        response.body.username
                    )
                    updateLocalDb(updateBlogPost)
                }

                withContext(Main)
                {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
               return openApiMainService.createBlog(
                "Token ${authToken.token}",
                   title,
                   body,
                   image
               )
            }

            // not applicable since we are not loading anything from the cache
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogRepository.insert(cacheObject)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }

        }.asLiveData()
    }

}