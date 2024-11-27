package com.wjdaudtn.musicplayer.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.musicplayer.MainActivity
import com.wjdaudtn.musicplayer.R
import com.wjdaudtn.musicplayer.databinding.ItemVerticalBinding


/**
 *packageName    : com.wjdaudtn.musicplayer.recycler
 * fileName       : VerticalAdapter
 * author         : licen
 * date           : 2024-11-09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-09        licen       최초 생성
 */
class VerticalAdapter(
    private val musicList: MutableList<MainActivity.MusicPlayer>,
    private var verticalAdapterButtonClickListener: VerticalAdapterButtonClickListener,
    private val activity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //private val onButtonClick: (MainActivity.MusicPlayer) -> Unit

    private val tag = "VerticalAdapter";

    interface VerticalAdapterButtonClickListener {
        fun startMusic(item: MainActivity.MusicPlayer,position: Int)
        fun stopMusic(item: MainActivity.MusicPlayer,position: Int)
        fun pauseMusic(item: MainActivity.MusicPlayer,position: Int)
    }

    var currentMusicPosition: Int? = null
    var itemPosition: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder { //뷰 홀더를 만듬
        return VerticalHolder(
            ItemVerticalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { //뷰 홀더와 어댑터 연결
        (holder as VerticalHolder).bind(position)
    }

    override fun getItemCount(): Int { //아이템 갯수 리턴
        return musicList.size
    }

    inner class VerticalHolder(private val binding: ItemVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            musicList.let {
                val item = it[position] //musicList 를 포지션 별로 item 에 넣어줌
                binding.textItemVerticalTitle.text = item.title
                binding.textItemVerticalArtist.text = item.artists

                val resourceIcon =
                    if (item.isMusicPlaying) R.drawable.pause else R.drawable.right_button_2 //9
                binding.btnPlay.background =
                    ContextCompat.getDrawable(activity.baseContext, resourceIcon)

                binding.btnPlay.setOnClickListener {
                    if (currentMusicPosition != null) {
                        if (currentMusicPosition != position) {
                            //다른 포지션의 노래가 나오고 있어 다른 포지션의 노래를 끄고 현재 포지션의 노래를 켜야해
                            Log.d("현재 노래 포지션", "$currentMusicPosition")
                            verticalAdapterButtonClickListener.stopMusic(musicList[currentMusicPosition!!],position)
                            Log.d("다음 노래 포지션", "$position")
                            verticalAdapterButtonClickListener.startMusic(item,position)
                            currentMusicPosition = position
                        } else {
                            //현재 포지션의 노래가 나오고 있어 pause 해야해
                            verticalAdapterButtonClickListener.pauseMusic(item,position)
                        }
                    } else {
                        //노래가 안나오고 있어 켜야해
                        verticalAdapterButtonClickListener.startMusic(item,position) //1
                        currentMusicPosition = position
                        Log.d("현재 노래 포지션", "$currentMusicPosition")
                    }
                }
            }
        }
    }

    fun musicBtnChange(item: MainActivity.MusicPlayer) {
        notifyDataSetChanged() //8
    }

//    private fun startPlayer(item:MainActivity.MusicPlayer,position: Int){
//        verticalAdapterButtonClickListener.startMusic(item,position) //1
//        currentMusicPosition = position
//
//    }
//    private fun stopPlayer(item:MainActivity.MusicPlayer,position: Int){
//        Log.d("현재 노래 포지션", "$currentMusicPosition")
//        verticalAdapterButtonClickListener.stopMusic(musicList[currentMusicPosition!!],position)
//        Log.d("다음 노래 포지션", "$position")
//        verticalAdapterButtonClickListener.startMusic(item,position)
//        currentMusicPosition = position
//    }
//    private fun pausePlayer(item: MainActivity.MusicPlayer,position: Int){
//        verticalAdapterButtonClickListener.pauseMusic(item,position)
//
//    }
}