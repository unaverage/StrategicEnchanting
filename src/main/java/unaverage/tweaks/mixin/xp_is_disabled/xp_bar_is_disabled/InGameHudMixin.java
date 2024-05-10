package unaverage.tweaks.mixin.xp_is_disabled.xp_bar_is_disabled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("ConstantConditions")
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Final @Shadow
    private MinecraftClient client;


    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    void cancelDrawingBar1(DrawContext instance, Identifier texture, int x, int y, int width, int height){
    }

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIII)V"
        )
    )
    void cancelDrawingBar2(DrawContext instance, Identifier texture, int i, int j, int k, int l, int x, int y, int width, int height){
    }

    @Redirect(
        method = "renderExperienceLevel",
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
        method = "renderFood",
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

    @Redirect(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
        )
    )
    private static void moveArmorBarLower(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        if (MinecraftClient.getInstance().player.getJumpingMount() != null){
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
    void moveMountHealthBarDown(DrawContext instance, Identifier texture, int x, int y, int width, int height){
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
    void moveAirBarDown(DrawContext instance, Identifier texture, int x, int y, int width, int height){
        if (this.client.player.getJumpingMount() != null){
            instance.drawGuiTexture(texture, x, y, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y+7, width, height);
    }
}
