package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.ContactUs
import com.altkamul.xpay.repositroy.ContactUsRepository
import com.altkamul.xpay.utils.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val contactUsRepository: ContactUsRepository,
    private val context: Context,
) :
    ViewModel() {

    private var _contactUsData = MutableLiveData<ContactUs>()
    val contactUsData: LiveData<ContactUs> = _contactUsData

    init {
        getContactDetails()
    }


    /** This Function Will Assaying  Contact Data To _contactUsData*/
    private fun getContactDetails() {
        viewModelScope.launch {
            /** Get Local Contact Data*/
            val contactData = contactUsRepository.getContactDataLocally()
            if (contactData != null)
            /** If Its Already Exist Assigning it Form Local*/
                _contactUsData.value = contactData
            else
                Common.createToast(context, "There a Problem Please Call The Support")
        }
    }
}