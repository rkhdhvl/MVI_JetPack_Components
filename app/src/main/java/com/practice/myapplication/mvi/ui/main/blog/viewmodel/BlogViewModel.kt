package com.practice.myapplication.mvi.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.practice.myapplication.mvi.persistence.BlogQueryUtils
import com.practice.myapplication.mvi.repository.main.BlogRepository
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.BaseViewModel
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.Loading
import com.practice.myapplication.mvi.ui.main.blog.state.BlogStateEvent
import com.practice.myapplication.mvi.ui.main.blog.state.BlogStateEvent.None
import com.practice.myapplication.mvi.ui.main.blog.state.BlogViewState
import com.practice.myapplication.mvi.util.AbsentLiveData
import com.practice.myapplication.mvi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.practice.myapplication.mvi.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    // initializing the default filter and order that would be set
    init {
        setBlogFilter(
            sharedPreferences.getString(BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED)
        )

        setBlogFilter(
            sharedPreferences.getString(
                BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_ASC)
        )
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        // it could either be the order which is asc or desc
                        // or it could be only the filter parameter or some combination of both
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: return AbsentLiveData.create()
            }

            is BlogStateEvent.CheckAuthorOfBlogPost ->{
                return AbsentLiveData.create()
            }

            is None ->{
                return object: LiveData<DataState<BlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }


    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }


    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    // This will be called in the BlogFragment after the user selects the filter
   fun saveFilterOptions(filter : String, order :String)
   {
       // asynchronously saving the changes made inside the shared preferences file
       editor.putString(BLOG_FILTER,filter)
       editor.apply()

       editor.putString(BLOG_ORDER,filter)
       editor.apply()
   }


}