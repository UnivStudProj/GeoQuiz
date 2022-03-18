package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private TextView mQuestionTextView;

    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private int mScore;
    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    private final Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_1, true),
            new Question(R.string.question_2, true),
            new Question(R.string.question_3, true),
            new Question(R.string.question_4, false),
            new Question(R.string.question_5, false)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_INDEX, false);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex == mQuestionBank.length - 1) {
                    mQuestionTextView.setOnClickListener(null);
                } else {
                    mCurrentIndex++;
                    updateQuestion();
                }
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });


        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex++;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex--;
                mScore--;
                updateQuestion();
            }
        });

        Button cheatButton = (Button) findViewById(R.id.cheat_button);
        cheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswer();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        Button restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = 0;
                mScore = 0;
                mIsCheater = false;

                String resetText = "Quiz has been restarted!";
                Toast resetToast = Toast.makeText(QuizActivity.this, resetText, Toast.LENGTH_SHORT);
                resetToast.setGravity(Gravity.TOP, 0, 350);
                resetToast.show();

                updateQuestion();
            }
        });

        updateQuestion();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_INDEX, mIsCheater);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);

        mPrevButton.setEnabled(mCurrentIndex != 0);
        mNextButton.setEnabled(mCurrentIndex != mQuestionBank.length - 1);
    }

    private void checkAnswer(boolean userAnswer) {
        boolean TrueAnswer = mQuestionBank[mCurrentIndex].isAnswer();
        int messageRes;

        if (mIsCheater) {
            messageRes = R.string.judgment_toast;
        } else {
            if (userAnswer == TrueAnswer) {
                messageRes = R.string.correct_toast;
                mScore++;
            } else {
                messageRes = R.string.incorrect_toast;
            }
        }

        Toast toast = Toast.makeText(this, messageRes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 150);
        toast.show();

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);

        if (mCurrentIndex == mQuestionBank.length - 1) {
            float scoreRes = (float) mScore / mQuestionBank.length * 100;
            String toastMes = String.format("Your score is %s%%", (int) scoreRes);
            Toast toastRes = Toast.makeText(this, toastMes, Toast.LENGTH_SHORT);
            toastRes.show();

            mNextButton.setEnabled(false);
            mPrevButton.setEnabled(false);
        }

    }
}