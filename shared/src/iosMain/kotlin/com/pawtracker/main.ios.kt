@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.pawtracker

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

actual fun getPlatformName(): String = "iOS"

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
