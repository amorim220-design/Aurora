package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sintetizador procedural de efeitos sonoros e hápticos para Projeto Aurora.
 * Gera áudios cinematográficos de fantasia sombria diretamente em PCM.
 */
class SoundManager(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.Default)
  private val sampleRate = 44100
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  fun playAttackSlash() {
    vibrate(40)
    scope.launch {
      // Swoosh metálico de lâmina cortando o ar
      val duration = 0.22f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val freq = 480f - (progress * 260f)
        val env = (1.0f - progress) * (1.0f - exp(-progress * 20f))
        val tone = sin(2.0 * PI * freq * t)
        val noise = (Random.nextFloat() * 2f - 1f) * 0.45f
        val sample = (tone * 0.55f + noise) * env * Short.MAX_VALUE * 0.7f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  fun playMagicBurst() {
    vibrate(75)
    scope.launch {
      // Explosão mística arcana: grave profundo + harmônico cintilante
      val duration = 0.55f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val env = (1.0f - progress) * (1.0f - exp(-progress * 15f))
        val subBass = sin(2.0 * PI * (90f - progress * 40f) * t) * 0.7f
        val crystal1 = sin(2.0 * PI * 880f * t) * 0.2f
        val crystal2 = sin(2.0 * PI * 1320f * t) * 0.15f
        val airNoise = (Random.nextFloat() * 2f - 1f) * 0.25f
        val sample = (subBass + crystal1 + crystal2 + airNoise) * env * Short.MAX_VALUE * 0.85f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  fun playDodge() {
    vibrate(25)
    scope.launch {
      // Rastro espectral rápido de esquiva
      val duration = 0.18f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val env = sin(progress * PI.toFloat())
        val noise = (Random.nextFloat() * 2f - 1f)
        val sweep = sin(2.0 * PI * (250f + progress * 300f) * t) * 0.4f
        val sample = (noise * 0.6f + sweep) * env * Short.MAX_VALUE * 0.5f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  fun playJump() {
    vibrate(20)
    scope.launch {
      // Impulso vertical com glide tonal
      val duration = 0.16f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val freq = 140f + (progress * 220f)
        val env = 1.0f - progress
        val sample = sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * 0.45f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  fun playLanding() {
    vibrate(50)
    scope.launch {
      // Impacto pesado contra o solo
      val duration = 0.25f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val env = (1.0f - progress) * (1.0f - exp(-progress * 25f))
        val lowThump = sin(2.0 * PI * 65f * t) * 0.8f
        val dirtCrunch = (Random.nextFloat() * 2f - 1f) * 0.35f
        val sample = (lowThump + dirtCrunch) * env * Short.MAX_VALUE * 0.75f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  fun playFootstep() {
    scope.launch {
      val duration = 0.08f
      val numSamples = (duration * sampleRate).toInt()
      val buffer = ShortArray(numSamples)
      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = t / duration
        val env = 1.0f - progress
        val noise = (Random.nextFloat() * 2f - 1f) * 0.4f
        val sample = noise * env * Short.MAX_VALUE * 0.25f
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }
      playPcm(buffer)
    }
  }

  private fun vibrate(durationMs: Long) {
    try {
      if (vibrator != null && vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(durationMs)
        }
      }
    } catch (_: Exception) {
      // Graceful fallback se sem permissão ou emulador sem motor háptico
    }
  }

  private fun playPcm(buffer: ShortArray) {
    try {
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      // Libera após reprodução
      track.setNotificationMarkerPosition(buffer.size)
      track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
        override fun onMarkerReached(t: AudioTrack?) {
          try {
            t?.stop()
            t?.release()
          } catch (_: Exception) {}
        }
        override fun onPeriodicNotification(t: AudioTrack?) {}
      })
    } catch (_: Exception) {
      // Ignora silenciosamente caso limite de tracks de áudio seja excedido
    }
  }
}
