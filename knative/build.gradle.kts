plugins {
    kotlin("multiplatform") version "1.5.31"
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
                    println("create customNativeLib with path: $libPath for target: $target")
                    defFile(File(projectDir, "src/nativeInterop/cinterop/nativelib.def"))
                    compilerOpts.add("-I$libPath")
                    compilerOpts.add("-shared")
                }

                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "$libPath/manual_compiled_archives/Native_Interfaces.a"
                )
            }
        }

        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    val rpi2b = linuxArm32Hfp("raspberrypi2b")
    rpi2b.apply {

        compilations.getByName("main") {
            cinterops {

                val customNativeLibArm32 by creating {
                    println("create customNativeLib with path: $libPath for $target")
                    defFile(File(projectDir, "src/nativeInterop/cinterop/nativelib32.def"))
                    compilerOpts.add("-I$libPath")
                    includeDirs("$libPath", "$libPath/cmake-debug-build")

                }
            }

            kotlinOptions.freeCompilerArgs = listOf(
                "-include-binary", "$libPath/manual_compiled_archives/Native_Interfaces_RPI.a"
            )
        }
        binaries {
            executable("RPI") {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting
        //val nativeTest by getting
        val raspberrypi2bMain by getting
    }
}
