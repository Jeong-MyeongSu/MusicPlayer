plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.wjdaudtn.musicplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wjdaudtn.musicplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true // 많은 메서드를 사용 할 수 있게 한다. 보통 64000개 이상의 메서드는 제한 한다.
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    viewBinding{
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.firebase.bom))// 파이어 베이스 버전 관리 라이브러리
    //implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation(libs.firebase.analytics) //파이어베이스 Analytics(분석) 라이브러리 이벤트나 사용자 행동 추적
    //implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation(libs.firebase.auth.ktx) //파이어베이스 인증 라이브러리 ktx확장버전 로그인 및 사용자 인증관련된 기능 제공
    //implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation(libs.play.services.auth) //플레이 서비스 인증 라이브러리 google 로그인 앱 구현할 수 있도록 도움
    //implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(libs.androidx.multidex) // Android 프로젝트에서 멀티덱스를 지원하는 라이브러리 64k 메서드 제한을 초과 할 때 여러 덱스로 파일을 분할 하여 더 많은 메서드를 사용 할 수 있게함
    //implementation("androidx.multidex:multidex:2.0.1")
    implementation(libs.firebase.firestore.ktx) //파이어스토어  데이터베이스 라이브러리, NoSQL
    implementation(libs.firebase.storage.ktx) //파이어베이스 파일 저장 기능 스토리지 라이브러리
    //implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
}