package unaverage.tweaks.mixin.composters_take_in_more_items;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.GlobalConfig;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ComposterBlock.class)
public class ComposterMixin {
    @Unique
    private static final AtomicBoolean init = new AtomicBoolean(false);

    @Inject(
        method = "onUse",
        at = @At("HEAD")
    )
    void injectNewItems(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        if (init.getAndSet(true)) return;

        for (var e: GlobalConfig.composters_take_in_more_items.getItems().entrySet()){
            var itemName = e.getKey();
            var chance = e.getValue();

            var item = unaverage.tweaks.HelperKt.fromId(
                itemName,
                Registries.ITEM
            );

            if (item == null) continue;

            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(
                item,
                (float)(double)chance
            );
        }
    }
}
