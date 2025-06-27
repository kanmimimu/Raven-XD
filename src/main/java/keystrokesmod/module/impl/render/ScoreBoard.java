package keystrokesmod.module.impl.render; // パッケージ名を変更

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;

public class ScoreBoard extends Module { // クラス名を変更

    // ★★ これがMiximから参照される静的変数です ★★
    public static ButtonSetting toggleScoreboardChanges;

    public ScoreBoard() { // コンストラクタ名を変更
        super("ScoreBoard", Module.category.render, 0); // モジュール名とカテゴリを変更
        // 設定を登録
        this.registerSetting(toggleScoreboardChanges = new ButtonSetting("Enable Changes", true)); // デフォルトで有効
    }
}