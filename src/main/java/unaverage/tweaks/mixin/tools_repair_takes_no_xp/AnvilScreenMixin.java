package unaverage.tweaks.mixin.tools_repair_takes_no_xp;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Redirect(
        method = "drawForeground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/AnvilScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"
        )
    )
    void removeXPBackground(MatrixStack matrixStack, int i1, int i2, int i3, int i4, int i5){
    }

    @Redirect(
        method = "drawForeground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"
        )
    )
    int removeXPNumberOnAnvil(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color){
        return 0;
    }
}
