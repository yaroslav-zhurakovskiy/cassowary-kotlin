plugins {
    kotlin("multiplatform").version("1.4-M3")
}

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

kotlin {
    jvm()
    js {
        browser {
            binaries.executable()
            dceTask {
                enabled = false
            }
            testTask {
                useMocha {
                    timeout = "30000"
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                dependsOn(commonMain)

                implementation(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                dependsOn(jsMain)

                implementation(kotlin("test-js"))
            }
        }

        val jvmMain by getting {
            dependencies {
                dependsOn(commonMain)

                implementation(kotlin("stdlib-jdk7"))
            }
        }

        val jvmTest by getting {
            dependencies {
                dependsOn(jvmMain)
                dependsOn(commonTest)

                implementation(kotlin("test-junit"))
            }
        }
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }
}
