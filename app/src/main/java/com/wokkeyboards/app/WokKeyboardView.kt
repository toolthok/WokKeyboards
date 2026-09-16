package com.wokkeyboards.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

enum class KeyType {
    CHAR, BACKSPACE, ENTER, SPACE, SHIFT, CTRL, ALT,
    MODE_SYMBOLS, MODE_LETTERS, MODE_NUMBERS,
    TAB, ESC, FN_TOGGLE,
    ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
    HOME, END, PAGE_UP, PAGE_DOWN,
    FUNCTION, COMMA, PERIOD
}

enum class KeyboardMode { LETTERS, SYMBOLS, NUMBERS }

data class Key(
    val label: String,
    val type: KeyType,
    val code: Int = 0,
    val weight: Float = 1f
)

interface KeyboardListener {
    fun onCharKey(char: String, metaState: Int)
    fun onSpecialKey(keyCode: Int, metaState: Int)
    fun onBackspace()
    fun onEnter()
    fun onSpace(metaState: Int)
    fun onModifierChanged()
}

/**
 * Modifier tap behaviour:
 *  - state 0 = off
 *  - state 1 = active-once (consumed after the next key press)
 *  - state 2 = locked (double-tap to enter/exit; stays on across many key presses,
 *              needed for sequences like Alt, D, F, F in Google Sheets)
 */
class WokKeyboardView(context: Context) : View(context) {

    var listener: KeyboardListener? = null

    var ctrlState = 0
        private set
    var altState = 0
        private set
    var shiftState = 0
        private set

    private var mode = KeyboardMode.LETTERS
    private var showFnRow = true // true = F1-F12 row, false = navigation row

    private var extraBottomPadding = 0
    private var compactMode = false

    private val bgPaint = Paint().apply { color = Color.parseColor("#2B2B2B") }
    private val keyPaint = Paint().apply { color = Color.parseColor("#3D3D3D") }
    private val keyActivePaint = Paint().apply { color = Color.parseColor("#FFC107") }
    private val keyLockedPaint = Paint().apply { color = Color.parseColor("#FF5722") }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var keyHeight = 0f
    private var rows: List<List<Key>> = emptyList()
    private val keyRects = mutableListOf<Pair<RectF, Key>>()

    private var lastAltTap = 0L
    private var lastCtrlTap = 0L
    private var lastShiftTap = 0L
    private val doubleTapWindowMs = 350L

    init {
        rebuildLayout()
    }

    fun setExtraBottomPadding(px: Int) {
        extraBottomPadding = px
        requestLayout()
        invalidate()
    }

    fun setCompactMode(enabled: Boolean) {
        compactMode = enabled
        requestLayout()
        invalidate()
    }

    private fun rebuildLayout() {
        rows = KeyboardLayouts.buildRows(mode, showFnRow)
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val rowCount = rows.size
        val baseKeyHeightDp = if (compactMode) 40f else 52f
        keyHeight = baseKeyHeightDp * resources.displayMetrics.density
        val totalHeight = (keyHeight * rowCount).toInt() + extraBottomPadding
        setMeasuredDimension(width, totalHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutKeys(w)
    }

    private fun layoutKeys(width: Int) {
        keyRects.clear()
        var y = 0f
        for (row in rows) {
            val totalWeight = row.sumOf { it.weight.toDouble() }.toFloat()
            var x = 0f
            for (key in row) {
                val keyWidth = width * (key.weight / totalWeight)
                keyRects.add(RectF(x, y, x + keyWidth, y + keyHeight) to key)
                x += keyWidth
            }
            y += keyHeight
        }
        textPaint.textSize = keyHeight * 0.34f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        for ((rect, key) in keyRects) {
            val margin = 3f
            val inner = RectF(rect.left + margin, rect.top + margin, rect.right - margin, rect.bottom - margin)
            val paint = when {
                key.type == KeyType.CTRL && ctrlState == 2 -> keyLockedPaint
                key.type == KeyType.CTRL && ctrlState == 1 -> keyActivePaint
                key.type == KeyType.ALT && altState == 2 -> keyLockedPaint
                key.type == KeyType.ALT && altState == 1 -> keyActivePaint
                key.type == KeyType.SHIFT && shiftState == 2 -> keyLockedPaint
                key.type == KeyType.SHIFT && shiftState == 1 -> keyActivePaint
                else -> keyPaint
            }
            canvas.drawRoundRect(inner, 10f, 10f, paint)
            val label = if (shiftState != 0 && key.type == KeyType.CHAR && key.label.length == 1)
                key.label.uppercase() else key.label
            canvas.drawText(
                label,
                inner.centerX(),
                inner.centerY() - (textPaint.ascent() + textPaint.descent()) / 2,
                textPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return true
        val touched = keyRects.firstOrNull { it.first.contains(event.x, event.y) } ?: return true
        handleKeyPress(touched.second)
        return true
    }

    private fun handleKeyPress(key: Key) {
        val now = System.currentTimeMillis()
        when (key.type) {
            KeyType.CTRL -> {
                ctrlState = if (now - lastCtrlTap < doubleTapWindowMs) {
                    if (ctrlState == 2) 0 else 2
                } else {
                    if (ctrlState == 0) 1 else 0
                }
                lastCtrlTap = now
                invalidate()
                listener?.onModifierChanged()
            }
            KeyType.ALT -> {
                altState = if (now - lastAltTap < doubleTapWindowMs) {
                    if (altState == 2) 0 else 2
                } else {
                    if (altState == 0) 1 else 0
                }
                lastAltTap = now
                invalidate()
                listener?.onModifierChanged()
            }
            KeyType.SHIFT -> {
                shiftState = if (now - lastShiftTap < doubleTapWindowMs) {
                    if (shiftState == 2) 0 else 2
                } else {
                    if (shiftState == 0) 1 else 0
                }
                lastShiftTap = now
                invalidate()
                listener?.onModifierChanged()
            }
            KeyType.MODE_SYMBOLS -> { mode = KeyboardMode.SYMBOLS; rebuildLayout() }
            KeyType.MODE_LETTERS -> { mode = KeyboardMode.LETTERS; rebuildLayout() }
            KeyType.MODE_NUMBERS -> { mode = KeyboardMode.NUMBERS; rebuildLayout() }
            KeyType.FN_TOGGLE -> { showFnRow = !showFnRow; rebuildLayout() }
            KeyType.BACKSPACE -> listener?.onBackspace()
            KeyType.ENTER -> listener?.onEnter()
            KeyType.SPACE -> {
                listener?.onSpace(currentMetaState())
                consumeOneShotModifiers()
            }
            KeyType.CHAR, KeyType.COMMA, KeyType.PERIOD -> {
                val label = if (shiftState != 0 && key.type == KeyType.CHAR && key.label.length == 1)
                    key.label.uppercase() else key.label
                val hasCommandModifier = ctrlState != 0 || altState != 0
                val meta = if (hasCommandModifier) currentMetaState() else 0
                listener?.onCharKey(label, meta)
                consumeOneShotModifiers()
            }
            KeyType.FUNCTION, KeyType.TAB, KeyType.ESC,
            KeyType.ARROW_UP, KeyType.ARROW_DOWN, KeyType.ARROW_LEFT, KeyType.ARROW_RIGHT,
            KeyType.HOME, KeyType.END, KeyType.PAGE_UP, KeyType.PAGE_DOWN -> {
                listener?.onSpecialKey(key.code, currentMetaState())
                consumeOneShotModifiers()
            }
        }
    }

    private fun currentMetaState(): Int {
        var meta = 0
        if (ctrlState != 0) meta = meta or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        if (altState != 0) meta = meta or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        if (shiftState != 0) meta = meta or KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
        return meta
    }

    private fun consumeOneShotModifiers() {
        if (ctrlState == 1) ctrlState = 0
        if (altState == 1) altState = 0
        if (shiftState == 1) shiftState = 0
        invalidate()
    }
}
