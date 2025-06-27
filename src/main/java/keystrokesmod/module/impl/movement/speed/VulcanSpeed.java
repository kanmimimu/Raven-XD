package keystrokesmod.module.impl.movement.speed;

import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils; // Utilsをインポート
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class VulcanSpeed extends SubMode<Speed> {
    private final SliderSetting lowHop;
    private final ButtonSetting strafeEnabled;
    // Strafe Powerの固定値は、StrafeSpeedの挙動に合わせるため削除または使用箇所を変更
    // private static final double DEFAULT_STRAFE_POWER = 0.4;

    // ★★ StrafeSpeedのように、着地時のStrafeを制御する設定を追加 ★★
    private final ButtonSetting groundStrafe;
    // VulcanSpeedは既にmc.thePlayer.jump()があるのでautoJumpは別途不要

    public VulcanSpeed(String name, @NotNull Speed parent) {
        super(name, parent);
        this.registerSetting(lowHop = new SliderSetting("Low hop", 2, 0, 8, 1));
        this.registerSetting(strafeEnabled = new ButtonSetting("Strafe", false));
        // ★★ groundStrafeの設定を追加 ★★
        this.registerSetting(groundStrafe = new ButtonSetting("Ground strafe", true));
    }

    @SubscribeEvent
    public void onPrePlayerInput(PrePlayerInputEvent event) {
        if (!MoveUtil.isMoving()) return;

        if (strafeEnabled.isToggled()) {
            // ★★ Strafeがオンの場合の新しいロジック ★★
            if (mc.thePlayer.onGround) {
                // 地面にいる場合 (offGroundTicks == 0 と同じ)
                // groundStrafeがオンなら、StrafeSpeedのように最適な速度で加速
                if (groundStrafe.isToggled()) {
                    MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance());
                } else {
                    // groundStrafeがオフなら、通常の初速加速（元のVulcanSpeedのロジック）
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MoveUtil.strafe(0.6);
                    } else {
                        MoveUtil.strafe(0.485);
                    }
                }
                // Auto jumpの挙動 (VulcanSpeedではoffGroundTicks==0でjump()しているので類似)
                if (mc.thePlayer.onGround && !Utils.jumpDown()) { // Utils.jumpDown()はジャンプキーが押されているか
                    mc.thePlayer.jump();
                }

            } else {
                // 空中にいる場合 (offGroundTicks > 0)
                // 空中ではStrafeSpeedのように引数なしのstrafe()で速度維持
                MoveUtil.strafe();

                // VulcanSpeed独自のオフグラウンドティックごとの調整
                switch (parent.offGroundTicks) {
                    case 5: // Low hopの適用
                        mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, (int) lowHop.getInput());
                        break;
                    case 9: // 天井チェック
                        if (!(blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) instanceof BlockAir)) {
                            // 天井にぶつかった時の挙動を調整（Strafeを継続するか、一時的に停止するかなど）
                            // ここでは、空中でのStrafeロジックを上書きしない
                            // MoveUtil.strafe(); // 必要であれば調整
                        }
                        break;
                }
            }
        } else {
            // ★★ Strafeがオフの場合の既存のVulcanSpeedロジック (変更なし) ★★
            switch (parent.offGroundTicks) {
                case 0:
                    mc.thePlayer.jump();
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MoveUtil.strafe(0.6);
                    } else {
                        MoveUtil.strafe(0.485);
                    }
                    break;
                case 9:
                    if (!(blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) instanceof BlockAir)) {
                        MoveUtil.strafe();
                    }
                    break;
                case 2:
                case 1:
                    MoveUtil.strafe();
                    break;
                case 5:
                    mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, (int) lowHop.getInput());
                    break;
            }
        }
    }

    public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }
}