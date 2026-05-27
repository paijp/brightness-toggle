# BrightnessToggle for BlackBerry KEYone (BBF100-9)

コンビニエンスキーを押すたびに画面輝度を5段階で切り替えるAndroidアプリです。

## 輝度ステップ
| ステップ | 輝度 |
|---------|------|
| 1       | 0%   |
| 2       | 25%  |
| 3       | 50%  |
| 4       | 75%  |
| 5       | 100% |

## セットアップ手順

### 1. ビルド
Android Studio でプロジェクトを開き、`Build > Make Project` → `Run`、または：
```
./gradlew assembleDebug
```
生成された `app-debug.apk` を KEYone に転送してインストール。

### 2. 権限の付与（初回起動時）
アプリ初回起動時に **「設定の変更を許可」** 画面が開きます。
`輝度切替` アプリのトグルを **ON** にしてください。

### 3. コンビニエンスキーへの割り当て
**方法A（推奨）：設定から直接割り当て**
1. 設定 → コンビニエンスキー
2. 「アクションをクリア」してから再設定
3. 「アプリを開く」→「輝度切替」を選択

**方法B：Button Mapper アプリを使う**
1. [Button Mapper](https://play.google.com/store/apps/details?id=flar2.homebutton) をインストール
2. 「ボタンを追加」→ コンビニエンスキーを押して `FP_SHORT_TOUCH (284)` を追加
3. シングルタップ → 「アプリを起動」→「輝度切替」を選択

## 動作の仕組み
- `SharedPreferences` に現在のステップ番号を保存
- 起動のたびにステップを +1 して `Settings.System.SCREEN_BRIGHTNESS` を変更
- 0.8秒後に自動でアクティビティを終了（バックグラウンドに残らない）
- 自動輝度は自動的にオフになります

## 注意事項
- Android 8.0 (Oreo) 以上が必要（KEYone は最終 Android 8.1 なので対応）
- `WRITE_SETTINGS` 権限が必要（初回のみ手動許可）
- 自動輝度は本アプリ使用中はオフになります

## ファイル構成
```
app/src/main/
├── java/com/example/brightnesstoggle/
│   └── MainActivity.kt          # メインロジック
├── res/
│   ├── layout/activity_main.xml # 画面レイアウト
│   └── values/
│       ├── strings.xml
│       └── styles.xml
└── AndroidManifest.xml
```
