package unaverage.tweaks.mixin.xp;

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
import unaverage.tweaks.GlobalConfig;

@SuppressWarnings("ConstantConditions")
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final @Shadow
    private MinecraftClient client;

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
        )
    )
    void cancelDrawingBar(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height){
        if (!GlobalConfig.xp.disable_bar){
            instance.drawTexture(texture, x, y, u, v, width, height);
        }
    }

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"
        )
    )
    int cancelDrawingLevels(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow){
        if (!GlobalConfig.xp.disable_bar){
            return instance.drawText(textRenderer, text, x, y, color, shadow);
        }

        return 0;
    }

    @Redirect(
        method = "drawHeart",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
        )
    )
    void moveHealthBarLower(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height){
        if (!GlobalConfig.xp.disable_bar){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }
        if (this.client.player.getJumpingMount() != null){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(texture, x, y+7, u, v, width, height);
    }

    @Redirect(
        method = "renderMountHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
        )
    )
    void moveRiddenHealthBarDown(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height){
        if (!GlobalConfig.xp.disable_bar){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }
        if (this.client.player.getJumpingMount() != null){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(texture, x, y+7, u, v, width, height);
    }

    @Redirect(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
        )
    )
    void moveHungerBarLower(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height){
        if (!GlobalConfig.xp.disable_bar){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }
        if (this.client.player.getJumpingMount() != null){
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(texture, x, y+7, u, v, width, height);
    }
}
