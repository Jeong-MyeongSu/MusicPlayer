package com.wjdaudtn.musicplayer.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 *packageName    : com.wjdaudtn.musicplayer.util
 * fileName       : MyMutil
 * author         : licen
 * date           : 2024-11-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-11-08        licen       최초 생성
 */
fun setPermission(activity: AppCompatActivity) {
    val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val internetGranted = permissions[Manifest.permission.INTERNET] ?: false
        val readExternalStorageGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        val accessNetworkStateGranted = permissions[Manifest.permission.ACCESS_NETWORK_STATE] ?: false
        val readMediaAudioGranted =  permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }] ?:false
        if(!internetGranted || !readExternalStorageGranted || !accessNetworkStateGranted || !readMediaAudioGranted){
            Toast.makeText(activity.applicationContext, "권한 거부됨", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
    }

    if (ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.INTERNET
        ) != PackageManager.PERMISSION_GRANTED ||
        (ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) ||
        ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.READ_MEDIA_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            )
        }else{
            //todo api 33 미만에서는 read_external_storage로 퉁치나 보다. 일단 34로만 테스트 해보고 만들자 33이상은 read audio 이하는 external storage 로 하면될듯?
            //API 33 미만 (Android 12 이하)
            //READ_EXTERNAL_STORAGE 권한을 통해 사용자가 디바이스에 저장된 모든 미디어 파일 (사진, 비디오, 오디오 등)에 접근할 수 있습니다. 이 권한 하나만으로 파일 접근이 가능했죠.
            //API 33 이상 (Android 13)
            //안드로이드 13부터는 보안과 개인정보 보호를 강화하기 위해 미디어 파일 접근 권한이 세분화되었습니다. 따라서 READ_EXTERNAL_STORAGE 권한이 더 이상 사용되지 않고, 다음의 세 가지로 나뉘어졌습니다:
            //READ_MEDIA_IMAGES: 이미지를 읽기 위한 권한
            //READ_MEDIA_VIDEO: 비디오를 읽기 위한 권한
            //READ_MEDIA_AUDIO: 오디오 파일을 읽기 위한 권한
            //즉, API 33 미만에서는 READ_EXTERNAL_STORAGE 권한을 사용하여 모든 미디어 파일에 접근할 수 있었지만, API 33 이상에서는 오디오 파일에 접근하려면 READ_MEDIA_AUDIO 권한을 별도로 요청해야 합니다.
            //
            //따라서, API 33 미만에서도 READ_EXTERNAL_STORAGE을 사용하여 오디오 파일에 접근할 수 있습니다. 하지만, API 33 이상에서는 READ_MEDIA_AUDIO 권한을 사용해야 오디오 파일에 접근할 수 있습니다.
        }
    }
}

interface MusicButtonClickListener{
    fun musicStart()
    fun musicStop()
    fun musicPause()
}