package com.cppdroid.ide.compiler

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class CompileResult(
    val success: Boolean,
    val output: String,
    val errors: List<CompileError> = emptyList(),
    val executionTime: Long = 0
)

data class CompileError(
    val line: Int,
    val column: Int,
    val message: String,
    val severity: ErrorSeverity
)

enum class ErrorSeverity { ERROR, WARNING, NOTE }

// C++ Libraries available via Termux pkg install
enum class CppLibrary(val pkgName: String, val linkFlag: String, val description: String) {
    SDL2("sdl2", "-lSDL2", "Window & Input (SDL2)"),
    SDL2_MIXER("sdl2-mixer", "-lSDL2_mixer", "Audio (SDL2 Mixer)"),
    SDL2_IMAGE("sdl2-image", "-lSDL2_image", "Images (SDL2 Image)"),
    OPENGL("mesa", "-lGL", "OpenGL"),
    OPENGL_ES("mesa", "-lGLESv2", "OpenGL ES 3.1"),
    BOX2D("box2d", "-lbox2d", "2D Physics (Box2D)"),
    BULLET("bullet", "-lBulletDynamics -lBulletCollision -lLinearMath", "3D Physics (Bullet)"),
    RAYLIB("raylib", "-lraylib", "Game Framework (Raylib)"),
    BOOST("boost", "", "Boost Libraries"),
    SQLITE("sqlite", "-lsqlite3", "SQLite Database"),
    CURL("curl", "-lcurl", "HTTP Client (libcurl)"),
    ZLIB("zlib", "-lz", "Compression (zlib)"),
    OPENSSL("openssl", "-lssl -lcrypto", "Cryptography (OpenSSL)"),
    FREETYPE("freetype", "-lfreetype", "Font Rendering (FreeType)"),
    LIBPNG("libpng", "-lpng", "PNG Images"),
    LIBJPEG("libjpeg-turbo", "-ljpeg", "JPEG Images"),
    EIGEN("eigen", "", "Linear Algebra (Eigen, header-only)"),
    GLM("glm", "", "Math for OpenGL (GLM, header-only)"),
    FMT("libfmt", "-lfmt", "String Formatting (fmt)"),
    SPDLOG("spdlog", "-lspdlog", "Logging (spdlog)"),
    NLOHMANN_JSON("nlohmann-json", "", "JSON (header-only)"),
}

class TermuxCompiler(private val context: Context) {

    companion object {
        const val TERMUX_PACKAGE = "com.termux"
        const val TERMUX_SERVICE = "com.termux.app.TermuxService"
    }

    fun isTermuxInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(TERMUX_PACKAGE, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun compile(
        sourceFile: File,
        outputFile: File,
        standard: String = "c++17",
        libraries: List<CppLibrary> = emptyList(),
        extraFlags: String = ""
    ): CompileResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        // Build clang++ command
        val libFlags = libraries.joinToString(" ") { it.linkFlag }.trim()
        val command = buildString {
            append("clang++ ")
            append("-std=$standard ")
            append("-O2 ")
            append("-Wall ")
            append("-Wextra ")
            append("\"${sourceFile.absolutePath}\" ")
            append("-o \"${outputFile.absolutePath}\" ")
            if (libFlags.isNotEmpty()) append("$libFlags ")
            if (extraFlags.isNotEmpty()) append(extraFlags)
        }.trim()

        runTermuxCommand(command, startTime)
    }

    suspend fun run(executableFile: File): CompileResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        runTermuxCommand("\"${executableFile.absolutePath}\"", startTime)
    }

    suspend fun installLibrary(library: CppLibrary): CompileResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        runTermuxCommand("pkg install -y ${library.pkgName}", startTime)
    }

    suspend fun runCustomCommand(command: String): CompileResult = withContext(Dispatchers.IO) {
        runTermuxCommand(command, System.currentTimeMillis())
    }

    private fun runTermuxCommand(command: String, startTime: Long): CompileResult {
        return try {
            // Send command to Termux via Intent
            val intent = Intent().apply {
                setClassName(TERMUX_PACKAGE, "com.termux.app.RunCommandService")
                action = "com.termux.RUN_COMMAND"
                putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash")
                putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-c", command))
                putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
            }
            context.startService(intent)

            CompileResult(
                success = true,
                output = "Command sent to Termux: $command",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            CompileResult(
                success = false,
                output = "Error: ${e.message}\n\nMake sure Termux is installed and 'Allow External Apps' is enabled in Termux settings.",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }

    fun parseErrors(output: String): List<CompileError> {
        val errors = mutableListOf<CompileError>()
        val errorPattern = Regex(""".*:(\d+):(\d+):\s+(error|warning|note):\s+(.+)""")

        output.lines().forEach { line ->
            errorPattern.find(line)?.let { match ->
                val (lineNum, col, severity, message) = match.destructured
                errors.add(
                    CompileError(
                        line = lineNum.toIntOrNull() ?: 0,
                        column = col.toIntOrNull() ?: 0,
                        message = message,
                        severity = when (severity) {
                            "error" -> ErrorSeverity.ERROR
                            "warning" -> ErrorSeverity.WARNING
                            else -> ErrorSeverity.NOTE
                        }
                    )
                )
            }
        }
        return errors
    }

    // Generate CMakeLists.txt for project
    fun generateCMakeLists(
        projectName: String,
        sourceFiles: List<String>,
        libraries: List<CppLibrary>,
        standard: String = "17"
    ): String = buildString {
        appendLine("cmake_minimum_required(VERSION 3.20)")
        appendLine("project($projectName)")
        appendLine()
        appendLine("set(CMAKE_CXX_STANDARD $standard)")
        appendLine("set(CMAKE_CXX_STANDARD_REQUIRED ON)")
        appendLine()
        appendLine("# Source files")
        appendLine("add_executable($projectName")
        sourceFiles.forEach { appendLine("    $it") }
        appendLine(")")
        appendLine()

        if (libraries.isNotEmpty()) {
            appendLine("# Find and link libraries")
            libraries.forEach { lib ->
                when (lib) {
                    CppLibrary.SDL2 -> {
                        appendLine("find_package(SDL2 REQUIRED)")
                        appendLine("target_include_directories($projectName PRIVATE \${SDL2_INCLUDE_DIRS})")
                        appendLine("target_link_libraries($projectName \${SDL2_LIBRARIES})")
                    }
                    CppLibrary.OPENGL_ES -> {
                        appendLine("find_package(OpenGL REQUIRED)")
                        appendLine("target_link_libraries($projectName OpenGL::GL GLESv2)")
                    }
                    else -> {
                        if (lib.linkFlag.isNotEmpty()) {
                            appendLine("target_link_libraries($projectName ${lib.linkFlag.replace("-l", "")})")
                        }
                    }
                }
                appendLine()
            }
        }

        appendLine("# Compiler flags")
        appendLine("target_compile_options($projectName PRIVATE -Wall -Wextra -O2)")
    }
}
