package com.practice.myapplication.mvi.ui.main.account

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.AccountProperties
import com.practice.myapplication.mvi.repository.main.AccountRepository
import com.practice.myapplication.mvi.session.SessionManager
import com.practice.myapplication.mvi.ui.BaseViewModel
import com.practice.myapplication.mvi.ui.DataState
import com.practice.myapplication.mvi.ui.main.account.state.AccountStateEvent
import com.practice.myapplication.mvi.ui.main.account.state.AccountStateEvent.*
import com.practice.myapplication.mvi.ui.main.account.state.AccountViewState
import com.practice.myapplication.mvi.util.AbsentLiveData
import javax.inject.Inject


class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>(){
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
      when(stateEvent)
      {
          is GetAccountPropertiesEvent ->
          {
              return sessionManager.cachedToken.value?.let {
                  authToken ->
                  accountRepository.getAccountProperties(authToken)
              }?: AbsentLiveData.create()
          }

          is UpdateAccountPropertiesEvent ->
          {
              return AbsentLiveData.create()
          }

          is ChangePasswordEvent ->
          {
              return sessionManager.cachedToken.value?.let{
                  authToken ->
                  accountRepository.updatePassword(
                      authToken,
                      stateEvent.currentPassword,
                      stateEvent.newPassword,
                      stateEvent.confirmNewPassword
                  )
              }?: AbsentLiveData.create()
          }

          is None ->
          {
              return AbsentLiveData.create()
          }
      }
    }

    override fun initNewViewState(): AccountViewState {
     return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties : AccountProperties)
    {
       val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties)
        {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout()
    {
        sessionManager.logout()
    }

    fun cancelActiveJobs(){
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}