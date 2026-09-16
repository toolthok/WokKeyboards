package com.wokkeyboards.app

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo

class WokKeyboardService : InputMethodService(), KeyboardListener {

    private lateinit var keyboardView: WokKeyboardView

    override fun onCreateInputView(): View {
        keyboardView = WokKeyboardView(this)
        keyboardView.listener = this
        applySettings()
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        applySettings()
    }

    private fun applySettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val extraPaddingDp = prefs.getInt(SettingsActivity.KEY_EXTRA_PADDING, 0)
        val compact = prefs.getBoolean(SettingsActivity.KEY_COMPACT_MODE, false)
        val density = resources.displayMetrics.density
        val navBarPx = getNavigationBarHeight()
        keyboardView.setExtraBottomPadding(navBarPx + (extraPaddingDp * density).toInt())
        keyboardView.setCompactMode(compact)
    }

    private fun getNavigationBarHeight(): Int {
        return try {
            val insets = window?.window?.decorView?.rootWindowInsets
            if (Build.VERSION.SDK_INT >= 30 && insets != null) {
                insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            } else {
                val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resId > 0) resources.getDimensionPixelSize(resId) else 0
            }
        } catch (e: Exception) {
            0
        }
    }

    override fun onCharKey(char: String, metaState: Int) {
        if (metaState == 0) {
            currentInputConnection?.commitText(char, 1)
        } else {
            sendMetaKeyEvent(charToKeyCode(char), metaState, char)
        }
    }

    override fun onSpecialKey(keyCode: Int, metaState: Int) {
        sendMetaKeyEvent(keyCode, metaState, null)
    }

    override fun onBackspace() {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
    }

    override fun onEnter() {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }

    override fun onSpace(metaState: Int) {
        if (metaState == 0) {
            currentInputConnection?.commitText(" ", 1)
        } else {
            sendMetaKeyEvent(KeyEvent.KEYCODE_SPACE, metaState, null)
        }
    }

    override fun onModifierChanged() {
        // View redraws itself; nothing needed here.
    }

    private fun sendMetaKeyEvent(keyCode: Int, metaState: Int, fallbackChar: String?) {
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            if (fallbackChar != null) currentInputConnection?.commitText(fallbackChar, 1)
            return
        }
        val ic = currentInputConnection ?: return
        val now = System.currentTimeMillis()
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, metaState))
    }

    private fun charToKeyCode(char: String): Int {
        return when (char.lowercase()) {
            "a" -> KeyEvent.KEYCODE_A
            "b" -> KeyEvent.KEYCODE_B
            "c" -> KeyEvent.KEYCODE_C
            "d" -> KeyEvent.KEYCODE_D
            "e" -> KeyEvent.KEYCODE_E
            "f" -> KeyEvent.KEYCODE_F
            "g" -> KeyEvent.KEYCODE_G
            "h" -> KeyEvent.KEYCODE_H
            "i" -> KeyEvent.KEYCODE_I
            "j" -> KeyEvent.KEYCODE_J
            "k" -> KeyEvent.KEYCODE_K
            "l" -> KeyEvent.KEYCODE_L
            "m" -> KeyEvent.KEYCODE_M
            "n" -> KeyEvent.KEYCODE_N
            "o" -> KeyEvent.KEYCODE_O
            "p" -> KeyEvent.KEYCODE_P
            "q" -> KeyEvent.KEYCODE_Q
            "r" -> KeyEvent.KEYCODE_R
            "s" -> KeyEvent.KEYCODE_S
            "t" -> KeyEvent.KEYCODE_T
            "u" -> KeyEvent.KEYCODE_U
            "v" -> KeyEvent.KEYCODE_V
            "w" -> KeyEvent.KEYCODE_W
            "x" -> KeyEvent.KEYCODE_X
            "y" -> KeyEvent.KEYCODE_Y
            "z" -> KeyEvent.KEYCODE_Z
            "0" -> KeyEvent.KEYCODE_0
            "1" -> KeyEvent.KEYCODE_1
            "2" -> KeyEvent.KEYCODE_2
            "3" -> KeyEvent.KEYCODE_3
            "4" -> KeyEvent.KEYCODE_4
            "5" -> KeyEvent.KEYCODE_5
            "6" -> KeyEvent.KEYCODE_6
            "7" -> KeyEvent.KEYCODE_7
            "8" -> KeyEvent.KEYCODE_8
            "9" -> KeyEvent.KEYCODE_9
            "," -> KeyEvent.KEYCODE_COMMA
            "." -> KeyEvent.KEYCODE_PERIOD
            else -> KeyEvent.KEYCODE_UNKNOWN
        }
    }
}
