package com.wjdaudtn.musicplayer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wjdaudtn.musicplayer.databinding.ActivitySinginBinding

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySinginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        binding.btnAuthEmail.setOnClickListener {
            //이메일로 회원가입
            val email = binding.editTextSigninEmail.text.toString() //유저가 입력한 email
            val password = binding.editTextSigninPassword.text.toString() //유저가 입력한 패스워드
            MyApplication.auth.createUserWithEmailAndPassword(email, password) //firebase authentication 에서 제공하는 이메일과 비밀번호 신규 사용자를 생성하는 기능
                .addOnCompleteListener(this) { task -> // 생성 성공 리스너
                    binding.editTextSigninEmail.text.clear() // 유저가 입력한 이메일 지움
                    binding.editTextSigninPassword.text.clear() //유저가 입력한 패스 워드 지움
                    if(task.isSuccessful){ //회원가입 성공시
                        MyApplication.auth.currentUser?.sendEmailVerification() //firebase 검증 메일 발송
                            ?.addOnCompleteListener{ sendTask ->
                                if(sendTask.isSuccessful){ // 메일 발송 성공시
                                    Toast.makeText(baseContext, "회원 가입 성공, 전송된 메일을 확인해 주세요",
                                        Toast.LENGTH_SHORT).show()
                                    finish()
                                }else { //메일 발송 실패시
                                    Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_LONG).show()
                                    finish()
                                }
                            }
                    }else { //회원 가입 실패시
                        Toast.makeText(baseContext, "회원 가입 실패", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
        }
    }
}