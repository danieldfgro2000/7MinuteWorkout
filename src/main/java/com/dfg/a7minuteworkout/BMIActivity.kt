package com.dfg.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_b_m_i.*
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
    val US_UNIT_VIEW = "US_UNIT_VIEW"

    var currentVisibileView:String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_m_i)

        setSupportActionBar(toolbar_bmi_activity)

        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.title = "CALCULATE BMI"
        }

        toolbar_bmi_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btnCalculateUnits.setOnClickListener {
            if (currentVisibileView.equals(METRIC_UNITS_VIEW)){
                if (validateMetricUnits()){
                    val heightValue: Float = etMetricUnitHeight.text.toString().toFloat() / 100
                    val weightValue: Float = etMetricUnitWeight.text.toString().toFloat()

                    val bmi = weightValue / (heightValue * heightValue)

                    displayBMIResult(bmi)
                }else{
                    Toast.makeText(this@BMIActivity,
                        "Please enter valid values.", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (validateUsUnits()){
                    val usUnitHEightValueFeet: String = etUSUnitHeightFeet.text.toString()
                    val usUnitHeightValueInch: String = etUsUnitHeightInch.text.toString()
                    val usUnitWeightValue: Float = etUsUnitWeight.text.toString().toFloat()

                    val heightValue = usUnitHeightValueInch.toFloat() + usUnitHEightValueFeet.toFloat() * 12

                    val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))

                    displayBMIResult(bmi)

                }else{
                    Toast.makeText(this@BMIActivity, "Please enter valid values",
                    Toast.LENGTH_SHORT).show()
                }
            }



        }
        makeVisibleMetricUnitsView()
        rgUnits.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUsUnitsView()
            }
        }
    }

    private fun makeVisibleUsUnitsView(){
        currentVisibileView = US_UNIT_VIEW
        tilMetricUnitWeight.visibility = View.GONE
        tilMetricUnitHeight.visibility = View.GONE

        etUsUnitWeight.text!!.clear()
        etUSUnitHeightFeet.text!!.clear()
        etUsUnitHeightInch.text!!.clear()

        tilUsUnitWeight.visibility = View.VISIBLE
        llUsUnitsHeight.visibility = View.VISIBLE

        llDisplayBMIResult.visibility = View.INVISIBLE
    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibileView = METRIC_UNITS_VIEW
        tilMetricUnitWeight.visibility = View.VISIBLE
        tilMetricUnitHeight.visibility = View.VISIBLE

        etMetricUnitHeight.text!!.clear()
        etMetricUnitWeight.text!!.clear()

        tilUsUnitWeight.visibility = View.GONE
        llUsUnitsHeight.visibility = View.GONE

        llDisplayBMIResult.visibility = View.INVISIBLE
    }

    private fun displayBMIResult(bmi: Float){
        val bmiLabel: String
        val bmiDescription: String

        if(bmi.compareTo(15f) <= 0){
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take care of you better! Eat more!"
        }else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0){
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take care of you better! Eat more!"
        }else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0){
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take care of you better! Eat more!"
        }else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0){
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of you better! Workout more!"
        }else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0){
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of you better! Workout more!"
        }else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0){
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "Oops! You really need to take care of you better! Act now!"
        }else{
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        llDisplayBMIResult.visibility = View.VISIBLE
//        tvYourBMI.visibility = View.VISIBLE
//        tvBMIValue.visibility = View.VISIBLE
//        tvBMIType.visibility = View.VISIBLE
//        tvBMIDescription.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        tvBMIValue.text = bmiValue
        tvBMIType.text = bmiLabel
        tvBMIDescription.text = bmiDescription

    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true

        if(etMetricUnitWeight.text.toString().isEmpty())
            isValid = false
        else if(etMetricUnitHeight.text.toString().isEmpty())
            isValid = false

        return isValid
    }

    private fun validateUsUnits(): Boolean{
        var isValid = true

        when {
            etUsUnitWeight.text.toString().isEmpty() -> isValid = false
            etUSUnitHeightFeet.text.toString().isEmpty() -> isValid = false
            etUsUnitHeightInch.text.toString().isEmpty() -> isValid = false
        }

        return isValid
    }
}