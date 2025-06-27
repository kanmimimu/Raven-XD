package keystrokesmod.mixins.impl.render;

import keystrokesmod.module.impl.render.Animations; // ★★ Animationsモジュールをインポート ★★
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "shouldCauseReequipAnimation", at = @At("HEAD"), cancellable = true, remap = false)
    public void keystrokesmod$modifyReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged, CallbackInfoReturnable<Boolean> cir) { // ★★ メソッド名をあなたのModの接頭辞に変更 ★★
        // Animations.onlyReequipOnSlotSwitch が有効な場合のみ、このロジックを適用
        if (Animations.onlyReequipOnSlotSwitch.isToggled()) {
            // PolyfrostのitemSwitchMode 0 に相当する「常に無効」のロジックは、
            // 今回の「スロット切り替え時のみ」機能の趣旨と異なるため、ここでは実装しない
            // もし「常に無効」も選択肢として含めるなら、AnimationsモジュールにModeSettingを追加し、
            // その値に応じてcir.setReturnValue(false)を呼び出すようにする

            // PolyfrostのitemSwitchMode 1 に相当するロジックを移植
            // 「スロットが切り替わった場合」または
            // 「fixReequipAnimationが無効な場合」または
            // 「GUIコンテナが開いている場合」にアニメーションを許可
            cir.setReturnValue(!Animations.fixReequipAnimation.isToggled() || slotChanged || Minecraft.getMinecraft().currentScreen instanceof GuiContainer);
        }
    }
}