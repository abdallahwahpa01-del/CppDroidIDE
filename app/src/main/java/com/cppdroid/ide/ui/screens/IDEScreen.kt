package com.cppdroid.ide.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cppdroid.ide.compiler.CppLibrary
import com.cppdroid.ide.ui.theme.*

// ===== DATA MODELS =====

data class EditorTab(
    val id: String,
    val fileName: String,
    var content: String,
    var isModified: Boolean = false
)

data class FileTreeItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileTreeItem> = emptyList(),
    var isExpanded: Boolean = false
)

// ===== MAIN IDE SCREEN =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDEScreen() {
    var tabs by remember { mutableStateOf(listOf(
        EditorTab("1", "main.cpp", DEFAULT_CPP_CODE),
        EditorTab("2", "CMakeLists.txt", DEFAULT_CMAKE)
    )) }
    var activeTabId by remember { mutableStateOf("1") }
    var showFileTree by remember { mutableStateOf(true) }
    var showConsole by remember { mutableStateOf(true) }
    var consoleOutput by remember { mutableStateOf("// CppDroid IDE Ready\n// Termux integration active\n") }
    var isCompiling by remember { mutableStateOf(false) }
    var showLibraryDialog by remember { mutableStateOf(false) }
    var selectedLibraries by remember { mutableStateOf(setOf<CppLibrary>()) }

    val activeTab = tabs.find { it.id == activeTabId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ─── Top Bar ───
        IDETopBar(
            onToggleFileTree = { showFileTree = !showFileTree },
            onToggleConsole = { showConsole = !showConsole },
            onBuild = {
                isCompiling = true
                consoleOutput += "\n$ clang++ main.cpp -std=c++17 -O2 ...\n"
            },
            onRun = {
                consoleOutput += "\n$ ./main\nHello from CppDroid!\n"
            },
            onLibraries = { showLibraryDialog = true },
            isCompiling = isCompiling
        )

        // ─── Tab Bar ───
        TabBar(
            tabs = tabs,
            activeTabId = activeTabId,
            onTabSelect = { activeTabId = it },
            onTabClose = { id ->
                tabs = tabs.filter { it.id != id }
                if (activeTabId == id) activeTabId = tabs.firstOrNull()?.id ?: ""
            }
        )

        // ─── Main Content ───
        Row(modifier = Modifier.weight(1f)) {

            // File Tree
            AnimatedVisibility(
                visible = showFileTree,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                FileTreePanel(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                )
            }

            // Vertical Divider
            if (showFileTree) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF3C3C3C))
                )
            }

            // Editor + Console
            Column(modifier = Modifier.weight(1f)) {

                // Code Editor
                Box(modifier = Modifier.weight(if (showConsole) 0.6f else 1f)) {
                    if (activeTab != null) {
                        CodeEditor(
                            content = activeTab.content,
                            fileName = activeTab.fileName,
                            onContentChange = { newContent ->
                                tabs = tabs.map {
                                    if (it.id == activeTabId)
                                        it.copy(content = newContent, isModified = true)
                                    else it
                                }
                            }
                        )
                    } else {
                        WelcomeScreen()
                    }
                }

                // Console
                AnimatedVisibility(visible = showConsole) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF3C3C3C))
                        )
                        ConsolePanel(
                            output = consoleOutput,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.4f),
                            onClear = { consoleOutput = "" },
                            onRunCommand = { cmd ->
                                consoleOutput += "\n$ $cmd\n"
                            }
                        )
                    }
                }
            }
        }

        // ─── Status Bar ───
        StatusBar(
            activeFile = activeTab?.fileName ?: "",
            isModified = activeTab?.isModified ?: false,
            selectedLibraries = selectedLibraries.size
        )
    }

    // Library Dialog
    if (showLibraryDialog) {
        LibraryDialog(
            selected = selectedLibraries,
            onDismiss = { showLibraryDialog = false },
            onConfirm = { libs ->
                selectedLibraries = libs
                showLibraryDialog = false
            }
        )
    }
}

// ===== TOP BAR =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDETopBar(
    onToggleFileTree: () -> Unit,
    onToggleConsole: () -> Unit,
    onBuild: () -> Unit,
    onRun: () -> Unit,
    onLibraries: () -> Unit,
    isCompiling: Boolean
) {
    TopAppBar(
        title = {
            Text(
                "CppDroid IDE",
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onToggleFileTree) {
                Icon(Icons.Default.Folder, contentDescription = "Files", tint = TextSecondary)
            }
        },
        actions = {
            // Libraries
            IconButton(onClick = onLibraries) {
                Icon(Icons.Default.LibraryBooks, contentDescription = "Libraries", tint = Secondary)
            }

            // Build
            IconButton(onClick = onBuild, enabled = !isCompiling) {
                if (isCompiling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Build, contentDescription = "Build", tint = Warning)
                }
            }

            // Run
            IconButton(onClick = onRun) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Success)
            }

            // Console Toggle
            IconButton(onClick = onToggleConsole) {
                Icon(Icons.Default.Terminal, contentDescription = "Console", tint = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surface
        )
    )
}

// ===== TAB BAR =====

@Composable
fun TabBar(
    tabs: List<EditorTab>,
    activeTabId: String,
    onTabSelect: (String) -> Unit,
    onTabClose: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(SurfaceVariant)
            .horizontalScroll(rememberScrollState())
    ) {
        tabs.forEach { tab ->
            val isActive = tab.id == activeTabId
            Row(
                modifier = Modifier
                    .height(36.dp)
                    .background(if (isActive) Background else SurfaceVariant)
                    .clickable { onTabSelect(tab.id) }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // File type icon color
                val iconColor = when {
                    tab.fileName.endsWith(".cpp") || tab.fileName.endsWith(".cc") -> Color(0xFF519ABA)
                    tab.fileName.endsWith(".h") || tab.fileName.endsWith(".hpp") -> Color(0xFFA074C4)
                    tab.fileName.endsWith(".cmake") || tab.fileName == "CMakeLists.txt" -> Color(0xFF4EC9B0)
                    else -> TextSecondary
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(iconColor, RoundedCornerShape(2.dp))
                )

                Text(
                    text = tab.fileName,
                    color = if (isActive) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (tab.isModified) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Primary, RoundedCornerShape(3.dp))
                    )
                }

                IconButton(
                    onClick = { onTabClose(tab.id) },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Active tab border top
            if (isActive) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF3C3C3C))
                )
            }
        }
    }
}

// ===== CODE EDITOR =====

@Composable
fun CodeEditor(
    content: String,
    fileName: String,
    onContentChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val lines = content.split("\n")

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Line Numbers
        Column(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .background(Color(0xFF1A1A1A))
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            lines.forEachIndexed { index, _ ->
                Text(
                    text = "${index + 1}",
                    color = LineNumber,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(end = 8.dp, start = 4.dp)
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color(0xFF3C3C3C))
        )

        // Actual Editor
        BasicTextField(
            content = content,
            onContentChange = onContentChange,
            fileName = fileName,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollState)
        )
    }
}

@Composable
fun BasicTextField(
    content: String,
    onContentChange: (String) -> Unit,
    fileName: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = content,
        onValueChange = onContentChange,
        modifier = modifier.padding(8.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = TextPrimary,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 20.sp
        ),
        decorationBox = { innerTextField ->
            if (content.isEmpty()) {
                Text(
                    "// Start coding...",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            innerTextField()
        }
    )
}

// ===== FILE TREE =====

@Composable
fun FileTreePanel(modifier: Modifier = Modifier) {
    val sampleFiles = listOf(
        FileTreeItem("MyProject", "/project", true, listOf(
            FileTreeItem("src", "/project/src", true, listOf(
                FileTreeItem("main.cpp", "/project/src/main.cpp", false),
                FileTreeItem("game.cpp", "/project/src/game.cpp", false),
                FileTreeItem("game.h", "/project/src/game.h", false),
            )),
            FileTreeItem("include", "/project/include", true, listOf(
                FileTreeItem("raylib.h", "/project/include/raylib.h", false),
            )),
            FileTreeItem("CMakeLists.txt", "/project/CMakeLists.txt", false),
        ))
    )

    Column(
        modifier = modifier
            .background(Color(0xFF1E1E1E))
            .padding(top = 8.dp)
    ) {
        Text(
            "EXPLORER",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        LazyColumn {
            items(sampleFiles) { item ->
                FileTreeItemView(item = item, depth = 0)
            }
        }
    }
}

@Composable
fun FileTreeItemView(item: FileTreeItem, depth: Int) {
    var expanded by remember { mutableStateOf(item.isExpanded) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (item.isDirectory) expanded = !expanded }
                .padding(
                    start = (depth * 16 + 8).dp,
                    top = 3.dp,
                    bottom = 3.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (item.isDirectory) {
                Icon(
                    if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Icon(
                    if (expanded) Icons.Default.FolderOpen else Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFFDCB67A),
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Spacer(modifier = Modifier.size(14.dp))
                val iconColor = when {
                    item.name.endsWith(".cpp") || item.name.endsWith(".cc") -> Color(0xFF519ABA)
                    item.name.endsWith(".h") || item.name.endsWith(".hpp") -> Color(0xFFA074C4)
                    item.name.endsWith(".cmake") || item.name == "CMakeLists.txt" -> Color(0xFF4EC9B0)
                    else -> TextSecondary
                }
                Icon(
                    Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                item.name,
                color = if (item.isDirectory) TextPrimary else TextSecondary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (item.isDirectory && expanded) {
            item.children.forEach { child ->
                FileTreeItemView(item = child, depth = depth + 1)
            }
        }
    }
}

// ===== CONSOLE =====

@Composable
fun ConsolePanel(
    output: String,
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    onRunCommand: (String) -> Unit
) {
    var command by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.background(Color(0xFF1A1A1A))
    ) {
        // Console Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TERMINAL",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClear, modifier = Modifier.size(20.dp)) {
                Icon(
                    Icons.Default.DeleteSweep,
                    contentDescription = "Clear",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Output
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            Text(
                text = output,
                color = Color(0xFFCCCCCC),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }

        // Command Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0D0D))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("$", color = Success, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
            androidx.compose.foundation.text.BasicTextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (command.isNotBlank()) {
                        onRunCommand(command)
                        command = ""
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Run", tint = Primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ===== LIBRARY DIALOG =====

@Composable
fun LibraryDialog(
    selected: Set<CppLibrary>,
    onDismiss: () -> Unit,
    onConfirm: (Set<CppLibrary>) -> Unit
) {
    var localSelected by remember { mutableStateOf(selected.toMutableSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text("C++ Libraries", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(CppLibrary.values()) { lib ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (localSelected.contains(lib))
                                    localSelected.remove(lib)
                                else
                                    localSelected.add(lib)
                            }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = localSelected.contains(lib),
                            onCheckedChange = { checked ->
                                if (checked) localSelected.add(lib)
                                else localSelected.remove(lib)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Primary,
                                uncheckedColor = TextSecondary
                            )
                        )
                        Column {
                            Text(
                                lib.description,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                            if (lib.pkgName.isNotEmpty()) {
                                Text(
                                    "pkg install ${lib.pkgName}",
                                    color = SyntaxComment,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(localSelected) }) {
                Text("Apply", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ===== STATUS BAR =====

@Composable
fun StatusBar(activeFile: String, isModified: Boolean, selectedLibraries: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .background(Primary)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (activeFile.isNotEmpty()) "  $activeFile${if (isModified) " ●" else ""}" else "  Ready",
            color = Color.White,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("C++17", color = Color.White.copy(0.8f), fontSize = 11.sp)
            Text("ARM64", color = Color.White.copy(0.8f), fontSize = 11.sp)
            Text("Libs: $selectedLibraries", color = Color.White.copy(0.8f), fontSize = 11.sp)
            Text("UTF-8", color = Color.White.copy(0.8f), fontSize = 11.sp)
        }
    }
}

// ===== WELCOME SCREEN =====

@Composable
fun WelcomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("CppDroid IDE", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("C++ Development on Android", color = TextSecondary, fontSize = 14.sp)
            Text("ARM64 • OpenGL ES 3.1 • SDL2 • Raylib", color = TextSecondary, fontSize = 12.sp,
                fontFamily = FontFamily.Monospace)
        }
    }
}

// ===== DEFAULT CODE TEMPLATES =====

const val DEFAULT_CPP_CODE = """#include <iostream>
#include <SDL2/SDL.h>

// CppDroid IDE - Galaxy Tab A (2016)
// ARM64 | OpenGL ES 3.1 | SDL2 | Raylib

int main(int argc, char* argv[]) {
    std::cout << "Hello from CppDroid!" << std::endl;
    
    // Initialize SDL2
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        std::cerr << "SDL Error: " << SDL_GetError() << std::endl;
        return 1;
    }
    
    SDL_Window* window = SDL_CreateWindow(
        "My Game",
        SDL_WINDOWPOS_CENTERED,
        SDL_WINDOWPOS_CENTERED,
        800, 600,
        SDL_WINDOW_OPENGL | SDL_WINDOW_SHOWN
    );
    
    bool running = true;
    SDL_Event event;
    
    while (running) {
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT) running = false;
            if (event.type == SDL_FINGERDOWN) {
                // Handle touch input
                float x = event.tfinger.x;
                float y = event.tfinger.y;
            }
        }
        
        SDL_Delay(16); // ~60 FPS
    }
    
    SDL_DestroyWindow(window);
    SDL_Quit();
    return 0;
}"""

const val DEFAULT_CMAKE = """cmake_minimum_required(VERSION 3.20)
project(MyGame)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Sources
add_executable(MyGame
    src/main.cpp
)

# SDL2
find_package(SDL2 REQUIRED)
target_include_directories(MyGame PRIVATE \${SDL2_INCLUDE_DIRS})
target_link_libraries(MyGame \${SDL2_LIBRARIES} GLESv2 EGL)

# Flags for ARM64
target_compile_options(MyGame PRIVATE -Wall -O2 -march=armv8-a)
"""
