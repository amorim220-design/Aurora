package com.example.game.engine

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

/**
 * GLSurfaceView configurado para OpenGL ES 3.0.
 * Captura arrasto de toque na metade direita da tela para controle orbital da câmera em terceira pessoa.
 */
@SuppressLint("ClickableViewAccessibility")
class GameSurfaceView(
  context: Context,
  val renderer: Game3DRenderer
) : GLSurfaceView(context) {

  private var lastTouchX = 0f
  private var lastTouchY = 0f
  private var isDraggingCamera = false
  private var cameraTouchPointerId = -1

  init {
    setEGLContextClientVersion(3)
    setRenderer(renderer)
    renderMode = RENDERMODE_CONTINUOUSLY
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val pointerIndex = event.actionIndex
    val x = event.getX(pointerIndex)
    val y = event.getY(pointerIndex)
    val width = width.toFloat()

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
        // Apenas toques na metade direita da tela movimentam a câmera (metade esquerda é o analógico)
        if (x > width * 0.45f && cameraTouchPointerId == -1) {
          cameraTouchPointerId = event.getPointerId(pointerIndex)
          lastTouchX = x
          lastTouchY = y
          isDraggingCamera = true
          return true
        }
      }

      MotionEvent.ACTION_MOVE -> {
        if (isDraggingCamera && cameraTouchPointerId != -1) {
          val idx = event.findPointerIndex(cameraTouchPointerId)
          if (idx != -1) {
            val curX = event.getX(idx)
            val curY = event.getY(idx)
            val dx = curX - lastTouchX
            val dy = curY - lastTouchY

            val sensitivity = 0.22f
            renderer.cameraYaw = (renderer.cameraYaw - dx * sensitivity) % 360f
            renderer.cameraPitch = (renderer.cameraPitch - dy * sensitivity).coerceIn(-45f, 45f)

            lastTouchX = curX
            lastTouchY = curY
            return true
          }
        }
      }

      MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
        val id = event.getPointerId(pointerIndex)
        if (id == cameraTouchPointerId) {
          cameraTouchPointerId = -1
          isDraggingCamera = false
          return true
        }
      }
    }

    return super.onTouchEvent(event)
  }
}
