package unaverage.tweaks.mixin.tools_repair_takes_no_xp;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import unaverage.tweaks.GlobalConfig;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Redirect(
        method = "drawForeground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
        )
    )
    void removeXPBackground(DrawContext instance, int x1, int y1, int x2, int y2, int color){
        if (!GlobalConfig.anvils_takes_zero_xp){
            instance.fill(x1, y1, x2, y2, color);
        }
    }

    @Redirect(
        method = "drawForeground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"
        )
    )
    int removeXPNumberOnAnvil(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color){
        return 0;
    }
}
