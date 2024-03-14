package unaverage.tweaks.mixin.flint_and_steel_can_be_used_with_separate_hands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unaverage.tweaks.helper.HelperKt;

import java.util.Random;

import static unaverage.tweaks.GlobalConfig.flint_and_steel_can_be_used_with_separate_hands.*;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow public abstract Item asItem();

    @Inject(
        method = "useOnBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    void setFireWithFlintAndSteelWithSeparateHands(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        if (this.asItem() != Items.FLINT && this.asItem() != Items.IRON_INGOT) return;

        PlayerEntity player = context.getPlayer();

        if (player == null) return;

        Hand handWithFlint = null;
        Hand handWithIron = null;
        for (Hand hand: new Hand[]{Hand.MAIN_HAND, Hand.OFF_HAND}){
            if (player.getStackInHand(hand).isOf(Items.FLINT)){
                handWithFlint = hand;
            }
            if (player.getStackInHand(hand).isOf(Items.IRON_INGOT)){
                handWithIron = hand;
            }
        }

        if (handWithFlint == null) return;
        if (handWithIron == null) return;

        var result = Items.FLINT_AND_STEEL.useOnBlock(context);
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(result);
            return;
        }

        if (!player.isCreative() && !context.getWorld().isClient()) {
            var rng = new Random();

            var hands = new Hand[]{
                handWithFlint,
                handWithIron
            };
            var chances = new double[]{
                getFlint_consume_chance(),
                getIron_ingot_consume_chance()
            };

            for (int i = 0; i < 2; i++){
                var chance = chances[i];
                var hand = hands[i];

                if (HelperKt.passesChance(chance, rng)) {
                    player.getStackInHand(hand).decrement(1);
                }
            }
        }

        cir.setReturnValue(result);
    }
}
