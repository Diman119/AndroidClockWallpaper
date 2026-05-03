package com.diman119.clockwallpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.DynamicLayout
import android.text.SpannableStringBuilder
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

class Painter(private val updateInterval: Long) {
    private val timePaint = TextPaint()
    private val datePaint = TextPaint()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE\nd MMMM")

    private val dateSB = SpannableStringBuilder()
    private val dateLayout = DynamicLayout.Builder
        .obtain(dateSB, datePaint, Int.MAX_VALUE)
        .build()

    private val widgetWidth = 956f / 1752f
    private val timeYPos = 328f / 1752f
    private val dateYPos = 194f / 1752f
    private val timeFontSize = 178f / 1752f
    private val dateFontSize = 53.5f / 1752f

    fun onCreate(context: Context) {
        val fontColor = context.resources.getColor(R.color.main_font)
        val timeFont = context.resources.getFont(R.font.oneui_clock_default_num_500)
        val dateFont = Typeface.create(context.resources.getFont(R.font.oneui_sans), 600, false)

        timePaint.color = fontColor
        timePaint.typeface = timeFont

        datePaint.color = fontColor
        datePaint.typeface = dateFont
    }

    fun draw(c: Canvas) {
        c.drawColor(Color.BLACK)

        val now = LocalDateTime.now()
            .plusNanos(updateInterval * 500000)  // improve accuracy 2x

        val minDimension = min(c.width, c.height)

        timePaint.textSize = timeFontSize * minDimension
        val timeString = timeFormatter.format(now)
        val timeWidth = timePaint.measureText(timeString)
        c.drawText(timeString, (c.width + widgetWidth * minDimension) * 0.5f - timeWidth, timeYPos * minDimension, timePaint)

        datePaint.textSize = dateFontSize * minDimension
        dateSB.clear()
        dateFormatter.formatTo(now, dateSB)
        c.withTranslation((c.width - widgetWidth * minDimension) * 0.5f, dateYPos * minDimension) {
            dateLayout.draw(c)
        }
    }
}