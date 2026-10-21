# Chess Lab APK

1. Put your Stockfish JavaScript build in `www/` (for example `stockfish.js` plus its `.wasm` file, from the stockfish.js project). If you renamed it, type the new name into the box on the Stockfish arena page.
2. Push everything in this folder to a GitHub repo (branch `main`). Make sure `.github/workflows/android.yml` is included.
3. Open the repo's Actions tab. The "Build APK" run starts by itself (or press Run workflow). When it finishes, download the `chess-lab-apk` artifact, unzip it, and install `app-debug.apk` (allow "install unknown apps").

Stockfish must be the JS/WASM build. A native Android Stockfish binary cannot run inside the app's web view.
