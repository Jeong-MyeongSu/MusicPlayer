package com.wjdaudtn.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.wjdaudtn.musicplayer.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            // 메인 스레드에서 비동기식으로 3초 후 LoginActivity로 이동 후 현재 Activity 종료
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // SplashActivity를 종료하여 뒤로 가기 버튼으로 다시 돌아오지 않게 함
        }, 3000) // 3000ms = 3초

    }
}