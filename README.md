# BrightnessToggle for BlackBerry KEYone (BBF100-9)

コンビニエンスキーを押すたびに画面輝度を4段階で切り替えるAndroidアプリです。

## 輝度ステップ
| ステップ | 輝度 |
|---------|------|
| 1       | 0%   |
| 2       | 10%  |
| 3       | 30%  |
| 4       | 100% |

## セットアップ手順

### 1. ビルド
Android Studio でプロジェクトを開き、`Build > Make Project` → `Run`、または：
```
./gradlew assembleDebug
```
生成された `app-debug.apk` を KEYone に転送してインストール。

またはリポジトリの `release/BrightnessToggle.apk` を直接インストールできます。

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
- `SharedPreferences` に前回の輝度値を保存（読み取り失敗時のフォールバック用）
- 起動のたびに現在値より大きい次のステップへ進み `Settings.System.SCREEN_BRIGHTNESS` を変更
- UIを表示せず即座にアクティビティを終了（バックグラウンドに残らない）
- 自動輝度は自動的にオフになります
- ロック画面上でも動作します

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
│   └── values/
│       ├── strings.xml
│       └── styles.xml
└── AndroidManifest.xml
```
