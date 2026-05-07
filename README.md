# 🛰️ Spectrum

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-5.0+-3DDC84?logo=android&logoColor=white)](https://www.android.com)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Spectrum** is a sleek, modern, and high-performance Android utility designed to give you total control over your device's network connectivity. Built with a focus on speed and aesthetic elegance, Spectrum lets you bypass restricted menus to force specific network modes like **5G Only**, **LTE Only**, and more.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| **🚀 One-Tap Switch** | Instantly jump to hidden preferred network settings. |
| **📶 Force 5G/NR** | Stay on high-speed 5G even when the system tries to downgrade. |
| **🎨 Material 3 UI** | Beautiful, dynamic interface with Glassmorphic design elements. |
| **🔘 Quick Tile** | Change modes directly from your notification panel without opening the app. |
| **📊 Signal Monitor** | Track your real-time signal strength (dBm) and network technology. |
| **⚡ Speed Insight** | Built-in indicators for 5G, 4G LTE, WCDMA, and GSM. |

---

## 📸 Preview

<p align="center">
  <img src="https://raw.githubusercontent.com/rahulmasal/Spectrum/main/screenshots/banner.png" width="800" alt="Spectrum Banner">
</p>

---

## 🛠️ Technical Stack

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative, reactive UI.
- **Design System:** [Material 3](https://m3.material.io/) with Dynamic Color support.
- **Architecture:** MVVM (Model-View-ViewModel) for clean separation of concerns.
- **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/) for robust, testable code.
- **Asynchronous Flow:** Kotlin Coroutines & Flow for real-time network updates.
- **Local Storage:** Room Database for signal history and preferences.

---

## 🚀 Getting Started

### Prerequisites
- Android device running **Lollipop (5.0)** or higher.
- 5G capability requires a compatible device and carrier plan.

### Installation
1.  **Clone** this repository:
    ```bash
    git clone https://github.com/rahulmasal/Spectrum.git
    ```
2.  Open the project in **Android Studio (Ladybug or newer)**.
3.  Click **Run** to install on your connected device.

---

## ⚠️ Disclaimer

Forcing specific network modes (e.g., "5G Only") in areas without coverage will cause your device to lose signal and prevent you from receiving calls or SMS. Please use these settings responsibly and revert to "Auto" when necessary.

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
