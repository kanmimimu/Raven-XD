package keystrokesmod.utility.render;

import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static keystrokesmod.Raven.mc;

public class BackgroundUtils {
    public static final ResourceLocation RES_LOGO = new ResourceLocation("keystrokesmod:textures/backgrounds/ravenxd.png");

    // ★変更: 背景ファイルを bg.png に変更
    private static final ResourceLocation FIXED_BACKGROUND = new ResourceLocation("keystrokesmod:textures/backgrounds/bg.png");

    private static int shadow = 0; // lastRenderTime と lastBackground は不要になったため削除

    // 静的初期化ブロックは不要になったため削除

    public static void renderBackground(@NotNull GuiScreen screen) {
        updateShadow(0);
        renderBackground(screen.width, screen.height);
    }

    public static void renderBackground(@NotNull GuiSlot slot) {
        updateShadow(200);
        renderBackground(slot.width, slot.height);
    }

    private static void renderBackground(final int width, final int height) {
        // ★変更: 常に固定の背景を描画
        if (!Utils.nullCheck())
            RenderUtils.drawImage(FIXED_BACKGROUND, 0, 0, width, height);

        if (shadow != 0) {
            ScaledResolution resolution = new ScaledResolution(mc);
            RenderUtils.drawBloomShadow(-16, -16, resolution.getScaledWidth() + 16, resolution.getScaledHeight() + 16, 4,
                    new Color(0, 0, 0, shadow), false
            );
        }
    }

    private static void updateShadow(final int shadowTarget) {
        if (shadowTarget > shadow) {
            shadow = (int) Math.min(shadow + 4.0 * 300 / Minecraft.getDebugFPS(), shadowTarget);
        } else if (shadowTarget < shadow) {
            shadow = (int) Math.max(shadow - 4.0 * 300 / Minecraft.getDebugFPS(), shadowTarget);
        }
    }

    public static ResourceLocation getLogoPng() {
        return RES_LOGO;
    }
}