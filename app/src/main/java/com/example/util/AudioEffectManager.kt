package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.media.AudioManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object AudioEffectManager {

  /**
   * Plays a pleasant cultural chord chime on app startup.
   * Synthesized in real-time via AudioTrack so it is 100% offline and requires no audio assets.
   */
  fun playAppOpenSound() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val sampleRate = 44100
        val durationSeconds = 0.8
        val numSamples = (durationSeconds * sampleRate).toInt()
        val generatedSnd = ShortArray(numSamples)

        // Pentatonic harmonious frequencies: 528Hz (Love/Peace chord), 660Hz (E5), 792Hz (G5)
        val f1 = 528.0
        val f2 = 660.0
        val f3 = 792.0

        for (i in 0 until numSamples) {
          val time = i.toDouble() / sampleRate
          // Smooth exponential decay envelope
          val envelope = kotlin.math.exp(-3.5 * time)
          val wave1 = sin(2.0 * Math.PI * f1 * time)
          val wave2 = 0.6 * sin(2.0 * Math.PI * f2 * time)
          val wave3 = 0.4 * sin(2.0 * Math.PI * f3 * time)
          val combined = (wave1 + wave2 + wave3) / 2.0 * envelope
          generatedSnd[i] = (combined * Short.MAX_VALUE * 0.7).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
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
          .setBufferSizeInBytes(generatedSnd.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
      } catch (e: Exception) {
        // Fallback tone generator
        try {
          val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
          toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
        } catch (_: Exception) {}
      }
    }
  }

  /**
   * Plays an alert chime when today's note reminder pops up.
   */
  fun playReminderBellSound(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        // First try system notification chime
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, notificationUri)
        if (ringtone != null) {
          ringtone.play()
          return@launch
        }
      } catch (_: Exception) {}

      // Fallback: Double chime bell sound
      try {
        val sampleRate = 44100
        val durationSeconds = 0.7
        val numSamples = (durationSeconds * sampleRate).toInt()
        val generatedSnd = ShortArray(numSamples)

        val freq = 880.0 // A5 Bell
        for (i in 0 until numSamples) {
          val time = i.toDouble() / sampleRate
          val envelope = kotlin.math.exp(-4.0 * (time % 0.35))
          val sample = sin(2.0 * Math.PI * freq * time) * envelope
          generatedSnd[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_NOTIFICATION)
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
          .setBufferSizeInBytes(generatedSnd.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
      } catch (_: Exception) {}
    }
  }
}
