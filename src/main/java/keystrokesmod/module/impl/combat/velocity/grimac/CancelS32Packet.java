package keystrokesmod.module.impl.combat.velocity.grimac;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.impl.combat.Velocity; // Velocity.hasReceivedVelocity を参照するため、このインポートは残す
import keystrokesmod.module.impl.combat.velocity.GrimACVelocity;
import keystrokesmod.module.setting.impl.SubMode;
import net.minecraft.network.play.server.S12PacketEntityVelocity; // ★★ 追加 ★★
import net.minecraft.network.play.server.S27PacketExplosion; // ★★ 追加 ★★
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.Packet; // ★★ 追加 ★★
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.jetbrains.annotations.NotNull;

public class CancelS32Packet extends SubMode<GrimACVelocity> {

    public CancelS32Packet(String name, @NotNull GrimACVelocity parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        // デバッグログ削除済み
    }

    @Override
    public void onDisable() {
        Velocity.hasReceivedVelocity = false;
        // デバッグログ削除済み
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // 高い優先度で処理
    public void onReceivePacket(ReceivePacketEvent event) {
        // このサブモードが有効でなければ何もしない
        if (!this.isEnabled()) {
            return;
        }

        Packet<?> packet = event.getPacket();

        // ★★ S12PacketEntityVelocity の処理 ★★
        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) packet;

            // プレイヤー自身へのノックバックパケットであるかを確認
            if (mc.thePlayer == null || velocityPacket.getEntityID() != mc.thePlayer.getEntityId()) {
                return;
            }

            // イベントがキャンセル可能であれば、キャンセルする（ノックバックをゼロにする）
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            // ノックバックパケットを受け取ったフラグを立てる
            Velocity.hasReceivedVelocity = true;
        }
        // ★★ S27PacketExplosion の処理 ★★
        else if (packet instanceof S27PacketExplosion) {
            // イベントがキャンセル可能であれば、キャンセルする（爆発によるノックバックをゼロにする）
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            // ノックバックパケットを受け取ったフラグを立てる
            Velocity.hasReceivedVelocity = true;
        }
        // ★★ S32PacketConfirmTransaction の処理 ★★
        else if (packet instanceof S32PacketConfirmTransaction) {
            // hasReceivedVelocity フラグが立っていなければ（ノックバックパケットが来ていなければ）何もしない
            if (!Velocity.hasReceivedVelocity) {
                return;
            }

            // イベントがキャンセル可能であれば、キャンセルする
            if (event.isCancelable()) {
                event.setCanceled(true);
                // S32Packetをキャンセルしたらフラグをリセット
                Velocity.hasReceivedVelocity = false;
            }
        }
    }
}