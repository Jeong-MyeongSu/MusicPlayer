package com.wjdaudtn.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.wjdaudtn.musicplayer.databinding.ActivityMainBinding
import com.wjdaudtn.musicplayer.recycler.VerticalAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity(),MusicService.OnMusicCompletionListener {
    override fun onMusicButton(item: MusicPlayer) {
//        runOnUiThread {
//            verticalAdapter.musicBtnChange(item)
//        }
        verticalAdapter.musicBtnChange(item) //7
    }

    //음악 플레이어 추상 클래스
    abstract class MusicPlayer(
        var id: Int,
        var title: String,
        var artists: String,
        var url:String,
        var isMusicPlaying:Boolean
    ){
        abstract fun musicPlay(item: MusicPlayer, position: Int)
        abstract fun btnChange(item: MusicPlayer, position: Int)
        abstract fun linkSeekBar(item: MusicPlayer, position: Int)
    }

    private lateinit var verticalAdapter: VerticalAdapter //VerticalAdapter 객체 생성
    private lateinit var verticalAdapterList:MutableList<MusicPlayer> //VerticalAdapter로 들어갈 음악 플레이어 리스트
    private var adapterPosition: Int = -1 //바텀 내비게이션에서 어떤 음악을 틀고 있는지 flag
    private lateinit var job: Job //코루틴

    private lateinit var musicIntent: Intent //music service 객체 생성
    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as MusicService.LocalBinder
            musicService = binder.getService() //이게 서비스의 인스턴스를 초기화 하는건가?
            musicService?.onMusicCompletionListener = this@MainActivity
            isBound = true

        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicIntent = Intent(this, MusicService::class.java)
        startService(musicIntent) //music service를 실행한다.
        bindService(musicIntent, connection, Context.BIND_AUTO_CREATE)

        verticalAdapterList = mutableListOf() //리사이클러 뷰에 들어갈 리스트 초기화
        getDatabase() //파이어베이스 데이터 베이스에 들어있는 데이터 만큼 리사이클러 뷰 생성하기.
        bottomNavigationInit()

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        //로그아웃
        MyApplication.auth.signOut() //내 앱에 인증되었던 정보 signOut()
        MyApplication.email = null //내 앱에 인증되었던 이메일 null
        stopService(musicIntent) //music Service를 종료한다.
        unbindService(connection)
    }

    private fun getDatabase(){
        MyApplication.db.collection("music") //music 컬랙션 //이게 비동기식이라 나중에 실행되네
            .get()//가져오기
            .addOnSuccessListener {result -> //데이터 가져오기 성공

                for(document in result){ //문서의 갯수 만큼 리스트 추가
                    verticalAdapterList.add(musicPlayerInit(document))
                }
                verticalAdapter = VerticalAdapter(verticalAdapterList, object: VerticalAdapter.VerticalAdapterButtonClickListener{
                    override fun startMusic(item:MusicPlayer,position:Int) {
                        musicService?.makeMusic(item,position) //2
                    }

                    override fun stopMusic(item:MusicPlayer,position:Int) {
                        musicService?.stopMusic(item,position)
                    }

                    override fun pauseMusic(item:MusicPlayer, position:Int) {
                        musicService?.startPauseMusic(item,position)
                    }
                },this) //리사이클러뷰 어댑터 초기화
                binding.verticalRecyclerview.adapter = verticalAdapter
                binding.verticalRecyclerview.layoutManager = LinearLayoutManager(baseContext)

            }
            .addOnFailureListener{exception -> //데이터 가져오기 실패 콜백
                Log.d("makeRecyclerView", "error.. getting document..", exception)
                Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bottomNavigationInit(){
        binding.btnBottomNavigationStartAndPause.setOnClickListener {
            if (::verticalAdapter.isInitialized && adapterPosition != -1) {
                val currentItem = verticalAdapterList[adapterPosition]
                musicService?.startPauseMusic(currentItem, adapterPosition)
            }
        }

        binding.btnBottomNavigationStop.setOnClickListener {
            if (::verticalAdapter.isInitialized && adapterPosition != -1) {
                val currentItem = verticalAdapterList[adapterPosition]
                musicService?.stopMusic(currentItem, adapterPosition)
            }
        }
    }
    private fun musicPlayerInit(document: QueryDocumentSnapshot):MusicPlayer{
        // Firebase 문서에서 필요한 필드 값을 가져옵니다.

        return object : MusicPlayer(
            id = document.getLong("id")?.toInt() ?: 0,
            title = document.getString("title") ?: "Unknown Title",
            artists = document.getString("artist") ?: "Unknown Artist",
            url = document.getString("url") ?: "",
            isMusicPlaying = false
        ) {
            init {
                Log.d("MusicPlayerInit", "id: $id, title: $title, artists: $artists, url: $url")
            }
            override fun musicPlay(item: MusicPlayer, position: Int) {
                Log.d("", item.toString())
                binding.bottomNavigationTitleText.text = item.title
                binding.bottomNavigationArtisText.text = item.artists
                binding.btnBottomNavigationStartAndPause.background =
                    ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
                adapterPosition = position
            }

            override fun btnChange(item: MusicPlayer,position: Int) {
                val isMusicOn: Boolean = item.isMusicPlaying
                val resourceIcon = if (isMusicOn) R.drawable.pause else R.drawable.right_button_2
                binding.btnBottomNavigationStartAndPause.background =
                    ContextCompat.getDrawable(baseContext, resourceIcon)
            }

            override fun linkSeekBar(item: MusicPlayer,position: Int) {
                Log.d("linkSeekbar", "1")
                seekBarConnection(item)
            }
        }
    }
    private fun seekBarConnection(item: MusicPlayer) {
        binding.customSeekBar.max = musicService!!.music.duration
        updateSeekBar(item)
        binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.music.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun updateSeekBar(item: MusicPlayer) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (musicService!!.music.isPlaying) {
                    binding.customSeekBar.progress = musicService!!.music.currentPosition
                } else {
                    binding.customSeekBar.progress = musicService!!.music.currentPosition
                }
                delay(1_000)  // 1초마다 업데이트
            }
        }
    }
}