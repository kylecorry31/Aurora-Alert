import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    compileSdk = 34

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "com.kylecorry.aurora_alert"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packagingOptions {
        resources.merges += "META-INF/LICENSE.md"
        resources.merges += "META-INF/LICENSE-notice.md"
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        abortOnError = false
    }
    namespace = "com.kylecorry.aurora_alert"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-service:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("com.google.android.material:material:1.10.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Room
    val roomVersion = "2.6.1"
    ksp("androidx.room:room-compiler:${roomVersion}")
    implementation("androidx.room:room-runtime:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")

    // Sol
    implementation("com.github.kylecorry31:sol:8.0.1")

    // Space weather
    implementation("com.github.kylecorry31:noaa-aurora:9d1d5778f1")

    // Andromeda
    val andromedaVersion = "50983ef77d"
    implementation("com.github.kylecorry31.andromeda:core:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:fragments:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:exceptions:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:preferences:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:permissions:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:notify:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:alerts:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:pickers:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:list:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:files:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:canvas:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:background:$andromedaVersion")

    // Ceres
    val ceresVersion = "e45e6958fb"
    implementation("com.github.kylecorry31.ceres:list:$ceresVersion")
    implementation("com.github.kylecorry31.ceres:toolbar:$ceresVersion")
    implementation("com.github.kylecorry31.ceres:chart:$ceresVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    ksp("com.google.dagger:hilt-android-compiler:2.49")

    // Hilt for Jetpack components
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("org.junit.platform:junit-platform-runner:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

// This is a workaround to get viewbinding to work with ksp + hilt (https://github.com/google/dagger/issues/4097)
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                val dataBindingTask =
                    project.tasks.getByName("dataBindingGenBaseClasses" + variant.name.capitalized()) as DataBindingGenBaseClassesTask
                (this as AbstractKotlinCompileTool<*>).setSource(dataBindingTask.sourceOutFolder)
            }
        }
    }
}