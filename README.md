# VividMotion
写真や動画を`screen`（地図）に変換する **Minecraft の Plugin** だよー
<br><br>

## ◆ 対応バージョン
  - MC.1.12.2 - MC.1.20.1
<br>

## ◆ アイテム・概念
- `screen` : 写真や動画を読み込んだもの。管理しやすい様に名前をつけよう！
<br>

- `ScreenSetter` : `screen`をワンクリックで設置できるアイテム。
  - 右クリック : `screen`を設置。緑のパーティクルだと設置可能。
  - 左クリック : `screen`を一括破壊。
  - このアイテムを手に持った状態だと ブロックが破壊できない様になってます。

https://github.com/Ryukkun/VividMotion/assets/83561145/9c1dee77-2df2-42c0-80ca-96a7048c525d

<br><br>

## ◆ コマンド
  | set-screen                              | screenを設置できる`ScreenSetter`を取得するコマンド                                                  |
  |:---------------------------------------|:--------------------------------------------------------------------------|
  | `/set-screen <name>`                   | `ScreenSetter`を取得する。                                                      |
  | `/set-screen <name> <URL・Path>`        | 新しく`screen`をつくり、`ScreenSetter`を取得する。                                      |
  | &nbsp;                                 |                                                                           |
  | __give-screen__ | __screenを地図状態で取得するコマンド__ |
  | `/give-screen <name>`                  | `screen`で使われるすべての地図を取得する。                                                 |
  | `/give-screen <name> <URL・Path>`       | 新しく`screen`をつくり、使われるすべての地図を取得する。                                          |
  | &nbsp;                                 |                                                                           |
  | __screen__ | __screenの設定などを操作するコマンド__ |
  | `/screen new <name> <URL・Path>`        | 新しく`screen`をつくる。URLが長くチャット欄に収まらない場合は、コマンドブロックを使おう！                        |
  | `/screen delete <name>`                | 駆逐する                                                                      |
  | `/screen pause <name>`                 | 一時停止 <-> resume                                                           |
  | `/screen resume <name>`                | 再生 <-> pause                                                              |
  | &nbsp;                                 |                                                                            |
  | __vividmotion__ | __configの変更 や debug機能の使用に関するコマンド__ |
  | `/vividmotion fps <0.0~20.0>`          | `screen`で動画を再生する際のFPS。すでに処理済みの`screen`のFPSは途中から変更はできない。初期設定は**10.0**      |
  | `/vividmotion map-encode <encodeType>` | 画像・動画の処理の方法を変更。`<encodeType>`は [近似, 誤差拡散, 誤差拡散.Mk3] から選択。初期設定は**誤差拡散.Mk3** |
  | `/vividmotion reload`                  | configファイルを読み込みし直す。                                                            |
  | `/vividmotion show-screen-updates` | 表示面の更新場所をパーティクルで表示する （ON / OFF） |

<br>

## ◆ VividMotionのここがすごい！
  - 画像・動画の処理に`誤差拡散方式`という方式採用してるため、グラデーションが綺麗！(変更可能)
  - 次のフレームと前のフレームを比較し 色が変化する部分のみ処理するため、無駄な通信がない。
<br>


## ◆ 注意
  - `screen`のファイル容量かなり大きくなるかもしれない。ゆるして！
  - `screen`を作りすぎたり、解像度の高い`screen`を作ると、サーバーに接続できなくなるから注意！
    - `1280x720 10FPS` の`screen`1つで上限くらい。
    - Pixel per Second で 9216000（1280x720x10）を上限の基準くらいで考えるといいかも
    - １Pixel １Byteなので、 `1280x720 10FPS`の場合、 プレイヤー１人に**最大で** `9.216MByte/s（73.728MBps）` 送信してることになる。
<br>

