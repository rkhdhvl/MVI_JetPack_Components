package com.practice.myapplication.mvi.ui.main.create_blog.state

import android.net.Uri
import okhttp3.MultipartBody

data class CreateBlogViewState(

    // CreateBlogFragment vars
    var blogFields: NewBlogFields = NewBlogFields()

)
{
    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newImageUri: Uri? = null
    )
}