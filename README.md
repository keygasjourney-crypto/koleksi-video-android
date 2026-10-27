# Koleksi Video — Android

Aplikasi Android ringan untuk menyimpan tautan video YouTube, memberi catatan, mencari koleksi, dan membuka video melalui aplikasi/situs YouTube resmi.

## Yang bisa dilakukan

- Simpan link `youtube.com` atau `youtu.be`.
- Catat judul dan catatan pribadi.
- Cari berdasarkan judul, catatan, atau link.
- Hapus item dari koleksi.
- Simpan koleksi secara lokal di perangkat.
- Buka video melalui aplikasi YouTube resmi atau browser.

## Batas pemutaran

Aplikasi ini **bukan pemutar YouTube pihak ketiga** dan tidak mengunduh stream YouTube. Iklan YouTube tetap bisa muncul saat video diputar di YouTube. Aplikasi ini tidak menambahkan iklan sendiri.

## Build APK tanpa Android Studio

Project ini sudah menyertakan GitHub Actions di `.github/workflows/android-apk.yml`.

1. Buat repository baru di GitHub, misalnya `koleksi-video-android`.
2. Upload seluruh isi folder project ini ke repository (termasuk folder tersembunyi `.github`). Pastikan `settings.gradle`, `build.gradle`, `gradle.properties`, dan folder `app` berada di root repository.
3. Buka tab **Actions** pada repository. Jika diminta, pilih **I understand my workflows, go ahead and enable them**.
4. Pilih workflow **Build Android APK** lalu klik **Run workflow**, atau push commit ke branch `main`/`master`.
5. Setelah workflow selesai dengan status hijau, buka run tersebut dan unduh APK dari bagian **Artifacts**.
6. Untuk membuat halaman **GitHub Release** yang memiliki file APK terlampir: buka **Releases → Draft a new release**, buat tag `v1.0.0`, lalu klik **Publish release**. Workflow akan membangun APK dan memasangnya sebagai asset release.

APK yang dihasilkan bernama `KoleksiVideo-<branch-atau-tag>.apk`. APK ini ditandatangani dengan debug key yang dibuat otomatis oleh runner GitHub agar bisa dipasang langsung untuk penggunaan pribadi/pengujian. Ini bukan signing key produksi untuk Google Play. Karena runner GitHub bersifat sementara, tanda tangan debug dapat berubah pada build berikutnya; bila Android menolak pemasangan update karena konflik tanda tangan, hapus versi lama terlebih dahulu lalu pasang APK terbaru.

## Menghubungkan GitHub ke ChatGPT

1. Buka ChatGPT → **Settings → Apps** (pada sebagian antarmuka lama tertulis **Plugins**).
2. Cari **GitHub**, pilih **Connect/Install**, lalu masuk ke akun GitHub.
3. Tinjau izin yang diminta. Di halaman GitHub, pasang/otorisasi aplikasi ChatGPT dan pilih repository `koleksi-video-android` yang boleh diakses.
4. Kembali ke ChatGPT. Repository baru mungkin memerlukan beberapa menit sampai terlihat.

Catatan: koneksi GitHub di ChatGPT untuk membaca/mencari isi repository tidak otomatis memberi kemampuan untuk push atau mengubah file. Untuk mengedit dan mem-publish perubahan langsung dari alur coding OpenAI, gunakan Codex jika tersedia di akunmu. GitHub Actions tetap bisa membangun APK sendiri setelah kode di-upload ke repository.

## Konfigurasi teknis

- Application ID: `com.bor.koleksivideo`
- Versi: `1.0.0` (versionCode `1`)
- Minimum Android: Android 6.0 / API 23
- Compile/target SDK: 35
- Android Gradle Plugin: 8.6.1
- Java: 17
- Gradle: 8.7
- Tidak memerlukan API key YouTube.
