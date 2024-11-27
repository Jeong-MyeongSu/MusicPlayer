package com.wjdaudtn.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.wjdaudtn.musicplayer.databinding.ActivityLoginBinding
import com.wjdaudtn.musicplayer.util.setPermission

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var requestLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPermission(this)

        requestLauncher = registerForActivityResult(  //구글앱 인텐트 해서 돌아와 사후 처리 런처
            ActivityResultContracts.StartActivityForResult())
        {
            //구글 로그인 결과 처리.
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data) //구글 인텐트에서 돌아온 정보 데이터 객체
            try {
                val account = task.getResult(ApiException::class.java)!! //task의 결과를 apiexception 처리하여 account 변수에 할당 (계정 정보를 얻고자)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null) //firebase 인증에 사용할 OAuth 자격증명을 생성 (account.idToken : google로그인 성공 시 얻는 토큰, null : OAuth 비밀번호로 google인증에서는 필요하지 않아서 null)
                MyApplication.auth.signInWithCredential(credential) //Firebase 인증을 수행
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){ //인증 성공
                            MyApplication.email = account.email //로그인한 사용자 이메일을 저장
                            val intent = Intent(this,MainActivity::class.java) //MainActivity 스타트 하고 로그인 화면 종료
                            startActivity(intent)
                            finish()
                        }else {
                            Toast.makeText(baseContext, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }catch (e: ApiException){
                Toast.makeText(baseContext, "구글 로   그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){


        binding.btnSignUp.setOnClickListener { //회원가입 activity로 넘어감
            val intent = Intent(this,SigninActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            //이메일로 로그인
            val email = binding.editTextEmail.text.toString() //유저가 입력한 email
            val password = binding.editTextPassword.text.toString() //유저가 입력한 패스워드
            MyApplication.auth.signInWithEmailAndPassword(email, password)  //firebase authentication 에서 제공하는 이메일과 비밀번호 확인 하는 기능
                .addOnCompleteListener(this){ task ->
                    binding.editTextEmail.text.clear()  // 유저가 입력한 이메일 지움
                    binding.editTextPassword.text.clear() //유저가 입력한 패스 워드 지움
                    if(task.isSuccessful){ //로그인 성공시
                        if(MyApplication.checkAuth()){ //이메일 인증 되어 있으면
                            MyApplication.email = email //내 앱에서 로그인한 이메일 초기화
                            val intent = Intent(this,MainActivity::class.java) //MainActivity 스타트 하고 로그인 화면 종료
                            startActivity(intent)
                            finish()
                        }else { //이메일 인증 되어 있지 않으면
                            Toast.makeText(baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }else { //로그인 실패시
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.btnGoogle.setOnClickListener {
            //구글 로그인
            val gso = GoogleSignInOptions //구글 로그인을 구성하는 객체, 다양한 옵션 지정 할 수 있는 클래스
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //기본적인 Google 로그인 옵션을 사용하겠다고 지정하는 부분, DEFAULT_SIGN_IN은 기본 프로필 정보를 가져오도록 설정
                .requestIdToken(getString(R.string.default_web_client_id)) //requestIdToken은 OAuth 2.0 ID 토큰을 요청하는 메서드로, Google에서 로그인한 사용자의 고유한 ID 토큰을 받습니다.
                .requestEmail() //사용자의 이메일 주소를 요청합니다. 로그인에 성공한 후 GoogleSignInAccount 객체에서 이메일 주소에 접근할 수 있습니다.
                .build() //설정한 옵션으로 GoogleSignInOptions 객체를 생성
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent //구글 로그인 앱 인텐트
            requestLauncher.launch(signInIntent) //구글 인증 앱 실행
        }
    }
}