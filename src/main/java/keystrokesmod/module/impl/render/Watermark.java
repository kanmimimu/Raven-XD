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
import keystrokesmod.utility.render.Render2D;
import keystrokesmod.utility.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Watermark extends Module {
    public static final String VERSION = "3.2.4";
    public static final Map<String, ResourceLocation> WATERMARK = new Object2ObjectOpenHashMap<>();

    public static String customName = "CustomClient";
    public static String username = Minecraft.getMinecraft().getSession().getUsername();

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
    private final ButtonSetting lowercase;
    private final ButtonSetting shadow;

    // ★★ Sense2モード用の設定項目 ★★
    private final ModeSetting sense2Theme;
    private final ButtonSetting showUsername;
    private final ButtonSetting showPing;
    private final ButtonSetting showFPS;
    private final ButtonSetting showServerIP;
    private final ButtonSetting showVersion;


    public Watermark() {
        super("Watermark", category.render);
        this.registerSetting(mode = new ModeSetting("Mode", new String[]{"Text", "Photo"}, 0));
        final ModeOnly textMode = new ModeOnly(mode, 0);
        final ModeOnly photoMode = new ModeOnly(mode, 1);

        this.registerSetting(watermarkText = new ModeSetting("Watermark text", new String[]{"Default", "Custom", "Sense", "Sense2", "bS"}, 0, textMode));

        this.registerSetting(watermarkPhoto = new ModeSetting("Watermark photo", new String[]{"Default", "Enders"}, 0, photoMode));
        this.registerSetting(font = new ModeSetting("Font", new String[]{"Minecraft", "Product Sans"}, 0, textMode));
        this.registerSetting(theme = new ModeSetting("Theme", Theme.themes, 0, textMode.extend(new ModeOnly(watermarkText, 2)))); // SenseモードのTheme

        // ★★ Sense2モード用の設定項目を登録 ★★
        final ModeOnly sense2ModeOnly = new ModeOnly(watermarkText, 3);
        this.registerSetting(sense2Theme = new ModeSetting("Theme", Theme.themes, 0, sense2ModeOnly));

        // ★★ `showVersion`をSense2設定の順序に合わせてここに移動 ★★
        this.registerSetting(showVersion = new ButtonSetting("Show version", true, textMode)); // この設定はSense2モードでも使用されます

        // ★★ 残りのSense2モード用の設定項目を登録 ★★
        this.registerSetting(showUsername = new ButtonSetting("Show username", true, sense2ModeOnly));
        this.registerSetting(showPing = new ButtonSetting("Show ping", true, sense2ModeOnly));
        this.registerSetting(showFPS = new ButtonSetting("Show FPS", true, sense2ModeOnly));
        this.registerSetting(showServerIP = new ButtonSetting("Show server IP", true, sense2ModeOnly));

        // ★★ lowercase設定の表示条件を変更 ★★
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false, textMode.extend(new ModeOnly(watermarkText, 0, 1, 2, 4))));
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
        if (mc.thePlayer == null || (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChest && ChestStealer.noChestRender()) && !(mc.currentScreen instanceof GuiChat)) || mc.gameSettings.showDebugInfo)
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
                    case 0: // Default
                        textToDisplay = "§r§f§lRaven §bX§9D §7";
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 1: // Custom
                        textToDisplay = customName;
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 2: // Sense
                        textToDisplay = "§r§f§lRaven§9Sense §rFPS:" + Minecraft.getDebugFPS() + " §r";
                        currentFont.drawString(textToDisplay, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY += Math.round(currentFont.height());
                        break;
                    case 3: // Sense2 - ★★ ここからが新しいロジック ★★
                        int borderThickness = 1;
                        int internalPaddingX = 4;
                        int internalPaddingY = 2;

                        // 各パーツの文字列を準備
                        String ravenText = "§f§lRaven ";
                        String senseText = "Sense";
                        String versionText = "§7B" + VERSION;
                        String usernameText = "§7" + mc.getSession().getUsername();

                        int ping = 0;
                        if (!mc.isSingleplayer() && mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
                            ping = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
                        }
                        String pingText = "§7" + ping + "ms";
                        String fpsText = "§7" + Minecraft.getDebugFPS() + " FPS";

                        ServerData currentServerData = mc.getCurrentServerData();
                        String serverIP = (currentServerData != null) ? currentServerData.serverIP : "Singleplayer";
                        String ipText = "§7" + serverIP;
                        String separator = " §7| ";

                        // 表示するコンポーネントの文字列リストを、指定された順序で作成
                        List<String> textsToRender = new ArrayList<>();
                        textsToRender.add("CLIENT_NAME"); // ★★ 常にクライアント名を追加 ★★
                        if (showVersion.isToggled()) {
                            textsToRender.add(versionText);
                        }
                        if (showUsername.isToggled()) {
                            textsToRender.add(usernameText);
                        }
                        if (showPing.isToggled()) {
                            textsToRender.add(pingText);
                        }
                        if (showFPS.isToggled()) {
                            textsToRender.add(fpsText);
                        }
                        if (showServerIP.isToggled()) {
                            textsToRender.add(ipText);
                        }

                        // 何も表示しない場合はここで終了
                        if (textsToRender.isEmpty()) {
                            current$minX = posX;
                            current$maxX = posX;
                            current$minY = posY;
                            current$maxY = posY;
                            break;
                        }

                        // 全体の幅を計算
                        StringBuilder fullTextForWidth = new StringBuilder();
                        for (int i = 0; i < textsToRender.size(); i++) {
                            if (i > 0) {
                                fullTextForWidth.append(separator);
                            }
                            String text = textsToRender.get(i);
                            if (text.equals("CLIENT_NAME")) {
                                fullTextForWidth.append(ravenText).append(senseText);
                            } else {
                                fullTextForWidth.append(text);
                            }
                        }
                        int combinedTextWidth = (int) currentFont.width(fullTextForWidth.toString().replaceAll("§.", ""));

                        // 背景と外枠の計算と描画
                        int innerBackgroundWidth = combinedTextWidth + 2 * internalPaddingX;
                        int innerBackgroundHeight = (int) Math.round(currentFont.height()) + 2 * internalPaddingY;
                        int totalWidth = innerBackgroundWidth + 2 * borderThickness;
                        int totalHeight = innerBackgroundHeight + 2 * borderThickness;

                        Render2D.drawOutlineRect(posX, posY, totalWidth, totalHeight, borderThickness, new Color(0x414141).getRGB());
                        Render2D.drawRect(posX + borderThickness, posY + borderThickness, innerBackgroundWidth, innerBackgroundHeight, new Color(0, 0, 0, 255).getRGB());

                        // テキストをパーツごとに描画
                        float currentTextX = posX + borderThickness + internalPaddingX;
                        float currentTextY = posY + borderThickness + internalPaddingY;
                        int unifiedThemeColor = Theme.getGradient((int) sense2Theme.getInput(), 0); // 新しい統一テーマを使用

                        for (int i = 0; i < textsToRender.size(); i++) {
                            if (i > 0) {
                                currentFont.drawString(separator, currentTextX, currentTextY, unifiedThemeColor, shadow.isToggled());
                                currentTextX += currentFont.width(separator);
                            }
                            String text = textsToRender.get(i);

                            if (text.equals("CLIENT_NAME")) {
                                currentFont.drawString(ravenText, currentTextX, currentTextY, unifiedThemeColor, shadow.isToggled());
                                currentTextX += currentFont.width(ravenText);
                                currentFont.drawString(senseText, currentTextX, currentTextY, unifiedThemeColor, shadow.isToggled());
                                currentTextX += currentFont.width(senseText);
                            } else {
                                currentFont.drawString(text, currentTextX, currentTextY, unifiedThemeColor, shadow.isToggled());
                                currentTextX += currentFont.width(text);
                            }
                        }

                        // 下線を描画 (統一テーマの色を使用)
                        float underlineThickness = 1.0f;
                        float underlineYOffset = 1.0f;
                        float underlineY = (posY + borderThickness + internalPaddingY) + (float) currentFont.height() + underlineYOffset;
                        Render2D.drawRect(posX + borderThickness, underlineY, innerBackgroundWidth, underlineThickness, unifiedThemeColor);

                        // ウォーターマーク全体のサイズを更新
                        current$minX = posX;
                        current$maxX = posX + totalWidth;
                        current$minY = posY;
                        current$maxY = posY + totalHeight;
                        break;
                    // ★★ ここまでが新しいSense2のロジック ★★
                    case 4: // 旧bSモード
                        String line1 = "§r§f§lRaven §bb§9S§r";
                        String line2Prefix = "§7[§bxia__mc Forked§7]";

                        currentFont.drawString(line1, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = Math.max(maxWidth, currentFont.width(line1));
                        currentY += Math.round(currentFont.height());

                        String line2 = line2Prefix;
                        if (showVersion.isToggled()) {
                            line2 += " §bB" + VERSION;
                        }
                        currentFont.drawString(line2, posX, currentY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = Math.max(maxWidth, currentFont.width(line2));
                        currentY += Math.round(currentFont.height());
                        break;
                }

                if (watermarkText.getInput() != 3 && watermarkText.getInput() != 4) {
                    if (!textToDisplay.isEmpty()) {
                        if (showVersion.isToggled()) {
                            if (((int) watermarkText.getInput() == 2) && !textToDisplay.endsWith(" ")) {
                                textToDisplay += " ";
                            }
                            if (((int) watermarkText.getInput() == 0 || (int) watermarkText.getInput() == 1 || (int) watermarkText.getInput() == 2)) {
                                textToDisplay += VERSION;
                            }
                        }
                        if (lowercase.isToggled())
                            textToDisplay = textToDisplay.toLowerCase();

                        currentFont.drawString(textToDisplay, posX, posY, Theme.getGradient((int) theme.getInput(), 0), shadow.isToggled());
                        maxWidth = currentFont.width(textToDisplay);
                        currentY = posY + (int) Math.round(currentFont.height());
                    }
                }

                if (watermarkText.getInput() != 3) {
                    current$minX = posX;
                    current$maxX = posX + maxWidth;
                    current$minY = posY;
                    current$maxY = currentY;
                }
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