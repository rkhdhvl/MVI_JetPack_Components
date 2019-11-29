package com.practice.myapplication.mvi.ui.main.create_blog.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.practice.myapplication.mvi.repository.main.CreateBlogRepository
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.BaseViewModel
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.Loading
import com.practice.myapplication.mvi.ui.main.create_blog.state.CreateBlogStateEvent
import com.practice.myapplication.mvi.ui.main.create_blog.state.CreateBlogViewState
import com.practice.myapplication.mvi.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {
    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        when (stateEvent) {
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                return sessionManager.cachedToken.value?.let {
                    authToken ->
                    val title = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.title
                    )
                    val body = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.body
                    )

                    createBlogRepository.createNewBlogPost(
                        authToken,
                        title,
                        body,
                        stateEvent.image
                    )
                } ?: AbsentLiveData.create()
            }

            is CreateBlogStateEvent.None -> {
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }

        }
    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun getNewImageUri():Uri?{
        getCurrentViewStateOrNew().let {
            it.blogFields.let {
               return  it.newImageUri
            }
        }
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

    fun clearNewBlogFields()
    {
        val update = getCurrentViewStateOrNew()
        update.blogFields = CreateBlogViewState.NewBlogFields()
        setViewState(update)
    }

    fun cancelActiveJobs()
    {
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData()
    {
        setStateEvent(CreateBlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}