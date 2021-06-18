package com.bignerdranch.android.geomain.controller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.geomain.R
import com.bignerdranch.android.geomain.viewmodel.QuizViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var sdkTextView: TextView

    private val minNumbOfPrompts = 0
    private val maxNumbOfPrompts = 3

    private val sdkVersion = Build.VERSION.SDK_INT.toString()


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this, SavedStateViewModelFactory(this.application, this))
            .get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate(Bundle?) called")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_btn)

        nextButton = findViewById(R.id.next_btn)
        prevButton = findViewById(R.id.prev_btn)

        questionTextView = findViewById(R.id.question_text_view)
        sdkTextView = findViewById(R.id.sdk_text)

        nextButton.setOnClickListener(this)
        prevButton.setOnClickListener(this)
        cheatButton.setOnClickListener(this)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        quizViewModel.questionIdx = quizViewModel.getCurrQuestionIdx()
        quizViewModel.cheatingIdx = quizViewModel.getCurrCheatingIdx()
        quizViewModel.cheatingBtnPressStatus = quizViewModel.getCurrCheatingBtnPressStatus()
        quizViewModel.numbOfCheatingQns = quizViewModel.getCurrNumbOfCheatingQn()

        val cheatingQuestions = quizViewModel.getCurrListOfCheatingQns()
        if (cheatingQuestions != null) {
            quizViewModel.listOfCheatingQns = cheatingQuestions

            for (element in quizViewModel.listOfCheatingQns) {
                quizViewModel.setQuestionAsCheating(element)
            }
        }

        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")

        sdkTextView.text = getString(R.string.api_level, sdkVersion)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currQuestionAnswer

        val messageResId = when {
            quizViewModel.isUserCheated() -> R.string.judgment_toast

            userAnswer == correctAnswer -> R.string.correct_toast

            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currQuestionText

        questionTextView.setText(questionTextResId)
    }

    @SuppressLint("RestrictedApi")
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.next_btn -> {
                quizViewModel.moveNext()
                updateQuestion()
            }

            R.id.prev_btn -> {
                quizViewModel.moveBack()
                updateQuestion()
            }

            R.id.cheat_btn -> {
                if (quizViewModel.numbOfCheatingQns in minNumbOfPrompts until maxNumbOfPrompts) {
                    val answerIsTrue = quizViewModel.currQuestionAnswer
                    val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

                    startCheatActivityForResult.launch(intent)
                } else {
                    Toast.makeText(this, R.string.no_prompts_toast, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val startCheatActivityForResult:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            quizViewModel.cheatingBtnPressStatus = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            if (quizViewModel.cheatingBtnPressStatus) {
                quizViewModel.setQuestionAsCheating(quizViewModel.questionIdx)
                quizViewModel.numbOfCheatingQns += 1

                quizViewModel.listOfCheatingQns.add(quizViewModel.questionIdx)

                val numbOfPrompts = this.resources.getQuantityString(
                    R.plurals.numb_of_prompts_toast,
                    maxNumbOfPrompts - quizViewModel.numbOfCheatingQns,
                    maxNumbOfPrompts - quizViewModel.numbOfCheatingQns
                )

                Toast.makeText(this, "You have $numbOfPrompts", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")

        quizViewModel.saveQuestionParam(
            quizViewModel.questionIdx,
            quizViewModel.cheatingIdx,
            quizViewModel.numbOfCheatingQns,
            quizViewModel.cheatingBtnPressStatus,
            quizViewModel.listOfCheatingQns
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}