package keystrokesmod.module.impl.render.targetvisual.targethud;

import keystrokesmod.module.impl.render.TargetHUD;
import keystrokesmod.module.impl.render.targetvisual.ITargetVisual;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.IFont;
import keystrokesmod.utility.render.Animation;
import keystrokesmod.utility.render.Easing;
import keystrokesmod.utility.render.RenderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static keystrokesmod.module.impl.render.TargetHUD.*;

public class TestTargetHUD extends SubMode<TargetHUD> implements ITargetVisual {
    private final ModeSetting theme;
    private final ModeSetting font;
    private final ButtonSetting showStatus;
    private final ButtonSetting healthColor;
    private final Animation healthBarAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 240);
    private final Animation backgroundWidthAnimation = new Animation(Easing.EASE_IN_QUAD, 80);
    private final Animation playerXAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 80);
    private final Animation playerYAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 80);

    private final Animation playerScaleAnimation = new Animation(Easing.EASE_OUT_QUAD, 100);

    private enum PlayerAnimationState {
        IDLE,         // アニメーション非アクティブ (モデルは1.0)
        SHRINKING,    // 縮むアニメーション中 (1.0 -> 0.85)
        EXPANDING     // 戻るアニメーション中 (0.85 -> 1.0)
    }

    private PlayerAnimationState currentAnimationState = PlayerAnimationState.IDLE;

    // ★★ ダメージ状態を追跡するためのフィールドを再導入 ★★
    private boolean wasPlayerHurtLastTick = false;

    public TestTargetHUD(String name, @NotNull TargetHUD parent) {
        super(name, parent);
        this.registerSetting(theme = new ModeSetting("Theme", Theme.themes, 0));
        this.registerSetting(font = new ModeSetting("Font", new String[]{"Minecraft", "ProductSans", "Regular"}, 0));
        this.registerSetting(showStatus = new ButtonSetting("Show win or loss", true));
        this.registerSetting(healthColor = new ButtonSetting("Traditional health color", true));

        // コンストラクタでplayerScaleAnimationの初期値を設定し、確実に1.0から開始させる
        playerScaleAnimation.run(1.0);
    }

    private IFont getFont() {
        switch ((int) font.getInput()) {
            default:
            case 0:
                return FontManager.getMinecraft();
            case 1:
                return FontManager.productSansMedium;
            case 2:
                return FontManager.regular22;
        }
    }

    @Override
    public void render(@NotNull EntityLivingBase target) {
        String string = target.getDisplayName().getFormattedText();
        float health = Utils.limit(target.getHealth() / target.getMaxHealth(), 0, 1);
        string = string + " §a" + Math.round(target.getHealth()) + " §c❤ ";
        if (showStatus.isToggled() && mc.thePlayer != null) {
            String status = (health <= Utils.getCompleteHealth(mc.thePlayer) / mc.thePlayer.getMaxHealth()) ? "§aW" : "§cL";
            string = string + status;
        }

        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int n2 = 8;
        final int n3 = mc.fontRendererObj.getStringWidth(string) + n2 + 30;
        final int n4 = scaledResolution.getScaledWidth() / 2 - n3 / 2 + posX;
        final int n5 = scaledResolution.getScaledHeight() / 2 + 15 + posY;
        current$minX = n4 - n2;
        current$minY = n5 - n2;
        current$maxX = n4 + n3;
        current$maxY = n5 + (mc.fontRendererObj.FONT_HEIGHT + 5) - 6 + n2;

        final int n10 = 255;
        final int n11 = Math.min(n10, 110);
        final int n12 = Math.min(n10, 210);

        backgroundWidthAnimation.run(current$maxX - current$minX);
        float animatedWidth = (float) backgroundWidthAnimation.getValue();
        float halfAnimatedWidth = animatedWidth / 2;
        float animatedMinX = (float) (current$minX + current$maxX) / 2 - halfAnimatedWidth;
        float animatedMaxX = (float) (current$minX + current$maxX) / 2 + halfAnimatedWidth;

        RenderUtils.drawRoundedRectangle(animatedMinX, (float) current$minY, animatedMaxX, (float) (current$maxY + 13), 10.0f, Utils.merge(Color.black.getRGB(), n11));

        final int n13 = current$minX + 6 + 30;
        final int n14 = current$maxX - 6;
        final int n15 = current$maxY;

        RenderUtils.drawRoundedRectangle((float) n13, (float) n15, (float) n14, (float) (n15 + 5), 4.0f, Utils.merge(Color.black.getRGB(), n11));

        float healthBar = (float) (int) (n14 + (n13 - n14) * (1.0 - ((health < 0.05) ? 0.05 : health)));
        if (healthBar - n13 < 3) {
            healthBar = n13 + 3;
        }

        healthBarAnimation.run(healthBar);
        float lastHealthBar = (float) healthBarAnimation.getValue();

        RenderUtils.drawRoundedGradientRect((float) n13, (float) n15, lastHealthBar, (float) (n15 + 5), 4.0f,
                Utils.merge(Theme.getGradients((int) theme.getInput())[0], n12), Utils.merge(Theme.getGradients((int) theme.getInput())[0], n12),
                Utils.merge(Theme.getGradients((int) theme.getInput())[1], n12), Utils.merge(Theme.getGradients((int) theme.getInput())[1], n12));

        if (healthColor.isToggled()) {
            int healthTextColor = Utils.getColorForHealth(health);
            RenderUtils.drawRoundedRectangle((float) n13, (float) n15, lastHealthBar, (float) (n15 + 5), 4.0f, healthTextColor);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        getFont().drawString(string, (float) (n4 + 30), (float) n5, (new Color(220, 220, 220, 255).getRGB() & 0xFFFFFF) | Utils.clamp(n10 + 15) << 24, true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if (target instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) target;
            double targetX = current$minX + 5;
            double targetY = current$minY + 4;
            playerXAnimation.run(targetX);
            playerYAnimation.run(targetY);
            double animatedX = playerXAnimation.getValue();
            double animatedY = playerYAnimation.getValue();

            boolean isPlayerHurt = player.hurtTime > 0; // 現在のフレームでダメージを受けているか

            double scale;

            // ★★ 2Dモデルアニメーションの状態遷移ロジック ★★
            switch (currentAnimationState) {
                case IDLE:
                    scale = playerScaleAnimation.getValue(); // アイドル状態でもアニメーションの値を追従（初期値は1.0）
                    playerScaleAnimation.run(1.0); // IDLE状態なら常に1.0に向かってアニメーションさせる

                    // ダメージを受け始めたら縮むアニメーションを開始
                    if (isPlayerHurt && !wasPlayerHurtLastTick) {
                        playerScaleAnimation.run(0.85); // 縮む目標値
                        currentAnimationState = PlayerAnimationState.SHRINKING;
                    }
                    break;

                case SHRINKING:
                    scale = playerScaleAnimation.getValue(); // 縮むアニメーションの値を使用
                    // 縮むアニメーションが完了したか (約0.85に到達したか)
                    if (Math.abs(playerScaleAnimation.getValue() - 0.85) < 0.001) {
                        playerScaleAnimation.run(1.0); // 戻るアニメーション開始
                        currentAnimationState = PlayerAnimationState.EXPANDING;
                    }
                    // 注意: 縮むアニメーション中に再度ダメージを受けても、中断せずにこのシーケンスを完了させる
                    break;

                case EXPANDING:
                    scale = playerScaleAnimation.getValue(); // 戻るアニメーションの値を使用
                    // 戻るアニメーションが完了したか (約1.0に到達したか)
                    if (Math.abs(playerScaleAnimation.getValue() - 1.0) < 0.001) {
                        currentAnimationState = PlayerAnimationState.IDLE;
                        playerScaleAnimation.run(1.0); // 完全に1.0になったことを保証 (IDLE状態へリセット)
                        scale = 1.0; // 明示的に1.0に設定
                    }
                    // 戻るアニメーション中に再度ダメージを受けたら、縮むアニメーションへ割り込み
                    else if (isPlayerHurt && !wasPlayerHurtLastTick) {
                        playerScaleAnimation.run(0.85);
                        currentAnimationState = PlayerAnimationState.SHRINKING;
                    }
                    break;

                default: // 何らかの理由で不明な状態になった場合のフォールバック
                    scale = 1.0;
                    currentAnimationState = PlayerAnimationState.IDLE;
                    playerScaleAnimation.run(1.0);
                    break;
            }

            // wasPlayerHurtLastTick を現在の状態に更新
            wasPlayerHurtLastTick = isPlayerHurt; // 次のフレームのために現在の hurtTime 状態を保存

            double offset = -(player.hurtTime * 10); // 色変化のオフセット (既存)

            GlStateManager.pushMatrix();

            GlStateManager.translate(animatedX + 25 / 2.0, animatedY + 25 / 2.0, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-(animatedX + 25 / 2.0), -(animatedY + 25 / 2.0), 0);

            Color dynamicColor = new Color(255, (int) (255 + offset), (int) (255 + offset));
            GlStateManager.color(dynamicColor.getRed() / 255F, dynamicColor.getGreen() / 255F, dynamicColor.getBlue() / 255F, dynamicColor.getAlpha() / 255F);
            RenderUtils.renderPlayer2D((float) animatedX, (float) animatedY, 25, 25, player);
            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.popMatrix();
        }
    }
}