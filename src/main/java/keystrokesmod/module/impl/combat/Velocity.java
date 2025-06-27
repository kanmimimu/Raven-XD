package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.velocity.*;
import keystrokesmod.module.setting.impl.ModeValue;

public class Velocity extends Module {
    public final ModeValue mode;

    public static boolean hasReceivedVelocity = false;

    public Velocity() {
        super("Velocity", category.combat, "Reduce knock-back.");
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new NormalVelocity("Normal", this))
                .add(new HypixelVelocity("Hypixel", this))
                .add(new IntaveVelocity("Intave", this))
                .add(new LegitVelocity("Legit", this))
                .add(new KarhuVelocity("Karhu", this))
                .add(new MatrixVelocity("Matrix", this))
                .add(new GrimACVelocity("GrimAC", this))
                .add(new TickVelocity("Tick", this))
                .add(new ZipVelocity("7-Zip", this))
        );
    }

    @Override
    public void onEnable() {
        mode.enable();
        hasReceivedVelocity = false;
        // System.out.println("[Velocity Module] Enabled. hasReceivedVelocity reset to false."); // 削除
    }

    @Override
    public void onDisable() {
        mode.disable();
        hasReceivedVelocity = false;
        // System.out.println("[Velocity Module] Disabled. hasReceivedVelocity reset to false."); // 削除
    }

    @Override
    public String getInfo() {
        return mode.getSubModeValues().get((int) mode.getInput()).getPrettyName();
    }
}