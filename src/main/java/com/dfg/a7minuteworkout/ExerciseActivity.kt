package com.dfg.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var restTimerDuration: Long = 10

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var exerciseTimerDuration:Long = 30

    private var tts: TextToSpeech? = null //variable for text to speech

    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(toolbar_exercise_activity)
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()

        setupRestView()

        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player != null){
            player!!.stop()
        }

        super.onDestroy()
    }

    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 10 - restProgress
                tv_timer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        exerciseProgressBar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                exerciseProgressBar.progress = 30 - exerciseProgress
                tv_exercise_timer.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsComplete(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    private fun setupRestView() {
        try {
            player = MediaPlayer.create(applicationContext, R.raw.press_start)

            player!!.isLooping = false
            player!!.start()
        }catch (e: Exception){
            e.stackTrace
        }


        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = GONE

        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        tv_upcomingExercise.text = exerciseList!![currentExercisePosition + 1].getname()
        setRestProgressBar()
    }

    private fun setupExerciseView() {

        llRestView.visibility = GONE
        llExerciseView.visibility = View.VISIBLE
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getname())

        setExerciseProgressBar()

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tv_ExerciseName.text = exerciseList!![currentExercisePosition].getname()
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.SIMPLIFIED_CHINESE)
            if(result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The LAnguage specified is not supported!")
            }else{
                Log.e("TTS", "Initialization Failed!")
            }
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView(){
        rvExerciseStatus.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)
        rvExerciseStatus.adapter = exerciseAdapter

    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}