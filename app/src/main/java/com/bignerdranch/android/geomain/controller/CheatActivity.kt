package com.bignerdranch.android.geomain.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.geomain.viewmodel.CheatViewModel
import com.bignerdranch.android.geomain.R

private const val TAG = "CheatActivity"
private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geomain.controller.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geomain.controller.answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button


    private var answerIsTrue: Boolean = false
    private var cheatBtnStatus: Boolean = false


    private val cheatViewModel: CheatViewModel by lazy {
        ViewModelProvider(this, SavedStateViewModelFactory(this.application, this))
            .get(CheatViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        Log.d(TAG, "onCreate(Bundle?) called")

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        cheatBtnStatus = cheatViewModel.getCheatBtnStatus()

        showAnswerButton.setOnClickListener{ showAnswer() }

        if (cheatBtnStatus) { showAnswer() }
    }


    private fun showAnswer() {
        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }

        answerTextView.setText(answerText)

        if (!cheatViewModel.cheatButtonPress) {
            cheatViewModel.cheatButtonPress = true
            cheatBtnStatus = cheatViewModel.cheatButtonPress
        }

        setAnswerShownResult(cheatBtnStatus)
    }


    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")

        cheatViewModel.saveCheatBtnStatus(cheatViewModel.cheatButtonPress)
    }


    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

}