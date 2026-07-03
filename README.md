# CppDroid IDE
**C++ Development Environment for Android**
> Samsung Galaxy Tab A (2016) | ARM64 | Android 8.1 | OpenGL ES 3.1

---

## خطوات الإعداد الكاملة

### الخطوة 1: رفع المشروع على GitHub
```bash
git init
git add .
git commit -m "Initial CppDroid IDE"
git remote add origin https://github.com/YOUR_USERNAME/CppDroidIDE.git
git push -u origin main
```

### الخطوة 2: GitHub Actions يبني APK تلقائياً
- ادخل على GitHub → Actions
- سترى workflow يعمل تلقائياً
- بعد الانتهاء → Artifacts → حمّل APK

### الخطوة 3: تثبيت Termux على جهازك
1. حمّل Termux من [F-Droid](https://f-droid.org/packages/com.termux/)
   > ⚠️ لا تستخدم نسخة Google Play (قديمة)
2. افتح Termux وشغّل:
```bash
curl -sL https://raw.githubusercontent.com/YOUR_USERNAME/CppDroidIDE/main/setup_termux.sh | bash
```

### الخطوة 4: تفعيل التكامل
في Termux:
```
Settings → Allow External Apps → Enable
```

### الخطوة 5: تثبيت APK
- انقل APK للجهاز وثبّته
- افتح CppDroid IDE
- ابدأ البرمجة!

---

## المكتبات المدعومة

| المكتبة | الاستخدام | التثبيت |
|--------|---------|--------|
| SDL2 | نافذة + إدخال لمس | `pkg install sdl2` |
| OpenGL ES 3.1 | رسوميات | مدمج في Mesa |
| Raylib | محرك ألعاب | `pkg install raylib` |
| Box2D | فيزياء 2D | `pkg install box2d` |
| SDL2_mixer | صوت | `pkg install sdl2-mixer` |
| GLM | رياضيات | `pkg install glm` |
| nlohmann/json | JSON | `pkg install nlohmann-json` |
| fmt | formatting | `pkg install libfmt` |
| spdlog | logging | `pkg install spdlog` |

---

## مثال: كود لعبة بسيطة

```cpp
#include <SDL2/SDL.h>
#include <GLES3/gl3.h>
#include <iostream>

int main(int argc, char* argv[]) {
    SDL_Init(SDL_INIT_VIDEO);
    
    SDL_Window* window = SDL_CreateWindow(
        "My Game", 0, 0, 1280, 800,
        SDL_WINDOW_OPENGL | SDL_WINDOW_FULLSCREEN
    );
    
    SDL_GLContext ctx = SDL_GL_CreateContext(window);
    
    bool running = true;
    while (running) {
        SDL_Event e;
        while (SDL_PollEvent(&e)) {
            if (e.type == SDL_QUIT) running = false;
            if (e.type == SDL_FINGERDOWN) {
                // Touch input
                float x = e.tfinger.x * 1280;
                float y = e.tfinger.y * 800;
            }
        }
        
        glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        SDL_GL_SwapWindow(window);
    }
    
    SDL_Quit();
    return 0;
}
```

بناء:
```bash
clang++ main.cpp -std=c++17 -lSDL2 -lGLESv2 -o game
./game
```

---

## هيكل المشروع

```
CppDroidIDE/
├── .github/workflows/build.yml    # GitHub Actions
├── app/
│   ├── src/main/
│   │   ├── java/com/cppdroid/ide/
│   │   │   ├── MainActivity.kt
│   │   │   ├── compiler/
│   │   │   │   └── TermuxCompiler.kt
│   │   │   ├── data/db/
│   │   │   │   └── AppDatabase.kt
│   │   │   └── ui/
│   │   │       ├── screens/IDEScreen.kt
│   │   │       └── theme/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── setup_termux.sh               # إعداد Termux
└── README.md
```
