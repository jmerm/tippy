package com.merm.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var swSplit: Switch
    private lateinit var tvTotalLabel: TextView
    private var split: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        swSplit = findViewById(R.id.swSplit)
        tvTotalLabel = findViewById(R.id.tvTotalLabel)
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }

        })
        swSplit.setOnCheckedChangeListener {_, isChecked->
            if(isChecked){
                split = true
                tvTotalLabel.text = "Your Half"
                computeTipAndTotal()
            }
            else{
                split = false
                tvTotalLabel.text = "Total"
                computeTipAndTotal()
            }
        }

    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when(tipPercent) {
            in 0..9-> "Poor"
            in 10..14->"Acceptable"
            in 15..19->"Good"
            in 20..24->"Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
          tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.Poor),
            ContextCompat.getColor(this, R.color.Amazing),
        )as Int
        tvTipDescription.setTextColor(color)
        //Interpolation
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        //1. Get value of the base and tip percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        //2.Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        var totalAmount = (baseAmount + tipAmount)
        if (split)
        {
            totalAmount = (baseAmount + tipAmount)/2
        }

        //3.Update the UI
        tvTipAmount.text = "$%.2f".format(tipAmount)
        tvTotalAmount.text = "$%.2f".format(totalAmount)
    }
}