import com.android.build.api.dsl.Packaging
import com.android.build.gradle.internal.dsl.PackagingOptions
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ohmshantiapps"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ohmshantiapps"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }

    buildFeatures {
        viewBinding =true
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.google.firebase.iid)
  
    //CircularView
    implementation (libs.circleimageview)
    //Picasso
    implementation (libs.picasso)
    implementation (libs.volley)
    implementation (libs.gson)
    //Alert
    implementation ("com.tapadoo.android:alerter:4.0.2")
    //Toast
    implementation ("com.muddzdev:styleabletoast:1.0.5")

    implementation ("com.agrawalsuneet.androidlibs:dotsloader:1.4")

    //CircularView
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.rishabhharit.roundedimageview:RoundedImageView:0.8.4")

    implementation ("com.github.shts:StoriesProgressView:3.0.0")
    implementation ("com.github.pedromassango:doubleClick:3.0")

    implementation ("com.github.danylovolokh:hashtag-helper:1.1.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("com.google.android.gms:play-services-ads:23.0.0")

    implementation(libs.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation("androidx.media3:media3-exoplayer-hls:1.0.0")
    implementation(libs.androidx.media3.ui)
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation (libs.converter.gson)
    implementation ("com.squareup.retrofit2:converter-scalars:2.1.0")
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("com.amazonaws:aws-android-sdk-s3:2.16.12")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("androidx.room:room-runtime:2.2.5")
    annotationProcessor ("androidx.room:room-compiler:2.2.5")
    implementation ("com.mikhaellopez:circularimageview:3.2.0")
    implementation ("com.balysv:material-ripple:1.0.2")
    implementation ("com.github.1902shubh:SendMail:1.0.0")
    implementation ("com.github.mukeshsolanki:android-otpview-pinview:2.1.2")
    implementation ("com.github.sharish:ShimmerRecyclerView:v1.3")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.devlomi:circularstatusview:1.0.1")
    implementation ("com.google.firebase:firebase-config")
    implementation ("com.github.pgreze:android-reactions:1.3")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.devlomi:circularstatusview:1.0.1")
    implementation("com.github.OMARIHAMZA:StoryView:1.0.2-alpha")










}



