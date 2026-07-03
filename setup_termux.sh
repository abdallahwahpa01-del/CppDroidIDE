#!/data/data/com.termux/files/usr/bin/bash
# ================================================
# CppDroid IDE - Termux Setup Script
# Galaxy Tab A (2016) | ARM64 | Android 8.1
# ================================================

echo "╔══════════════════════════════════════╗"
echo "║     CppDroid IDE - Termux Setup      ║"
echo "║     ARM64 | Android 8.1              ║"
echo "╚══════════════════════════════════════╝"
echo ""

# Update packages
echo "[1/8] Updating package list..."
pkg update -y && pkg upgrade -y

# Core build tools
echo "[2/8] Installing build tools..."
pkg install -y clang cmake make git pkg-config

# C++ Standard Libraries
echo "[3/8] Installing core C++ libs..."
pkg install -y libstdc++ libc++

# Graphics
echo "[4/8] Installing graphics libraries..."
pkg install -y sdl2 sdl2-image sdl2-mixer sdl2-ttf
pkg install -y mesa

# Game Development
echo "[5/8] Installing game dev libraries..."
pkg install -y raylib
pkg install -y box2d

# Utility Libraries
echo "[6/8] Installing utility libraries..."
pkg install -y libcurl
pkg install -y openssl
pkg install -y zlib
pkg install -y freetype
pkg install -y libpng
pkg install -y libjpeg-turbo
pkg install -y sqlite
pkg install -y libfmt
pkg install -y spdlog

# Header-only libraries
echo "[7/8] Installing header-only libraries..."
pkg install -y nlohmann-json
pkg install -y eigen
pkg install -y glm

# Setup workspace
echo "[8/8] Setting up workspace..."
mkdir -p ~/cppdroid/projects
mkdir -p ~/cppdroid/builds
mkdir -p ~/cppdroid/include

# Create test file
cat > ~/cppdroid/test.cpp << 'EOF'
#include <iostream>
#include <SDL2/SDL.h>

int main(int argc, char* argv[]) {
    std::cout << "CppDroid IDE Ready!" << std::endl;
    std::cout << "SDL2 Version: " 
              << SDL_MAJOR_VERSION << "."
              << SDL_MINOR_VERSION << "."
              << SDL_PATCHLEVEL << std::endl;
    return 0;
}
EOF

# Test compilation
echo ""
echo "Testing compilation..."
clang++ ~/cppdroid/test.cpp -lSDL2 -o ~/cppdroid/test_build
if [ $? -eq 0 ]; then
    echo "✓ Compilation test PASSED"
    ~/cppdroid/test_build
else
    echo "✗ Compilation test FAILED"
fi

# Allow external apps (required for APK integration)
echo ""
echo "⚠️  IMPORTANT: Enable 'Allow External Apps' in Termux:"
echo "   Termux Settings → Allow External Apps → Enable"
echo ""
echo "✅ Setup Complete!"
echo "   Workspace: ~/cppdroid/"
echo "   Build: clang++ main.cpp -std=c++17 -lSDL2 -o output"
