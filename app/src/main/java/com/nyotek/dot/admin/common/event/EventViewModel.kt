package com.nyotek.dot.admin.common.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import javax.inject.Inject

class EventViewModel @Inject constructor()  : ViewModel() {

    private val _refreshEvent = MutableLiveData<Boolean>()

    val refreshEvent: LiveData<Boolean> get() = _refreshEvent
   

    fun resumeEvent(isResume: Boolean) {
        _refreshEvent.value = isResume
    }
}