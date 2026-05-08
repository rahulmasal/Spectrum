# 🛰️ Spectrum

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?logo=android&logoColor=white)](https://www.android.com)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.1.0-blue)]()

**Spectrum** is a sleek, modern, and high-performance Android utility designed to give you total control over your device's network connectivity. Built with a focus on speed and aesthetic elegance, Spectrum lets you access hidden menus to force specific network modes like **5G Only**, **LTE Only**, and more using multiple methods including the secret code `*#*#4636#*#*`.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| **🔓 4 Access Methods** | Universal secret code `*#*#4636#*#*`, Android 11+, 11-, and Samsung-specific methods |
| **📶 Force 5G/NR** | Stay on high-speed 5G even when the system tries to downgrade |
| **🎨 Material 3 UI** | Beautiful, dynamic interface with Glassmorphic design elements |
| **🔘 Quick Tile** | Change modes directly from your notification panel without opening the app |
| **📊 Signal Monitor** | Track your real-time signal strength (dBm) and network technology |
| **⚡ Speed Test** | Built-in download/upload speed testing with ping measurement |
| **📱 All Architectures** | Supports ARM, x86, and universal APK builds |

---

## 🔐 Network Mode Methods

Spectrum provides **4 methods** to access hidden Android network settings:

| Method | Description | Best For |
|--------|-------------|----------|
| **Method 1: Android 11-** | Opens standard RadioInfo activity | Android 10 and older |
| **Method 2: Android 11+** | Opens RadioInfoControlActivity | Android 11, 12, 13, 14+ |
| **Samsung Method** | Opens Samsung's hidden network setting | Samsung Galaxy devices |
| **Direct Secret Code** | Dials `*#*#4636#*#*` to open Phone Information | Universal - all devices |

---

## 📸 Preview

<p align="center">
  <img src="https://raw.githubusercontent.com/rahulmasal/Spectrum/main/screenshots/banner.png" width="800" alt="Spectrum Banner">
</p>

---

## 🛠️ Technical Stack

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative, reactive UI
- **Design System:** [Material 3](https://m3.material.io/) with Dynamic Color support
- **Architecture:** MVVM (Model-View-ViewModel) for clean separation of concerns
- **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/) for robust, testable code
- **Asynchronous Flow:** Kotlin Coroutines & Flow for real-time network updates
- **Local Storage:** Room Database for signal history and preferences
- **Data Preferences:** Jetpack DataStore for user settings

---

## 🚀 Getting Started

### Prerequisites
- Android device running **Android 8.0 (API 26)** or higher
- 5G capability requires a compatible device and carrier plan

### Installation
1. Download the latest APK from [Releases](https://github.com/rahulmasal/Spectrum/releases)
2. Enable "Install from unknown sources" in your device settings
3. Install the APK and launch Spectrum

### Build from Source
```bash
git clone https://github.com/rahulmasal/Spectrum.git
cd Spectrum
./gradlew assembleDebug
```

---

## ⚠️ Disclaimer

Forcing specific network modes (e.g., "5G Only" or "NR Only") in areas without 5G coverage will cause your device to lose signal and prevent you from receiving calls or SMS. Please use these settings responsibly and revert to "Auto" when necessary.

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for more information.

<p align="center">
  Developed with ❤️ by <a href="https://github.com/rahulmasal">Rahul Masal</a>
</p>
