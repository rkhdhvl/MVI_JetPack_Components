package com.practice.myapplication.mvi.api.auth

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import com.practice.myapplication.mvi.api.auth.network_responses.LoginResponse
import com.practice.myapplication.mvi.api.auth.network_responses.RegistrationResponse
import com.practice.myapplication.mvi.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiAuthService {

    @POST("account/login")
    @FormUrlEncoded
    fun login(
      @Field("username") email: String,
      @Field("password") password: String
    ) : LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    @FormUrlEncoded
    fun register(
      @Field ("email") email: String,
      @Field ("username") username: String,
      @Field ("password") password: String,
      @Field ("password2") password2: String
    ) : LiveData<GenericApiResponse<RegistrationResponse>>
}