package unaverage.tweaks.helper

import com.mojang.serialization.MapDecoder
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.predicate.item.CustomDataPredicate

const val key = "unaverage_tweaks:total_decay"

var ItemStack.decay: Double
    get(){
        var data = this.get(DataComponentTypes.CUSTOM_DATA)

        if (data == null){
            return 0.0
        }

        return data.nbt.getDouble(key)
    }
    set(value){
        this.apply(
            DataComponentTypes.CUSTOM_DATA,
            NbtComponent.DEFAULT
        ){
            it.apply {
                it.putDouble(key, value)
            }
        }
    }