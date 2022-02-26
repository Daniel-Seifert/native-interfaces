plugins {
    kotlin("multiplatform") version "1.5.10"
}

group = "me.toni"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val libPath = "${projectDir.path}/../c-library"
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                // create klib (0_nativelib.knm) to call C-Functions
                val customNativeLib by creating {
                    println("create customNativeLib with path: $libPath")
                    defFile(File(projectDir, "src/nativeInterop/cinterop/nativelib.def"))
                    compilerOpts.add("-I$libPath")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "$libPath/cmake-build-debug/libNative_Interfaces.a"
                )
            }
        }

        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}
