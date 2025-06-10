package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Gui extends Module {
    // ★変更: removeWatermark を削除
    public static ButtonSetting removePlayerModel, resetPosition, translucentBackground, rainBowOutlines, toolTip;
    public static ModeSetting font;
    public static ModeSetting theme;

    public static ModeSetting watermarkMode;

    // ウォーターマークの種類を定義するenum
    public enum WatermarkType {
        RAVEN_XD_STYLE("Raven XD"), // ★変更: 通常版 から Raven XD へ
        RAVEN_BS_STYLE("Raven bS"),   // Raven bS をスタイル名に
        RAVEN_B4_STYLE("Raven B4"),   // Raven B4 をスタイル名に
        REMOVE("Remove");             // ★変更: None から Remove へ

        private final String displayName;

        WatermarkType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public Gui() {
        super("Gui", Module.category.client, 54);
        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", true));
        this.registerSetting(removePlayerModel = new ButtonSetting("Remove player model", false));
        // ★変更: removeWatermark の登録を削除
        this.registerSetting(translucentBackground = new ButtonSetting("Translucent background", true));
        this.registerSetting(toolTip = new ButtonSetting("Tool tip", true));
        this.registerSetting(resetPosition = new ButtonSetting("Reset position", ClickGui::resetPosition));
        this.registerSetting(font = new ModeSetting("Font", new String[]{"Minecraft", "Product Sans", "Tenacity"}, 0));
        this.registerSetting(theme = new ModeSetting("Theme", Theme.themes, 0));

        String[] watermarkNames = Arrays.stream(WatermarkType.values())
                .map(WatermarkType::toString)
                .toArray(String[]::new);

        // ★変更: デフォルトを Raven XD スタイルに設定
        this.registerSetting(watermarkMode = new ModeSetting("Watermark", watermarkNames, WatermarkType.RAVEN_XD_STYLE.ordinal()));
    }

    public static int getColor() {
        return Theme.getGradient((int) theme.getInput(), 12);
    }

    public void onEnable() {
        if (Utils.nullCheck() && mc.currentScreen != Raven.clickGui) {
            mc.displayGuiScreen(Raven.clickGui);
            Raven.clickGui.initMain();
        }
        this.disable();
    }

    public static String getCurrentWatermarkModeName() {
        return WatermarkType.values()[(int) watermarkMode.getInput()].toString();
    }
}