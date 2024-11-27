package com.wjdaudtn.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.wjdaudtn.musicplayer.recycler.VerticalAdapter

class MusicService : Service() {

    interface OnMusicCompletionListener {
        fun onMusicButton(item:MainActivity.MusicPlayer)
    }
    var onMusicCompletionListener: OnMusicCompletionListener? = null

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
    private val binder = LocalBinder()

    lateinit var music: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("MyService","onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService","onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    fun startPauseMusic(item:MainActivity.MusicPlayer,posision:Int){
        if (::music.isInitialized && !music.isPlaying) {
            music.start() //5
            item.isMusicPlaying = true
            item.musicPlay(item, posision)
            item.linkSeekBar(item, posision)

        } else {
            music.pause()
            item.isMusicPlaying = false
        }
        onMusicCompletionListener?.onMusicButton(item) //6
        item.btnChange(item, posision)
    }

    fun stopMusic(item:MainActivity.MusicPlayer, posision:Int){
        music.pause()
        music.seekTo(0) // 재설정
        item.isMusicPlaying = false
        onMusicCompletionListener?.onMusicButton(item)
        item.btnChange(item,posision)
        item.linkSeekBar(item,posision)
    }

    fun makeMusic(item:MainActivity.MusicPlayer, posision: Int){
        val musicTitle = item.title
        val musicRef = MyApplication.storage.reference.child("music/${musicTitle}.mp3")

        musicRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val musicUrl = task.result.toString()
                Log.d("MyService", "Music URL: $musicUrl")

                music = MediaPlayer().apply { //3
                    setDataSource(musicUrl)
                    prepareAsync()
                    setOnPreparedListener {
                        Log.d("MyService", "Music is ready to play")
                        startPauseMusic(item, posision) //4
                    }
                    setOnCompletionListener {
                        Log.d("MyService", "Music playback completed")
                        music.pause()
                        music.seekTo(0) // 재설정
                        item.isMusicPlaying = false
                        onMusicCompletionListener?.onMusicButton(item)
                        item.btnChange(item,posision)
                        item.linkSeekBar(item,posision)
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("MyService", "MediaPlayer Error: $what, $extra")
                        false
                    }
                }

            } else {
                Log.e("MyService", "Failed to get music URL", task.exception)
            }
        }
    }
}