package keystrokesmod;


import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;


import keystrokesmod.keystroke.KeySrokeRenderer;

import keystrokesmod.keystroke.KeyStrokeConfigGui;

import keystrokesmod.keystroke.keystrokeCommand;

import keystrokesmod.module.Module;

import keystrokesmod.clickgui.ClickGui;

import keystrokesmod.module.ModuleManager;

import keystrokesmod.script.ScriptManager;

import keystrokesmod.utility.*;

import keystrokesmod.utility.clicks.CPSCalculator;

import keystrokesmod.utility.i18n.I18nManager;

import keystrokesmod.utility.interact.moveable.MoveableManager;

import keystrokesmod.utility.profile.Profile;

import keystrokesmod.utility.profile.ProfileManager;

import keystrokesmod.utility.render.progress.ProgressManager;

import lombok.Getter;

import net.minecraft.client.Minecraft;

import net.minecraftforge.client.ClientCommandHandler;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.common.Mod.EventHandler;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;


@Mod(

        modid = "keystrokes",

        name = "KeystrokesMod",

        version = "KMV5",

        acceptedMinecraftVersions = "[1.8.9]"

)

public class Raven {

    public static boolean debugger = false;

    public static Minecraft mc = Minecraft.getMinecraft();

    private static KeySrokeRenderer keySrokeRenderer;

    private static boolean isKeyStrokeConfigGuiToggled;

    private static final ScheduledExecutorService ex = Executors.newScheduledThreadPool(4);

    @Getter

    public static ModuleManager moduleManager;

    public static ClickGui clickGui;

    public static ProfileManager profileManager;

    public static ScriptManager scriptManager;

    public static Profile currentProfile;

    public static BadPacketsHandler badPacketsHandler;

    public static ProgressManager progressManager;

    @SuppressWarnings({"unused", "FieldMayBeFinal"})

    private boolean loaded = false;


    public static int moduleCounter;

    public static int settingCounter;

    // ★追加: 最終的に設定したいウィンドウタイトルを定数として定義
    private static final String CUSTOM_WINDOW_TITLE = "Raven B4";


    public Raven() {

        moduleManager = new ModuleManager();

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 起動直後のタイトル設定 (最初の瞬間だけはこれでも適用される)
        Display.setTitle(CUSTOM_WINDOW_TITLE);
    }


    @EventHandler

    public void init(FMLInitializationEvent ignored) {

        Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));

        ClientCommandHandler.instance.registerCommand(new keystrokeCommand());

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new DebugInfoRenderer());

        MinecraftForge.EVENT_BUS.register(new CPSCalculator());

        MinecraftForge.EVENT_BUS.register(new KeySrokeRenderer());

        MinecraftForge.EVENT_BUS.register(new Ping());

        MinecraftForge.EVENT_BUS.register(badPacketsHandler = new BadPacketsHandler());

        MinecraftForge.EVENT_BUS.register(progressManager = new ProgressManager());

        Reflection.getFields();

        Reflection.getMethods();

        moduleManager.register();

        scriptManager = new ScriptManager();

        keySrokeRenderer = new KeySrokeRenderer();

        clickGui = new ClickGui();

        profileManager = new ProfileManager();

        profileManager.loadProfiles();

        profileManager.loadProfile();

        Reflection.setKeyBindings();

        scriptManager.loadScripts();

        scriptManager.loadScripts();

        MinecraftForge.EVENT_BUS.register(ModuleManager.tower);

        MinecraftForge.EVENT_BUS.register(ModuleManager.rotationHandler);

        MinecraftForge.EVENT_BUS.register(ModuleManager.slotHandler);

        MinecraftForge.EVENT_BUS.register(ModuleManager.dynamicManager);

        MinecraftForge.EVENT_BUS.register(new MoveableManager());

        MinecraftForge.EVENT_BUS.register(profileManager);


        I18nManager.init();

        AutoUpdate.init();

    }


    @SubscribeEvent

    public void onTick(@NotNull ClientTickEvent e) {

        if (e.phase == Phase.END) {

            try {
                // ★ここから追加・修正するコード
                // ウィンドウタイトルが意図したものでなければ再設定
                if (!Display.getTitle().equals(CUSTOM_WINDOW_TITLE)) {
                    Display.setTitle(CUSTOM_WINDOW_TITLE);
                }
                // ★ここまで追加・修正するコード


                if (Utils.nullCheck()) {

                    if (Reflection.sendMessage) {

                        Utils.sendMessage("&cThere was an error, relaunch the game.");

                        Reflection.sendMessage = false;

                    }

                    for (Module module : getModuleManager().getModules()) {

                        if (mc.currentScreen instanceof ClickGui) {

                            module.guiUpdate();

                        }


                        if (module.isEnabled()) {

                            module.onUpdate();

                        }

                    }

                }


                if (isKeyStrokeConfigGuiToggled) {

                    isKeyStrokeConfigGuiToggled = false;

                    mc.displayGuiScreen(new KeyStrokeConfigGui());

                }

            } catch (Throwable ignored) {

            }

        }

    }


    @SubscribeEvent

    public void onRenderTick(TickEvent.@NotNull RenderTickEvent event) {

        if (event.phase == Phase.END) {

            try {

                if (Utils.nullCheck()) {

                    for (Module module : getModuleManager().getModules()) {

                        if (mc.currentScreen == null && module.canBeEnabled()) {

                            module.keybind();

                        }

                    }

                    synchronized (Raven.profileManager.profiles) {

                        for (Profile profile : Raven.profileManager.profiles) {

                            if (mc.currentScreen == null) {

                                profile.getModule().keybind();

                            }

                        }

                    }

                    for (Module module : Raven.scriptManager.scripts.values()) {

                        if (mc.currentScreen == null) {

                            module.keybind();

                        }

                    }

                }

            } catch (Throwable ignored) {

            }

        }

    }


    public static ScheduledExecutorService getExecutor() {

        return ex;

    }


    public static KeySrokeRenderer getKeyStrokeRenderer() {

        return keySrokeRenderer;

    }


    public static void toggleKeyStrokeConfigGui() {

        isKeyStrokeConfigGuiToggled = true;

    }

}