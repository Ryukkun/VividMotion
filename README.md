# VividMotion
写真や動画を`screen`（地図）に変換する **Minecraft の Plugin** だよー
<br><br>

## 対応バージョン
  - MC.1.12.2 - MC.1.20.1
<br>

## アイテム・概念
  - `screen` : 写真や動画を読み込んだもの。管理しやすい様に名前をつけよう！。
  - `ScreenSetter` : `screen`をワンクリックで設置できるアイテム。
<br>

## コマンド
  | command                             | description                          |
  |:------------------------------------|:-------------------------------------|
  | `/set-screen <name>`                | `ScreenSetter`を取得する。                 |
  | `/set-screen <name> <URL or Path>`  | 新しく`screen`をつくり、`ScreenSetter`を取得する。 |
  | `/give-screen <name>`               | `screen`で使われるすべての地図を取得する。            |
  | `/give-screen <name> <URL or Path>` | 新しく`screen`をつくり、使われるすべての地図を取得する。     |
  | `/screen new <name> <URL or Path>`  | 新しく`screen`をつくる。                     |
  | `/screen delete <name>`             | 駆逐する                                 |
  | `/screen pause <name>`              | 一時停止 <-> resume                      |
  | `/screen resume <name>`             | 再生 <-> pause                         |
  | `/screen set-fps <deouble>`         | FPSの設定。FPSはエンコードする際に決定する。            |
<br>

## VividMotionのここがすごい！
  - 画像・動画の処理に`誤差拡散方式`という方式採用してるため、グラデーションが綺麗！(configから変更可能)
  - 次のフレームと前のフレームを比較し 色が変化する部分のみ処理するため、無駄な通信がない。
<br>


## 注意
  - `screen`のファイル容量かなり大きくなるかもしれない。ゆるして！
  - FPSは`20`が上限。初期設定は`10`。
  - `screen`を作りすぎたり、解像度の高い`screen`を作ると、サーバーに接続できなくなるから注意！
    - `1280x720 10FPS` の`screen`1つで上限くらい。
    - Pixel per Second で 9216000（1280x720x10）を上限の基準くらいで考えるといいかも
    - １Pixel １Byteなので、 `1280x720 10FPS`の場合、 プレイヤー１人に `9.216MByte/s（73.728MBps）` 送信してることを忘れずに心に刻もう。
<br>

