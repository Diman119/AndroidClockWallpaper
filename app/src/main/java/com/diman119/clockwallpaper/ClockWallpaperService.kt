package com.diman119.clockwallpaper

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class ClockWallpaperService : WallpaperService() {

    override fun onCreateEngine() = WallpaperEngine()

    inner class WallpaperEngine : Engine() {
        private val updateIntervalMillis = 4000L

        private val painter = Painter(updateIntervalMillis)
        private val handler = Handler(Looper.getMainLooper())

        private var isVisible = false
        private var nextDrawTime = Long.MIN_VALUE

        private val iterationRunnable: Runnable = Runnable {
            if (isVisible && SystemClock.uptimeMillis() >= nextDrawTime) {
                drawAndScheduleNext()
            }
        }

        private val bootstrapRunnable: Runnable = Runnable {
            drawAndScheduleNext()
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            painter.onCreate(applicationContext)
        }

        override fun onDestroy() {
            super.onDestroy()
            isVisible = false
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            handler.post(bootstrapRunnable)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            handler.post(bootstrapRunnable)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (isVisible) {
                handler.post(bootstrapRunnable)
            }
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder) {
            super.onSurfaceRedrawNeeded(holder)
            drawAndScheduleNext()
        }

        private fun drawAndScheduleNext() {
            drawSynchronously(surfaceHolder)
            handler.postDelayed(iterationRunnable, updateIntervalMillis)
        }

        private fun drawSynchronously(holder: SurfaceHolder) {
            nextDrawTime = SystemClock.uptimeMillis() + updateIntervalMillis

            var c: Canvas? = null
            try {
                c = holder.lockCanvas()
                c?.let {
                    painter.draw(c)
                }
            } finally {
                c?.let {
                    holder.unlockCanvasAndPost(it)
                }
            }
        }
    }
}
