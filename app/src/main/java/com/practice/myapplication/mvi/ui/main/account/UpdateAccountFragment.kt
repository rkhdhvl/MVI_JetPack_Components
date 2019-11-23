package com.practice.myapplication.mvi.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.models.AccountProperties
import com.practice.myapplication.R
import com.practice.myapplication.mvi.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*

class UpdateAccountFragment : BaseAccountFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    fun subscribeObservers()
    {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            dataState -> stateChangeListener.onDataStateChange(dataState)
            Log.d(TAG,"Update Account Fragment , DataState : ${dataState}")
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            viewState ->
            if(viewState!=null)
            {
                viewState.accountProperties?.let {
                    Log.d(TAG,"Update Account Fragment, ViewState : ${it}")
                    setDataAccountFields(it)
                }
            }
        })
    }

    private fun setDataAccountFields( accountProperties: AccountProperties)
    {
        input_email?.let {
            input_email.setText(accountProperties.email)
        }
        input_username?.let {
            input_username.setText(accountProperties.username)
        }
    }

    // initiating the state event to save the changes
    private fun saveChanges()
    {
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}