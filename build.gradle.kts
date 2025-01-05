import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("signing")
}

android {
    namespace = "io.github.xpler"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    compileOnly(files("libs/api-82.jar"))
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
}

mavenPublishing {
    coordinates("io.github.thatworld", "xpler", "0.0.2")
    pom {
        name.set("xpler")
        description.set("Xpler is a library for Xposed")
        url.set("https://github.com/ThatWorld/xpler")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                name.set("Gang")
                url.set("https://github.com/ThatWorld/xpler")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/ThatWorld/xpler.git")
            developerConnection.set("scm:git:ssh://github.com/ThatWorld/xpler.git")
            url.set("https://github.com/ThatWorld/xpler.git")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}