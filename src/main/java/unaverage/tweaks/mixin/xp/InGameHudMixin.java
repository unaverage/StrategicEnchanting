package unaverage.tweaks.mixin.xp;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract LivingEntity getRiddenEntity();

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        )
    )
    void cancelDrawingBar(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height){
        if (this.getRiddenEntity() != null){
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
            return;
        }
    }

    @Redirect(
        method = "renderExperienceBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"
        )
    )
    int cancelDrawingLevels(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color){
        return 0;
    }

    @Redirect(
        method = "drawHeart",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        )
    )
    void moveHealthBarLower(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height){
        if (this.getRiddenEntity() != null){
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(matrixStack, x, y+7, u, v, width, height);
    }

    @Redirect(
        method = "renderMountHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        )
    )
    void moveRiddenHealthBarDown(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height){
        if (this.getRiddenEntity() != null){
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(matrixStack, x, y+7, u, v, width, height);
    }

    @Redirect(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
        )
    )
    void moveHungerBarLower(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height){
        if (this.getRiddenEntity() != null){
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(matrixStack, x, y+7, u, v, width, height);
    }


}
