# WokKeyboards

Custom Android IME (keyboard) untuk mengoperasikan aplikasi spreadsheet
(Google Sheets, WPS, dll) dari tablet — lengkap dengan layout QWERTY,
baris F1–F12/navigasi, dan modifier Ctrl/Alt/Shift yang bisa "dikunci"
untuk kombinasi berurutan seperti Alt, D, F, F.

## Fitur

- Layout penuh: huruf (QWERTY), angka, simbol — jadi bisa dipakai sebagai
  keyboard utama, bukan cuma tambahan.
- Baris atas bisa di-toggle antara **F1–F12** dan **navigasi** (Home, End,
  PgUp, PgDn, arrow keys) lewat tombol "Nav" / "F1-12".
- Ctrl, Alt, Shift punya dua mode:
  - **Tap sekali** = aktif untuk satu tombol berikutnya saja (mis. Ctrl+F1).
  - **Tap dua kali cepat** = terkunci (lock) sampai di-tap lagi — dipakai
    untuk kombinasi berurutan seperti Alt (kunci) → D → F → F.
  - Tombol yang aktif berwarna kuning, yang terkunci berwarna oranye.
- Padding bawah otomatis menyesuaikan tinggi navigation bar Android, plus
  slider manual (0–150dp) di Settings kalau butuh penyesuaian lebih.
- Compact mode untuk mengurangi tinggi keyboard total.

## Cara build (tanpa Android Studio)

Project ini didesain untuk di-build lewat **GitHub Actions**, bukan di
perangkat lokal:

1. Buat repo baru di GitHub, lalu push seluruh isi folder ini:
   ```bash
   gh repo create wokkeyboards --public --clone
   cd wokkeyboards
   # copy semua file project ke sini
   git add .
   git commit -m "initial WokKeyboards IME"
   git push origin main
   ```
2. Workflow `.github/workflows/build.yml` otomatis jalan setelah push ke
   `main` (atau trigger manual lewat tab Actions > Run workflow).
3. Setelah build sukses (centang hijau), buka run tersebut → bagian
   **Artifacts** → download `wokkeyboards-apks.zip`.
4. Extract, kamu akan dapat `app-debug.apk` (untuk testing langsung) dan
   `app-release-unsigned.apk` (perlu ditandatangani/signed sebelum rilis
   ke Play Store).

Catatan: project ini sengaja tidak menyertakan Gradle wrapper (`gradlew`)
karena build dilakukan di GitHub Actions runner, yang menginstal Gradle
8.9 secara langsung. Kalau suatu saat ingin build lokal di komputer
dengan Android Studio, jalankan `gradle wrapper` sekali untuk
men-generate wrapper-nya.

## Cara install & aktivasi di tablet

1. Pindahkan `app-debug.apk` ke tablet, tap untuk install (izinkan
   "install from unknown sources" kalau diminta).
2. Buka app **WokKeyboards** → tombol "Aktifkan Keyboard" akan membuka
   **Settings > System > Languages & Input**.
3. Aktifkan WokKeyboards di daftar keyboard, lalu pilih sebagai keyboard
   aktif saat mengetik (biasanya lewat ikon keyboard di notification bar).
4. Buka Settings WokKeyboards untuk atur padding bawah/compact mode kalau
   keyboard masih tertutup gesture bar di device tertentu.

## Cara pakai untuk shortcut Google Sheets (contoh Alt+D, F, F)

1. Tap **Alt** dua kali cepat (jadi oranye = terkunci).
2. Tap **123** untuk pindah ke mode angka/simbol kalau perlu, atau
   langsung tap huruf **D** di layout QWERTY (mode Letters).
3. Tap **F**, lalu **F** lagi — masing-masing terkirim sebagai Alt+D,
   Alt+F, Alt+F ke aplikasi.
4. Tap **Alt** sekali lagi untuk melepas kunci.

## Sebelum publish ke Play Store

- **Ikon aplikasi**: saat ini pakai ikon sistem bawaan Android sebagai
  placeholder (`@android:drawable/sym_def_app_icon`). Ganti dengan ikon
  asli 512×512 sebelum submit — Play Store menolak ikon hasil ciplak
  brand lain.
- **Signing**: APK release masih unsigned. Perlu dibuat keystore lalu
  ditandatangani (bisa lewat `apksigner` di GitHub Actions juga, tinggal
  tambahkan step dengan secret keystore di repo).
- **Privacy Policy**: wajib ada URL kebijakan privasi yang bisa diakses
  tanpa login, meski app ini tidak mengumpulkan data pengguna.
- **Data safety form**: isi jujur di Play Console — app ini tidak
  mengirim data ke server manapun (semua pemrosesan lokal di perangkat).
- **Deskripsi & screenshot**: jelaskan use case spreadsheet secara
  spesifik (Ctrl, F1–F12, shortcut) supaya jelas nilai tambahnya
  dibanding keyboard bawaan — ini membantu lolos review kategori
  produktivitas.

## Struktur project

```
WokKeyboards/
├── build.gradle
├── settings.gradle
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/wokkeyboards/app/
│       │   ├── WokKeyboardService.kt   (IME service, kirim KeyEvent)
│       │   ├── WokKeyboardView.kt      (custom view + logic modifier)
│       │   ├── KeyboardLayouts.kt      (definisi tombol per mode)
│       │   ├── SettingsActivity.kt
│       │   └── SetupActivity.kt
│       └── res/
│           ├── xml/method.xml
│           ├── values/{strings,styles}.xml
│           └── layout/{activity_settings,activity_setup}.xml
└── .github/workflows/build.yml
```
