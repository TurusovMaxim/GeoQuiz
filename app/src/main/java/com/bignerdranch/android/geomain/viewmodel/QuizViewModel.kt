package com.bignerdranch.android.geomain.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.geomain.R
import com.bignerdranch.android.geomain.model.Question

private const val TAG = "QuizViewModel"
private const val QUESTION_INDEX_KEY = "questionIndex"
private const val CHEAT_INDEX_KEY = "cheatIndex"
private const val CHEAT_STATUS = "cheatStatus"
private const val NUMB_CHEATING_QUESTION_KEY = "numbCheatingQuestion"
private const val LIST_OF_CHEATING_QUESTION_KEY = "listOfCheatingQuestion"

class QuizViewModel(state: SavedStateHandle): ViewModel() {
    private val savedStateHandle = state


    private val questionBank = listOf(
        Question(R.string.question_australia, answer = true, isCheating = false),
        Question(R.string.question_oceans, answer = true, isCheating = false),
        Question(R.string.question_mideast, answer = false, isCheating = false),
        Question(R.string.question_africa, answer = false, isCheating = false),
        Question(R.string.question_americas, answer = true, isCheating = false),
        Question(R.string.question_asia, answer = true, isCheating = false)
    )


    var questionIndex = 0
    var cheatIndex: Int = 0
    var numbCheatingQuestion: Int = 0
    var cheatStatus: Boolean = false

    var cheatingQuestions: MutableList<Int> = mutableListOf()


    val currentQuestionAnswer: Boolean
        get() = questionBank[questionIndex].answer

    val currentQuestionText: Int
        get() = questionBank[questionIndex].textResId

    fun moveNext() {
        questionIndex = (questionIndex + 1) % questionBank.size
    }

    fun moveBack() {
        questionIndex = if (questionIndex != 0) (questionIndex - 1) % questionBank.size
        else questionBank.size - 1
    }

    fun cheatingDetected() {
        questionBank[questionIndex].isCheating = true
    }

    fun isCheating(): Boolean =
        questionBank[questionIndex].isCheating

    fun setCheatingQuestion(index: Int) {
        questionBank[index].isCheating = true
    }


    fun saveQuestionParam(
        questionIndex: Int,
        cheatIndex: Int,
        cheatStatus: Boolean,
        numbCheatingQuestion: Int,
        cheatingQuestions: MutableList<Int>
    ) {
        savedStateHandle.set(QUESTION_INDEX_KEY, questionIndex)
        savedStateHandle.set(CHEAT_INDEX_KEY, cheatIndex)
        savedStateHandle.set(CHEAT_STATUS, cheatStatus)
        savedStateHandle.set(NUMB_CHEATING_QUESTION_KEY, numbCheatingQuestion)
        savedStateHandle.set(LIST_OF_CHEATING_QUESTION_KEY, cheatingQuestions)
    }

    fun getCurrQuestionIndex(): Int =
        savedStateHandle.get(QUESTION_INDEX_KEY) ?: 0


    fun getCurrCheatingIndex(): Int =
        savedStateHandle.get(CHEAT_INDEX_KEY) ?: 0


    fun getCurrCheatingStatus(): Boolean =
        savedStateHandle.get(CHEAT_STATUS) ?: false

    fun getCurrNumbCheatingOfQuestion(): Int =
        savedStateHandle.get(NUMB_CHEATING_QUESTION_KEY)  ?: 0

    fun getListOfCheatingQuestion(): MutableList<Int>? =
        savedStateHandle.get(LIST_OF_CHEATING_QUESTION_KEY)


    init {
        Log.d(TAG, "QuizViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "QuizViewModel instance about to be destroyed")
    }
}