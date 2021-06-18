package com.bignerdranch.android.geomain.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.geomain.R
import com.bignerdranch.android.geomain.model.Question

private const val TAG = "QuizViewModel"

//KEYS
private const val QN_IDX_KEY = "questionIndex"
private const val CHEATING_QN_IDX_KEY = "cheatingQuestionIndex"
private const val CHEATING_BTN_PRESS_STATUS = "cheatingButtonPressStatus"
private const val NUMB_OF_CHEATING_QNS_KEY = "numbOfCheatingQuestions"
private const val LIST_OF_CHEATING_QNS_KEY = "listOfCheatingQuestions"

/**
 * @param state - QuizViewModel takes in SavedStateHandle as
 * a constructor parameter to save/get a state of MainActivity instance
 */
class QuizViewModel(state: SavedStateHandle): ViewModel() {
    private val savedStateHandle = state


    //quiz questions
    private val questionBank = listOf(
        Question(R.string.question_australia, answer = true, isCheating = false),
        Question(R.string.question_oceans, answer = true, isCheating = false),
        Question(R.string.question_mideast, answer = false, isCheating = false),
        Question(R.string.question_africa, answer = false, isCheating = false),
        Question(R.string.question_americas, answer = true, isCheating = false),
        Question(R.string.question_asia, answer = true, isCheating = false)
    )


    //an index of the current question
    var questionIdx = 0

    //an index of the current cheating question
    var cheatingIdx: Int = 0

    //a number of cheating questions
    var numbOfCheatingQns: Int = 0

    //is the CHEAT button pressed?
    var cheatingBtnPressStatus: Boolean = false

    //a list of cheating questions
    var listOfCheatingQns: MutableList<Int> = mutableListOf()


    //get an answer to the current question
    val currQuestionAnswer: Boolean
        get() = questionBank[questionIdx].answer

    //get a text of the current question
    val currQuestionText: Int
        get() = questionBank[questionIdx].textResId


    //go to the next question
    fun moveNext() {
        questionIdx = (questionIdx + 1) % questionBank.size
    }

    //go to the previous question
    fun moveBack() {
        questionIdx = if (questionIdx != 0) (questionIdx - 1) % questionBank.size
        else questionBank.size - 1
    }


    //check if an user cheated
    fun isUserCheated(): Boolean =
        questionBank[questionIdx].isCheating

    //flag a question as cheating
    fun setQuestionAsCheating(index: Int) {
        questionBank[index].isCheating = true
    }


    //save parameters before killing the application process
    fun saveQuestionParam(
        questionIdx: Int,
        cheatingIdx: Int,
        numbOfCheatingQns: Int,
        cheatingBtnPressStatus: Boolean,
        listOfCheatingQns: MutableList<Int>
    ) {
        savedStateHandle.set(QN_IDX_KEY, questionIdx)
        savedStateHandle.set(CHEATING_QN_IDX_KEY, cheatingIdx)
        savedStateHandle.set(NUMB_OF_CHEATING_QNS_KEY, numbOfCheatingQns)
        savedStateHandle.set(CHEATING_BTN_PRESS_STATUS, cheatingBtnPressStatus)
        savedStateHandle.set(LIST_OF_CHEATING_QNS_KEY, listOfCheatingQns)
    }


    //get parameters after killing the application process
    fun getCurrQuestionIdx(): Int =
        savedStateHandle.get(QN_IDX_KEY) ?: 0

    fun getCurrCheatingIdx(): Int =
        savedStateHandle.get(CHEATING_QN_IDX_KEY) ?: 0

    fun getCurrNumbOfCheatingQn(): Int =
        savedStateHandle.get(NUMB_OF_CHEATING_QNS_KEY)  ?: 0

    fun getCurrCheatingBtnPressStatus(): Boolean =
        savedStateHandle.get(CHEATING_BTN_PRESS_STATUS) ?: false

    fun getCurrListOfCheatingQns(): MutableList<Int>? =
        savedStateHandle.get(LIST_OF_CHEATING_QNS_KEY)


    init {
        Log.d(TAG, "QuizViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "QuizViewModel instance about to be destroyed")
    }
}