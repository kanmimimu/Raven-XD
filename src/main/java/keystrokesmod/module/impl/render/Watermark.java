package keystrokesmod.module.impl.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.player.ChestStealer;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.utils.ModeOnly;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.IFont;
import keystrokesmod.utility.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Watermark extends Module {
    public static final String VERSION = "2.14.1";
    public static final Map<String, ResourceLocation> WATERMARK = new Object2ObjectOpenHashMap<>();

    public static String customName = "CustomClient";

    public static int posX = 5;
    public static int posY = 5;
    public static int current$minX = 0;
    public static double current$maxX = 0;
    public static int current$minY = 0;
    public static int current$maxY = 0;
    private final ModeSetting mode;
    private final ModeSetting watermarkText;
    private final ModeSetting watermarkPhoto;
    private final ModeSetting font;
    private final ModeSetting theme;
    private final ButtonSetting showVersion;
    private final ButtonSetting lowercase;
    private final ButtonSetting shadow;

    public Watermark() {
        super("Watermark", category.render);
        this.registerSetting(mode = new ModeSetting("Mode", new String[]{"Text", "Photo"}, 0));
        final ModeOnly textMode = new ModeOnly(mode, 0);
        final ModeOnly photoMode = new ModeOnly(mode, 1);

        this.registerSetting(watermarkText = new ModeSetting("Watermark text", new String[]{"Default", "Custom", "Sense", "bS"}, 0, textMode));

        this.registerSetting(watermarkPhoto = new ModeSetting("Watermark photo", new String[]{"Default", "Enders"}, 0, photoMode));
        this.registerSetting(font = new ModeSetting("Font", new String[]{"Minecraft", "Product Sans"}, 0, textMode));
        this.registerSetting(theme = new ModeSetting("Theme", Theme.themes, 0, textMode.extend(new ModeOnly(watermarkText, 2))));
        this.registerSetting(showVersion = new ButtonSetting("Show version", true, textMode));
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false, textMode));
        this.registerSetting(shadow = new ButtonSetting("Shadow", true, textMode));

        for (String s : Arrays.asList("default", "enders")) {
            try (InputStream stream = Objects.requireNonNull(Raven.class.getResourceAsStream("/assets/keystrokesmod/textures/watermarks/" + s + ".png"))) {
                BufferedImage image = ImageIO.read(stream);
                WATERMARK.put(s, Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(s, new DynamicTexture(image)));
            } catch (NullPointerException | IOException ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChest && ChestStealer.noChestRender()) && !(mc.currentScreen instanceof GuiChat) || mc.gameSettings.showDebugInfo)
            return;
        render();
    }

    public void render() {
        switch ((int) mode.getInput()) {
            case 0: // Textモードの場合
                String textToDisplay = "";

                IFont currentFont;
                switch ((int) this.font.getInput()) {
                    default:
                    case 0:
                        currentFont = FontManager.getMinecraft();
                        break;
                    case 1:
                        currentFont = FontManager.productSans20;
                }

                double maxWidth = 0;
                int currentY = posY;

                switch ((int) watermarkText.getInput()) {
                    case 0:
                        textToDisplay = "§r§f§lRaven §bX§9D §7";
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 1:
                        textToDisplay = customName;
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 2:
                        textToDisplay = "§r§f§lRaven§9Sense §rFPS:" + Minecraft.getDebugFPS() + " §r";
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 3:
                        String line1 = "§r§f§lRaven §bb§9S§r"; // 1行目のテキスト
                        String line2Prefix = "§7[§bxia__mc Forked§7]"; // 2行目のプレフィックス

                        // 1行目の描画
                        currentFont.drawString(line1, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = Math.max(maxWidth, currentFont.width(line1));
                        currentY += Math.round(currentFont.height());

                        // ★修正箇所: B の前にも §b を追加★
                        String line2 = line2Prefix;
                        if (showVersion.isToggled()) {
                            line2 += " §bB" + VERSION; // B も水色にし、Bとバージョンの間に空白なし
                        }
                        currentFont.drawString(line2, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = Math.max(maxWidth, currentFont.width(line2));
                        currentY += Math.round(currentFont.height());

                        break;
                }

                if (watermarkText.getInput() != 3) {
                    if (!textToDisplay.isEmpty()) {
                        if (showVersion.isToggled()) {
                            if (((int)watermarkText.getInput() == 2) && !textToDisplay.endsWith(" ")) {
                                textToDisplay += " ";
                            }
                            textToDisplay += VERSION;
                        }
                        if (lowercase.isToggled())
                            textToDisplay = textToDisplay.toLowerCase();

                        currentFont.drawString(textToDisplay, posX, posY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY = posY + (int) Math.round(currentFont.height());
                    }
                }

                current$minX = posX;
                current$maxX = posX + maxWidth;
                current$minY = posY;
                current$maxY = currentY;

                break;
            case 1: // Photoモードの場合
                switch ((int) watermarkPhoto.getInput()) {
                    case 0:
                        RenderUtils.drawImage(WATERMARK.get("default"), posX, posY, 50, 50);
                        current$minX = posX;
                        current$maxX = posX + 50;
                        current$minY = posY;
                        current$maxY = posY + 50;
                        break;
                    case 1:
                        RenderUtils.drawImage(WATERMARK.get("enders"), posX, posY, 150, 45);
                        current$minX = posX;
                        current$maxX = posX + 150;
                        current$minY = posY;
                        current$maxY = posY + 45;
                        break;
                }
                break;
        }
    }
}