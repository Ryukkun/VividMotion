# VividMotion
写真や動画を`screen`（地図）に変換する **Minecraft の Plugin** だよー
<br><br>

## ◆ 対応バージョン
  - MC.1.12.2 ~ MC.1.20.6 まで動作確認済み
  - それ以降のバージョンでも動く可能性があります。
<br>



## ◆ VividMotionのここがすごい！
  - 画像だけじゃなくて動画も再生できちゃう！音声は無理！
  - 無駄な通信が少ない！
  - 配布マップで写真を地図に変換したいときなどにも使える！（動画は不可）
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
  | `/vividmotion fps <0.0~20.0>`          | `動画のscreen`のFPS。screen生成時に設定されたFPSの値を参照して処理するため、生成済みのscreenはFPSを変更できない。初期設定は**10.0**      |
  | `/vividmotion map-encode <mapEncodeType>` | 画像・動画を地図データに変換する際のアルゴリズムの変更。`<mapEncodeType>`は後述。 |
  | `/vividmotion reload`                  | configファイルを読み込みし直す。                                                            |
  | `/vividmotion show-screen-updates` | コマンド実行者は、screenの表示面の更新場所をパーティクルで表示する （ON / OFF） |

<br>



## ◆ mapEncodeType
マインクラフトの地図は表現できる色が256色以下と限られているため、画像・動画を地図データに変換する際のアルゴリズムを3つ用意しました。  
初期設定は**誤差拡散.Mk3**  
`動画のscreen`を使用する場合は、アルゴリズムごとに通信量が変わってきます。  
それぞれメリットデメリットあるから好きなの選んで。
| encodeType | 写メ | 写メ2 | 通信量 | 説明 |
| :--- | :---: | :---: | :--- | :--- |
| _元画像_ | ![320](https://github.com/Ryukkun/VividMotion/assets/83561145/50e086bd-2b80-4868-ac21-d3365e6e1772) | ![320R](https://github.com/Ryukkun/VividMotion/assets/83561145/e731ac05-aa2b-411e-8121-8e735dbb3695) | | |
| 近似 | ![320N](https://github.com/Ryukkun/VividMotion/assets/83561145/9236cf1a-95bf-4b7a-b95b-58aef57ddc90) | ![320RN](https://github.com/Ryukkun/VividMotion/assets/83561145/3e6ff80d-d650-4745-bd44-6d126c30b750) | 少 | ベーシックな奴。色の精度は低いが、グラデーションが無い画像は得意。 |
| 誤差拡散 | ![320G](https://github.com/Ryukkun/VividMotion/assets/83561145/0a89894c-7e76-48e9-a2e9-d58596cf1a3b) | ![320RG](https://github.com/Ryukkun/VividMotion/assets/83561145/d63f4c3f-3ca1-4ffe-b765-4da966ffd506) | 多 | 遠くから見たらキレイ。近くで見たらキレイじゃない。 |
| 誤差拡散.Mk3 | ![320G3](https://github.com/Ryukkun/VividMotion/assets/83561145/2b7ca56a-ce14-4f65-974c-d48588bf0a26) | ![320RG3](https://github.com/Ryukkun/VividMotion/assets/83561145/1f3e4787-9150-4d71-b828-e7e3e5b5034e) | 中 | ↑同じく。グラデーションが無い画像は苦手。 |


<br>




## ◆ 注意・仕様
  - クリエイティブでのみscreenとの干渉（破壊やmapの回転）が可能です。
  - 地図1枚の解像度は`128x128`なので、screenに変換する写真の解像度は縦横ともに128の倍数にすることをオススメします！<br>128の倍数ではなかった場合、余白が生成されます。
  - 長い動画はscreenのファイル容量かなり大きくなるかもしれない。ゆるして！
  - `動画のscreen`は、`写真のscreen`よりも負荷が大きいので注意！<br>また、作りすぎたり解像度が高いと、通信量が膨大になり接続エラーになる場合があります！
    - `1280x720 10FPS のscreen`1つくらいで、1人のプレイヤーに送信できる通信量の上限となる。
    - １Pixel １Byteなので、 `1280x720 10FPS`の場合、 プレイヤー１人に**最大で** `9.216MByte/s（73.728MBps）` 送信してることになる。
<br>

