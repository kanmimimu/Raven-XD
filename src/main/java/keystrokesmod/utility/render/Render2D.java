package keystrokesmod.utility.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Render2D {

    // 四角形を描画するメソッド (既存)
    public static void drawRect(float x, float y, float width, float height, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x, (double)(y + height), 0.0D).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0D).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0D).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    // ★★ 新しいメソッド: 外枠を描画するメソッド ★★
    public static void drawOutlineRect(float x, float y, float width, float height, float thickness, int color) {
        // 上の線
        drawRect(x, y, width, thickness, color);
        // 下の線
        drawRect(x, y + height - thickness, width, thickness, color);
        // 左の線
        drawRect(x, y + thickness, thickness, height - 2 * thickness, color);
        // 右の線
        drawRect(x + width - thickness, y + thickness, thickness, height - 2 * thickness, color);
    }


    // 角丸四角形を描画するメソッド (既存 - 未使用なら削除しても構いません)
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawRect(x + radius, y, width - 2 * radius, height, color); // 中央の長方形
        drawRect(x, y + radius, width, height - 2 * radius, color); // 縦方向の長方形

        drawCircleSegment(x + radius, y + radius, radius, 180, 270, color); // 左上
        drawCircleSegment(x + width - radius, y + radius, radius, 270, 360, color); // 右上
        drawCircleSegment(x + width - radius, y + height - radius, radius, 0, 90, color); // 右下
        drawCircleSegment(x + radius, y + height - radius, radius, 90, 180, color); // 左下
    }

    private static void drawCircleSegment(float x, float y, float radius, int startAngle, int endAngle, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y, 0.0D).endVertex();
        for (int i = startAngle; i <= endAngle; i++) {
            double angle = Math.toRadians(i);
            worldrenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0.0D).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}