package com.example.educationapp

import platform.UIKit.UIDevice
import platform.Foundation.NSBundle

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val appVersion: String by lazy {
        NSBundle.mainBundle.infoDictionary
            ?.get("CFBundleShortVersionString") as? String ?: "1.0.0"
    }
}

actual fun getPlatform(): Platform = IOSPlatform()