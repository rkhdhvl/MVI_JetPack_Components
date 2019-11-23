package com.practice.myapplication.mvi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.models.AccountProperties
import com.practice.myapplication.mvi.api.GenericResponse
import com.practice.myapplication.mvi.api.main.OpenApiMainService
import com.practice.myapplication.mvi.models.AuthToken
import com.practice.myapplication.mvi.persistence.AccountPropertiesDao
import com.practice.myapplication.mvi.repository.JobManager
import com.practice.myapplication.mvi.repository.NetworkBoundResource
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.Response
import com.practice.myapplication.mvi.ui.ResponseType
import com.practice.myapplication.mvi.ui.main.account.state.AccountViewState
import com.practice.myapplication.mvi.util.AbsentLiveData
import com.practice.myapplication.mvi.util.ApiSuccessResponse
import com.practice.myapplication.mvi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountDaoProperties: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG: String = "AppDebug"

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<AccountProperties, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            /*
            * This will be called ,if for whatever reason the network is down ,view the cache */
            override suspend fun createCacheRequestAndReturn() {
                // view the db cache
               withContext(Dispatchers.Main){
                   result.addSource(loadFromCache())
                   {
                           viewState -> onCompleteJob(DataState.data(
                       data = viewState,
                       response = null
                   ))
                   }
               }
            }

            // In case of successful response
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token}")
            }

            // using switchMap to convert LiveData from one type to another
            // in this case the searchByPk method returns a LiveData of type AccountProperties
            // hence using switchMap to convert from LiveData<AccountProperties> to LiveData<AccountViewState>
            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountDaoProperties.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                // taking the data retrieved from retrofit and updating the local database

                if(cacheObject is AccountProperties) {
                    cacheObject?.let {
                        accountDaoProperties.updateAccountProperties(
                            cacheObject.pk,
                            cacheObject.email,
                            cacheObject.username
                        )
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties",job)
            }

        }.asLiveData()
    }


    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null) // The update does not return a CacheObject

                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                return accountDaoProperties.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties",job)
            }

        }.asLiveData()
    }


    fun updatePassword(authToken: AuthToken, currentPassword: String, newPassword: String, confirmNewPassword: String): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(null,
                            Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob("updatePassword",job)
            }

        }.asLiveData()
    }


}