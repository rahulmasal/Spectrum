package com.switcher.fiveg.util

import android.os.Build

/**
 * Detects device manufacturer for targeted network settings access.
 */
object DeviceDetector {

    enum class DeviceManufacturer {
        SAMSUNG,
        XIAOMI,
        ONEPLUS,
        OPPO,
        VIVO,
        HUAWEI,
        GOOGLE,
        MOTOROLA,
        REALME,
        OTHER
    }

    /**
     * Detects the device manufacturer.
     */
    fun detectManufacturer(): DeviceManufacturer {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        return when {
            manufacturer.contains("samsung") || brand.contains("samsung") -> DeviceManufacturer.SAMSUNG
            manufacturer.contains("xiaomi") || brand.contains("xiaomi") || brand.contains("redmi") -> DeviceManufacturer.XIAOMI
            manufacturer.contains("oneplus") || brand.contains("oneplus") -> DeviceManufacturer.ONEPLUS
            manufacturer.contains("oppo") || brand.contains("oppo") -> DeviceManufacturer.OPPO
            manufacturer.contains("vivo") || brand.contains("vivo") -> DeviceManufacturer.VIVO
            manufacturer.contains("huawei") || brand.contains("huawei") -> DeviceManufacturer.HUAWEI
            manufacturer.contains("google") || brand.contains("google") -> DeviceManufacturer.GOOGLE
            manufacturer.contains("motorola") || brand.contains("motorola") -> DeviceManufacturer.MOTOROLA
            manufacturer.contains("realme") || brand.contains("realme") -> DeviceManufacturer.REALME
            else -> DeviceManufacturer.OTHER
        }
    }

    fun isSamsung(): Boolean = detectManufacturer() == DeviceManufacturer.SAMSUNG
    fun isXiaomi(): Boolean = detectManufacturer() == DeviceManufacturer.XIAOMI
    fun isOnePlus(): Boolean = detectManufacturer() == DeviceManufacturer.ONEPLUS
    fun isGoogle(): Boolean = detectManufacturer() == DeviceManufacturer.GOOGLE

    /**
     * Returns a hint string for the detected device.
     */
    fun getDeviceHint(): String {
        return when (detectManufacturer()) {
            DeviceManufacturer.SAMSUNG -> "Samsung device - try Samsung Method first"
            DeviceManufacturer.XIAOMI -> "Xiaomi/MI device - Direct Secret Code works best"
            DeviceManufacturer.ONEPLUS -> "OnePlus device - OxygenOS detected"
            DeviceManufacturer.OPPO -> "OPPO device - ColorOS detected"
            DeviceManufacturer.VIVO -> "VIVO device - FuntouchOS detected"
            DeviceManufacturer.HUAWEI -> "Huawei device - EMUI detected"
            DeviceManufacturer.GOOGLE -> "Google Pixel - Stock Android"
            DeviceManufacturer.MOTOROLA -> "Motorola device - Moto UX detected"
            DeviceManufacturer.REALME -> "Realme device - Realme UI detected"
            DeviceManufacturer.OTHER -> "Generic Android device"
        }
    }

    /**
     * Returns the device model string.
     */
    fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"
}
