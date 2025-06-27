package keystrokesmod;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;

import keystrokesmod.keystroke.KeySrokeRenderer;
import keystrokesmod.module.impl.render.Watermark;
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
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent; // ★追加
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
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

    // ★再定義: 最終的に設定したいウィンドウタイトルを定数として定義
    private static final String CUSTOM_WINDOW_TITLE = "Raven XD " + Watermark.VERSION;

    // アイコンファイルのパスはそのまま残す
    private static final ResourceLocation ICON_PATH_16 = new ResourceLocation("keystrokesmod:icons/icon.png");
    private static final ResourceLocation ICON_PATH_32 = new ResourceLocation("keystrokesmod:icons/icon.png");

    public Raven() {
        moduleManager = new ModuleManager();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // ★削除: 起動直後のタイトル設定を削除 (postInitで処理されるため)
        // Display.setTitle(CUSTOM_WINDOW_TITLE);

        // ウィンドウアイコン設定は必要なので残す
        setWindowIcon();
    }

    @EventHandler
    public void init(FMLInitializationEvent ignored) {
        Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));
        ClientCommandHandler.instance.registerCommand(new keystrokeCommand());
        MinecraftForge.EVENT_BUS.register(this); // これでpostInitも登録される
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

    // ★追加: FMLPostInitializationEvent ハンドラ
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // MinecraftとForgeの初期化がほぼ完了した後にタイトルを設定
        Display.setTitle(CUSTOM_WINDOW_TITLE);
    }

    @SubscribeEvent
    public void onTick(@NotNull ClientTickEvent e) {
        if (e.phase == Phase.END) {
            try {
                // ★削除: ここでのタイトル再設定ロジックは削除
                // if (!Display.getTitle().equals(CUSTOM_WINDOW_TITLE)) {
                //     Display.setTitle(CUSTOM_WINDOW_TITLE);
                // }

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
                // 例外を無視していますが、開発中はログ出力などを検討してください
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
                // 例外を無視していますが、開発中はログ出力などを検討してください
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

    private void setWindowIcon() {
        try {
            IResource res16 = mc.getResourceManager().getResource(ICON_PATH_16);
            IResource res32 = mc.getResourceManager().getResource(ICON_PATH_32);

            BufferedImage image16 = ImageIO.read(res16.getInputStream());
            BufferedImage image32 = ImageIO.read(res32.getInputStream());

            BufferedImage resizedImage16 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            resizedImage16.getGraphics().drawImage(image16, 0, 0, 16, 16, null);

            BufferedImage resizedImage32 = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            resizedImage32.getGraphics().drawImage(image32, 0, 0, 32, 32, null);

            ByteBuffer buffer16 = convertImageData(resizedImage16);
            ByteBuffer buffer32 = convertImageData(resizedImage32);

            Display.setIcon(new ByteBuffer[]{buffer16, buffer32});

        } catch (IOException e) {
            System.err.println("Failed to set window icon: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while setting window icon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ByteBuffer convertImageData(BufferedImage image) {
        ByteBuffer byteBuffer;
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        byteBuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));
                byteBuffer.put((byte) (pixel & 0xFF));
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        byteBuffer.flip();
        return byteBuffer;
    }
}