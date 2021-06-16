package com.bignerdranch.android.geomain.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.geomain.viewmodel.QuizViewModel
import com.bignerdranch.android.geomain.R

private const val TAG = "MainActivity"
private const val REQUEST_CODE_CHEAT = 0

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

        quizViewModel.questionIndex = quizViewModel.getCurrQuestionIndex()
        quizViewModel.cheatIndex = quizViewModel.getCurrCheatingIndex()
        quizViewModel.cheatStatus = quizViewModel.getCurrCheatingStatus()
        quizViewModel.numbCheatingQuestion = quizViewModel.getCurrNumbCheatingOfQuestion()

        updateQuestion()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheating() -> R.string.judgment_toast

            userAnswer == correctAnswer -> R.string.correct_toast

            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText

        questionTextView.setText(questionTextResId)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")

        sdkTextView.text = getString(R.string.api_level, sdkVersion)
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
                if (quizViewModel.numbCheatingQuestion in minNumbOfPrompts until maxNumbOfPrompts)
                {
                    val answerIsTrue = quizViewModel.currentQuestionAnswer
                    val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val options = ActivityOptions.makeClipRevealAnimation(
                            view,
                            0, 0,
                            view.width, view.height
                        )

                        startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                    } else {
                        startActivityForResult(intent, REQUEST_CODE_CHEAT)
                    }
                } else {
                    Toast.makeText(this, R.string.no_prompts_toast, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK)
            return

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.cheatStatus = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            if (quizViewModel.cheatStatus) {
                quizViewModel.cheatingDetected()
                quizViewModel.numbCheatingQuestion += 1

                val numbOfPrompts = this.resources.getQuantityString(
                    R.plurals.numb_of_prompts_toast,
                    maxNumbOfPrompts - quizViewModel.numbCheatingQuestion,
                    maxNumbOfPrompts - quizViewModel.numbCheatingQuestion
                )

                Toast.makeText(this, "You have $numbOfPrompts", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")

        quizViewModel.saveQuestionParam(
            quizViewModel.questionIndex,
            quizViewModel.cheatIndex,
            quizViewModel.cheatStatus,
            quizViewModel.numbCheatingQuestion
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}