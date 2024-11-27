package com.wjdaudtn.musicplayer

import android.annotation.SuppressLint
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

/**
 *packageName    : com.wjdaudtn.musicplayer
 * fileName       : MyApplication
 * author         : licen
 * date           : 2024-11-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-08        licen       최초 생성
 */
class MyApplication: MultiDexApplication(){ // API 21 이하에서 멀티덱스 지원을 위해 사용. API 21 이상에서는 Application으로 해도 무방함.
    companion object{  // checkAuth 함수를 앱 전역에서 단일 인스턴스로 사용하기 위해 companion object 사용
        lateinit var auth: FirebaseAuth //Firebase 인증 객체 생성
        var email: String? = null //이메일 객체 생성
        @SuppressLint("StaticFieldLeak")
        lateinit var db: FirebaseFirestore //파이어베이스 파이어스토어 데이터베이스 객체
        lateinit var storage: FirebaseStorage //파이어베이스 스토리지 객체
        fun checkAuth(): Boolean{
            val currentUser = auth.currentUser  // 인증되지 않으면 null, 인증되면 유저 데이터가 들어옴
            return currentUser?.let{
                email = currentUser.email // 인증된 사용자의 이메일로 초기화
                currentUser.isEmailVerified // 이메일 인증 여부를 리턴 (true/false)
            } ?: let{
                false // 인증 실패 시 false 리턴
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth // Firebase 로그인 인증 객체 초기화
        db = FirebaseFirestore.getInstance() // 파이어 베이스 데이터베이스 객체
        storage = FirebaseStorage.getInstance("gs://androidtest-9ec03.firebasestorage.app") //10월에 storage 정책이 변경 되서 그런지  gs://androidtest-9ec03.firebasestorage.app 명시적으로 안해주면 .appspot.com로 되는데 상호 호환되서 그대로 사용해도 된다고 chat gpt가 그러는데 안되서 12시간 정도 해맴
    }
}