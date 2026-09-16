package com.wokkeyboards.app

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_EXTRA_PADDING = "extra_bottom_padding_dp"
        const val KEY_COMPACT_MODE = "compact_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val seekBar = findViewById<SeekBar>(R.id.seekBarPadding)
        val label = findViewById<TextView>(R.id.labelPaddingValue)
        val compactSwitch = findViewById<Switch>(R.id.switchCompact)

        val currentPadding = prefs.getInt(KEY_EXTRA_PADDING, 0)
        seekBar.max = 150
        seekBar.progress = currentPadding
        label.text = getString(R.string.padding_value_format, currentPadding)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                label.text = getString(R.string.padding_value_format, progress)
                prefs.edit().putInt(KEY_EXTRA_PADDING, progress).apply()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        compactSwitch.isChecked = prefs.getBoolean(KEY_COMPACT_MODE, false)
        compactSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_COMPACT_MODE, isChecked).apply()
        }
    }
}
