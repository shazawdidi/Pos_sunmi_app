package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.utils.APP_LANGUAGE
import com.altkamul.xpay.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ChangeLanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val currentLanguage = context.dataStore.data.map {
        it[APP_LANGUAGE] ?: "en"
    }

    /** A function to update the current language */
    fun updateCurrentLanguage(newLanguage: String){
        viewModelScope.launch {
            context.dataStore.edit {
                it[APP_LANGUAGE] = newLanguage
            }
        }
    }

}