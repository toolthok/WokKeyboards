package com.wokkeyboards.app

import android.view.KeyEvent

object KeyboardLayouts {

    fun buildRows(mode: KeyboardMode, showFnRow: Boolean): List<List<Key>> {
        val topRow = buildTopRow(showFnRow)
        val middleRows = when (mode) {
            KeyboardMode.LETTERS -> lettersRows()
            KeyboardMode.SYMBOLS -> symbolsRows()
            KeyboardMode.NUMBERS -> numbersRows()
        }
        val modifierRow = buildModifierRow(mode)
        return listOf(topRow) + middleRows + listOf(modifierRow)
    }

    private fun buildTopRow(showFnRow: Boolean): List<Key> {
        return if (showFnRow) {
            listOf(
                Key("Esc", KeyType.ESC, KeyEvent.KEYCODE_ESCAPE),
                Key("F1", KeyType.FUNCTION, KeyEvent.KEYCODE_F1),
                Key("F2", KeyType.FUNCTION, KeyEvent.KEYCODE_F2),
                Key("F3", KeyType.FUNCTION, KeyEvent.KEYCODE_F3),
                Key("F4", KeyType.FUNCTION, KeyEvent.KEYCODE_F4),
                Key("F5", KeyType.FUNCTION, KeyEvent.KEYCODE_F5),
                Key("F6", KeyType.FUNCTION, KeyEvent.KEYCODE_F6),
                Key("F7", KeyType.FUNCTION, KeyEvent.KEYCODE_F7),
                Key("F8", KeyType.FUNCTION, KeyEvent.KEYCODE_F8),
                Key("F9", KeyType.FUNCTION, KeyEvent.KEYCODE_F9),
                Key("F10", KeyType.FUNCTION, KeyEvent.KEYCODE_F10),
                Key("F11", KeyType.FUNCTION, KeyEvent.KEYCODE_F11),
                Key("F12", KeyType.FUNCTION, KeyEvent.KEYCODE_F12),
                Key("Nav", KeyType.FN_TOGGLE, weight = 1.2f)
            )
        } else {
            listOf(
                Key("Esc", KeyType.ESC, KeyEvent.KEYCODE_ESCAPE),
                Key("Home", KeyType.HOME, KeyEvent.KEYCODE_MOVE_HOME, weight = 1.3f),
                Key("End", KeyType.END, KeyEvent.KEYCODE_MOVE_END, weight = 1.3f),
                Key("PgUp", KeyType.PAGE_UP, KeyEvent.KEYCODE_PAGE_UP, weight = 1.3f),
                Key("PgDn", KeyType.PAGE_DOWN, KeyEvent.KEYCODE_PAGE_DOWN, weight = 1.3f),
                Key("<", KeyType.ARROW_LEFT, KeyEvent.KEYCODE_DPAD_LEFT),
                Key("^", KeyType.ARROW_UP, KeyEvent.KEYCODE_DPAD_UP),
                Key("v", KeyType.ARROW_DOWN, KeyEvent.KEYCODE_DPAD_DOWN),
                Key(">", KeyType.ARROW_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT),
                Key("F1-12", KeyType.FN_TOGGLE, weight = 1.4f)
            )
        }
    }

    private fun lettersRows(): List<List<Key>> {
        val row1 = "qwertyuiop".map { Key(it.toString(), KeyType.CHAR) }
        val row2 = "asdfghjkl".map { Key(it.toString(), KeyType.CHAR) }
        val row3 = listOf(Key("Shift", KeyType.SHIFT, weight = 1.6f)) +
            "zxcvbnm".map { Key(it.toString(), KeyType.CHAR) } +
            listOf(Key("Del", KeyType.BACKSPACE, weight = 1.6f))
        val row4 = listOf(
            Key("123", KeyType.MODE_NUMBERS, weight = 1.3f),
            Key("Tab", KeyType.TAB, KeyEvent.KEYCODE_TAB, weight = 1.3f),
            Key(",", KeyType.COMMA),
            Key("Space", KeyType.SPACE, weight = 3.5f),
            Key(".", KeyType.PERIOD),
            Key("Enter", KeyType.ENTER, weight = 1.6f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun symbolsRows(): List<List<Key>> {
        val row1 = "1234567890".map { Key(it.toString(), KeyType.CHAR) }
        val row2 = ("@#\$_&-+()".map { it.toString() } + "/").map { Key(it, KeyType.CHAR) }
        val row3 = listOf(Key("~[<", KeyType.MODE_NUMBERS, weight = 1.6f)) +
            "*\"':;!?".map { Key(it.toString(), KeyType.CHAR) } +
            listOf(Key("Del", KeyType.BACKSPACE, weight = 1.6f))
        val row4 = listOf(
            Key("ABC", KeyType.MODE_LETTERS, weight = 1.3f),
            Key("Tab", KeyType.TAB, KeyEvent.KEYCODE_TAB, weight = 1.3f),
            Key(",", KeyType.COMMA),
            Key("Space", KeyType.SPACE, weight = 3.5f),
            Key(".", KeyType.PERIOD),
            Key("Enter", KeyType.ENTER, weight = 1.6f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun numbersRows(): List<List<Key>> {
        val row1 = "1234567890".map { Key(it.toString(), KeyType.CHAR) }
        val row2 = "~`|=<>{}[]".map { Key(it.toString(), KeyType.CHAR) }
        val row3 = listOf(Key("!#1", KeyType.MODE_SYMBOLS, weight = 1.6f)) +
            "%^\u00a3\u00a5\u00a2\u00b0".map { Key(it.toString(), KeyType.CHAR) } +
            listOf(Key("Del", KeyType.BACKSPACE, weight = 1.6f))
        val row4 = listOf(
            Key("ABC", KeyType.MODE_LETTERS, weight = 1.3f),
            Key("Tab", KeyType.TAB, KeyEvent.KEYCODE_TAB, weight = 1.3f),
            Key(",", KeyType.COMMA),
            Key("Space", KeyType.SPACE, weight = 3.5f),
            Key(".", KeyType.PERIOD),
            Key("Enter", KeyType.ENTER, weight = 1.6f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun buildModifierRow(mode: KeyboardMode): List<Key> {
        val modeKey = if (mode == KeyboardMode.LETTERS)
            Key("123", KeyType.MODE_NUMBERS, weight = 1.2f)
        else
            Key("ABC", KeyType.MODE_LETTERS, weight = 1.2f)
        return listOf(
            Key("Ctrl", KeyType.CTRL, weight = 1.4f),
            Key("Alt", KeyType.ALT, weight = 1.4f),
            modeKey,
            Key("<", KeyType.ARROW_LEFT, KeyEvent.KEYCODE_DPAD_LEFT),
            Key("^", KeyType.ARROW_UP, KeyEvent.KEYCODE_DPAD_UP),
            Key("v", KeyType.ARROW_DOWN, KeyEvent.KEYCODE_DPAD_DOWN),
            Key(">", KeyType.ARROW_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT)
        )
    }
}
