plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "pt.ipleiria.estg.dei.tripplan_android"
    compileSdk = 36

    defaultConfig {
        applicationId = "pt.ipleiria.estg.dei.tripplan_android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        buildFeatures {
            viewBinding = true
        }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Retrofit: Para fazer pedidos HTTP ao servidor
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson: Para converter o JSON em objetos Kotlin (e vice-versa)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Opcional: Para veres os logs do que é enviado (muito útil para debug)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // MQTT (Para o Messaging)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
}