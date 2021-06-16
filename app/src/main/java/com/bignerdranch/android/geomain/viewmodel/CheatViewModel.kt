package com.bignerdranch.android.geomain.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "CheatViewModel"
private const val CHEAT_BTN_PRESS_KEY = "cheatBtnPress"

class CheatViewModel(state: SavedStateHandle ): ViewModel() {
    private val savedStateHandle = state


    var cheatButtonPress = false


    fun saveCheatBtnStatus(status: Boolean) {
        savedStateHandle.set(CHEAT_BTN_PRESS_KEY, status)
    }

    fun getCheatBtnStatus(): Boolean =
        savedStateHandle.get(CHEAT_BTN_PRESS_KEY) ?: false


    init {
        Log.d(TAG, "CheatViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "CheatViewModel instance about to be destroyed")
    }
}