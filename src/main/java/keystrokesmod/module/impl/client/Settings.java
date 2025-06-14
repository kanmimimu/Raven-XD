package keystrokesmod.module.impl.client;


import keystrokesmod.event.PreTickEvent;

import keystrokesmod.mixins.impl.client.MinecraftAccessor;

import keystrokesmod.module.Module;

import keystrokesmod.module.setting.impl.ButtonSetting;

import keystrokesmod.module.setting.impl.DescriptionSetting;

import keystrokesmod.module.setting.impl.ModeSetting;

import keystrokesmod.module.setting.impl.SliderSetting;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.jetbrains.annotations.NotNull;


public class Settings extends Module {

    public static ButtonSetting weaponSword;

    public static ButtonSetting weaponAxe;

    public static ButtonSetting weaponRod;

    public static ButtonSetting weaponStick;

    private final ButtonSetting oldHitReg;

    public static SliderSetting offset;

    public static SliderSetting timeMultiplier;

    public static ModeSetting toggleSound;

    public static ButtonSetting sendMessage;


    public Settings() {

        super("Settings", category.client, 0);

        this.registerSetting(new DescriptionSetting("General"));

        this.registerSetting(weaponSword = new ButtonSetting("Set sword as weapon", true));

        this.registerSetting(weaponAxe = new ButtonSetting("Set axe as weapon", false));

        this.registerSetting(weaponRod = new ButtonSetting("Set rod as weapon", false));

        this.registerSetting(weaponStick = new ButtonSetting("Set stick as weapon", false));

        this.registerSetting(oldHitReg = new ButtonSetting("1.7 hit reg", true));

        this.registerSetting(new DescriptionSetting("Profiles"));

        this.registerSetting(sendMessage = new ButtonSetting("Send message on enable", true));

        this.registerSetting(new DescriptionSetting("Theme colors"));

        this.registerSetting(offset = new SliderSetting("Offset", 0.5, -3.0, 3.0, 0.1));

        this.registerSetting(timeMultiplier = new SliderSetting("Time multiplier", 0.5, 0.1, 4.0, 0.1));

        this.registerSetting(toggleSound = new ModeSetting("Toggle sound", new String[]{"None", "Vanilla","Rise", "Sigma","QuickMacro", "Augustus"}, 1));

        this.canBeEnabled = false;

    }


    @SubscribeEvent

    public void onPreTick(PreTickEvent event) {

        if (oldHitReg.isToggled())

            ((MinecraftAccessor) mc).setLeftClickCounter(-1);

    }


    public static @NotNull String getToggleSound(boolean enable) {

        final String startSuffix = "keystrokesmod:toggle.";

        final String endSuffix = enable ? ".enable" : ".disable";


        final String middleSuffix;

        switch ((int) toggleSound.getInput()) {

            default:

            case 0:

                return "";

            case 1:

                middleSuffix = "vanilla";

                break;

            case 2:

                middleSuffix = "rise";

                break;

            case 3:

                middleSuffix = "sigma";

                break;

            case 4:

                middleSuffix = "quickmacro";

                break;

            case 5:

                middleSuffix = "augustus";

                break;

        }

        return startSuffix + middleSuffix + endSuffix;

    }

}