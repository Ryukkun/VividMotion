# VividMotion
写真や動画を`screen`（地図）に変換する **Minecraft の Plugin** だよー
<br><br>

## 対応バージョン
  - MC.1.12.2 - MC.1.20.1
<br>

## 登場人物
  - `screen` : 地図の集合体。写真や動画が映る。また、管理しやすい様に名前をつけることがきる。
  - `ScreenSetter` : `screen`をワンクリックで設置できる優れもの。
<br>

## すごいよん
  - 画面の処理に`誤差拡散方式`を採用してるため、グラデーションが綺麗。
  - 前のフレームから色が変化する部分のみ 処理するため、画面の動きが穏やかな動画は得意。
<br>


## 注意
  - FPSは`20`が上限。
  - `screen`を作りすぎたり、解像度の高い`screen`を作ると、サーバーに接続できなくなるから注意。
    - `1280x720 20FPS` の`screen`1つで上限くらい。
    - Pixel per Second で 18432000（1280x720x20）を上限の基準くらいで考えるといいかも
    - １Pixel １Byteなので、 `1280x720 20FPS`の場合、 プレイヤー１人に `18.432MByte/s（147.456MBit/s）` 送信してることを忘れずに心に刻もう。
<br>


## コマンド
  | command | description |
  | :--- | :--- |
  | `/set-screen <name>` | `ScreenSetter`を取得する。 |
  | `/set-screen <name> <URL or Path>` | 新しく`screen`をつくり、`ScreenSetter`を取得する。 |
  | `/give-screen <name>` | `screen`で使われるすべての地図を取得する。 |
  | `/give-screen <name> <URL or Path>` | 新しく`screen`をつくり、使われるすべての地図を取得する。 |
  | `/screen new <name> <URL or Path>` | 新しく`screen`をつくる。 |
  | `/screen delete <name>` | 駆逐する |
  | `/screen pause <name>` | 一時停止 |
  | `/screen resume <name>` | 再生 |
  | `/screen set-fps <name> <deouble>` | FPSの設定。 |
