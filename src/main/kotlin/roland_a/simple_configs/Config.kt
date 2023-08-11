package roland_a.simple_configs

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

interface Config{
    companion object{
        fun Config.toMap(): Map<String, Any?>{
            val result = mutableMapOf<String,Any?>()

            this.forEachNestedConfig{ name, config->
                result += name to config.toMap()
            }

            this.forEachVar { name, value, _ ->
                result += name to value
            }

            return result
        }

        fun Config.override(
            map: Map<String, Any?>,
            whenKeyIsInvalid: (String)->Unit,
            whenKeyIsUnknown: (String)->Unit,
        ) {
            map.forEach { (k, v) ->
                this.forEachNestedConfig { name, config ->
                    if (name != k) return@forEachNestedConfig

                    if (v !is Map<*, *>) {
                        whenKeyIsInvalid(name)
                    }

                    @Suppress("UNCHECKED_CAST")
                    config.override(v as Map<String, Any?>, whenKeyIsInvalid, whenKeyIsUnknown)

                    return@forEach
                }

                this.forEachVar { name, _, setter ->
                    if (name != k) return@forEachVar

                        setter(v)
                        .ifFalse {
                            whenKeyIsInvalid(name)
                        }

                    return@forEach
                }

                whenKeyIsUnknown(k)
            }
        }

        private inline fun Config.forEachVar(fn: (String, Any?, setter: (Any?)->Boolean)->Unit){
            this::class
            .memberProperties
            .sortedBy { it.name }
            .forEach {property->
                fn(property.name, property.call(this)){
                    trySet(
                        it
                    ){
                        if (property !is KMutableProperty<*>){
                            throw RuntimeException("${property.name} is not mutable")
                        }

                        try{
                            property.setter.call(this, it)
                        }
                        catch (e: IllegalArgumentException){
                            return@trySet false
                        }
                        true
                    }
                }
            }
        }

        private inline fun Config.forEachNestedConfig(fn: (String, Config)->Unit){
            this::class
            .nestedClasses
            .forEach{
                if (it.objectInstance == null){
                    throw RuntimeException("Nested class ${it.simpleName} is not a singleton")
                }
                if (it.objectInstance !is Config){
                    throw RuntimeException("Nested class ${it.simpleName} does not implement Config")
                }
                if (it.simpleName == null){
                    throw RuntimeException("nested class missing name")
                }
                fn(it.simpleName!!, it.objectInstance as Config)
            }
        }

        private fun trySet(value: Any?, setter: (Any?)->Boolean): Boolean{
            setter(value).ifTrue { return true }

            if (value is String){
                @Suppress("NAME_SHADOWING")
                val value = value.trim()

                if (value.lowercase() == "true") setter(true).ifTrue { return true }
                if (value.lowercase() == "false") setter(false).ifTrue { return true }
                if (value.lowercase() == "null") setter(null).ifTrue { return true }

                value.toDoubleOrNull()?.let{ trySet(it, setter) }?.ifTrue { return true }
                value.toIntOrNull()?.let{ trySet(it, setter) }?.ifTrue { return true }
            }
            if (value is Double && floor(value)==ceil(value)){
                setter(value.toInt()).ifTrue { return true }
            }
            if (value is Int){
                setter(value.toDouble()).ifTrue { return true }
            }
            return false
        }

        private inline fun Boolean.ifTrue(fn: ()->Unit){
            if (this) fn()
        }

        private inline fun Boolean.ifFalse(fn: ()->Unit){
            if (!this) fn()
        }
    }
}