package keystrokesmod.module.impl.combat.velocity;

import keystrokesmod.module.impl.combat.Velocity;
import keystrokesmod.module.impl.combat.velocity.grimac.GrimACAdvancedVelocity;
import keystrokesmod.module.impl.combat.velocity.grimac.GrimACSimpleVelocity;
import keystrokesmod.module.impl.combat.velocity.grimac.CancelS32Packet;

import keystrokesmod.module.impl.exploit.viaversionfix.ViaVersionFixHelper;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
// import keystrokesmod.event.ReceivePacketEvent; // ★★ 削除 ★★
// import net.minecraft.network.play.server.S12PacketEntityVelocity; // ★★ 削除 ★★
// import net.minecraft.network.play.server.S27PacketExplosion; // ★★ 削除 ★★
// import net.minecraft.network.Packet; // ★★ 削除 ★★
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent; // ★★ 削除 ★★
// import net.minecraftforge.fml.common.eventhandler.EventPriority; // ★★ 削除 ★★
import org.jetbrains.annotations.NotNull;

public class GrimACVelocity extends SubMode<Velocity> {
    private final ModeValue mode;

    // parentModule は GrimACVelocity が Velocity のサブモードとして、
    // その ModeValue にアクセスするために必要なので残します。
    private final Velocity parentModule;

    public GrimACVelocity(String name, @NotNull Velocity parent) {
        super(name, parent);
        this.parentModule = parent;

        this.registerSetting(mode = new ModeValue("GrimAC mode", this)
                .add(new GrimACSimpleVelocity("Simple", this))
                .add(new GrimACAdvancedVelocity("Advanced", this))
                .add(new CancelS32Packet("Zentic", this))
        );
    }

    // ★★ onReceivePacket メソッドを完全に削除します ★★


    public void sendAttack(Entity target) {
        if (ViaVersionFixHelper.is122()) {
            PacketUtils.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            PacketUtils.sendPacket(new C0APacketAnimation());
        } else {
            PacketUtils.sendPacket(new C0APacketAnimation());
            PacketUtils.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }
    }

    @Override
    public void onEnable() throws Throwable {
        mode.enable();
    }

    @Override
    public void onDisable() throws Throwable {
        mode.disable();
    }
}