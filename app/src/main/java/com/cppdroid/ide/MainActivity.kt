package com.cppdroid.ide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cppdroid.ide.ui.screens.IDEScreen
import com.cppdroid.ide.ui.theme.Background
import com.cppdroid.ide.ui.theme.CppDroidIDETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CppDroidIDETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    IDEScreen()
                }
            }
        }
    }
}
