package unaverage.tweaks.mixin.sugarcane_no_longer_spawns_on_ice;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleBlockStateProvider.class)
public interface SimpleBlockStateProviderAccessor {
    @Accessor
    BlockState getState();
}
