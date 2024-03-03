package unaverage.tweaks.helper

import net.minecraft.item.ItemStack

var ItemStack.decay: Double
    get(){
        return this.orCreateNbt.getDouble("unaverage_tweaks:total_decay")
    }
    set(value){
        this.orCreateNbt.putDouble("unaverage_tweaks:total_decay", value)
    }