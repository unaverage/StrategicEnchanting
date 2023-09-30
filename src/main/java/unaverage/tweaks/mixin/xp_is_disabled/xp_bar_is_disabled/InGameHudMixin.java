package unaverage.tweaks.mixin.xp_is_disabled.xp_bar_is_disabled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("ConstantConditions")
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final @Shadow
    private MinecraftClient client;

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    void cancelDrawingBar(DrawContext instance, Identifier texture, int x, int y, int width, int height){
    }

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"
        )
    )
    int cancelDrawingLevels(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow){
        return 0;
    }

    @Redirect(
        method = "drawHeart",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    void moveHealthBarLower(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        if (this.client.player.getJumpingMount() != null){
            instance.drawGuiTexture(texture, x, y, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y+7, width, height);
    }

    @Redirect(
        method = "renderMountHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    void moveRiddenHealthBarDown(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        if (this.client.player.getJumpingMount() != null){
            instance.drawGuiTexture(texture, x, y, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y+7, width, height);
    }

    @Redirect(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    void moveHungerBarLower(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        if (this.client.player.getJumpingMount() != null){
            instance.drawGuiTexture(texture, x, y, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y+7, width, height);
    }
}
