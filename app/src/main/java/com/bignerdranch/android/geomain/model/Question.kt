package com.bignerdranch.android.geomain.model

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answer: Boolean, var isCheating: Boolean)