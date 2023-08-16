package roland_a.simple_configs

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

interface Config{
    companion object{
        fun Config.toMap(): Map<String, Any?>{
            val result = mutableMapOf<String,Any?>()

            this
            .variables()
            .forEach { (k, v) ->
                result += k to v.tryCall(this)
            }

            this
            .nestedConfigs()
            .forEach { (k, v) ->
                result += k to v.toMap()
            }

            return result.toSortedMap()
        }

        fun Config.override(
            map: Map<String, Any?>,
            whenKeyIsInvalid: (String)->Unit,
            whenKeyIsUnknown: (String)->Unit,
        ) {
            map.forEach { (key, v) ->
                this
                .nestedConfigs()
                .toList()
                .firstOrNull {
                        (checkedKey,_)-> checkedKey == key
                }
                ?.let {(_, config)->
                    if (v !is Map<*,*>) {
                        whenKeyIsInvalid(key)
                    }
                    else {
                        config.override(v as Map<String, Any?>, whenKeyIsInvalid, whenKeyIsUnknown)
                    }
                }
                ?.let {
                    return@forEach
                }

                this
                .variables()
                .toList()
                .firstOrNull{
                        (checkedKey, _)-> checkedKey==key
                }
                ?.let { (_, property)->
                    val flag = property.trySet(this, v)

                    if (!flag) {
                        whenKeyIsInvalid(key)
                    }
                }
                ?.let {
                    return@forEach
                }

                whenKeyIsUnknown(key)
            }
        }

        private fun Config.variables(): Map<String, KMutableProperty1<Config, *>> {
            return this::class
                .memberProperties
                .onEach {
                    if (it !is KMutableProperty1<*, *>){
                        throw RuntimeException("${it.name} is not mutable")
                    }
                }
                .sortedBy {
                    it.name
                }
                .associate {
                    it.name to it as KMutableProperty1<Config, *>
                }
        }

        private fun Config.nestedConfigs(): Map<String, Config> {
            return this::class
                .nestedClasses
                .associate{
                    if (it.objectInstance == null){
                        throw RuntimeException("Nested class ${it.simpleName} is not a singleton")
                    }
                    if (it.objectInstance !is Config){
                        throw RuntimeException("Nested class ${it.simpleName} does not implement Config")
                    }
                    if (it.simpleName == null){
                        throw RuntimeException("nested class missing name")
                    }

                    it.simpleName!! to it.objectInstance as Config
                }
        }

        private fun KMutableProperty1<Config, *>.trySet(config: Config, value: Any?): Boolean{
            val setter = fun(it: Any?): Boolean {
                try {
                    this.setter.call(config, it)
                } catch (e: IllegalArgumentException) {
                    try {
                        this.setter.call(it)
                    } catch (e: IllegalArgumentException) {
                        return false
                    }
                }
                return true
            }

            setter(value).ifTrue { return true }

            if (value is String){
                @Suppress("NAME_SHADOWING")
                val value = value.trim()

                if (value.lowercase() == "true") setter(true).ifTrue { return true }
                if (value.lowercase() == "false") setter(false).ifTrue { return true }
                if (value.lowercase() == "null") setter(null).ifTrue { return true }

                value.toDoubleOrNull()?.let{ this.trySet(config, it) }?.ifTrue { return true }
                value.toIntOrNull()?.let{ this.trySet(config, it) }?.ifTrue { return true }
            }
            if (value is Double && floor(value)==ceil(value)){
                setter(value.toInt()).ifTrue { return true }
            }
            if (value is Int){
                setter(value.toDouble()).ifTrue { return true }
            }
            if (value is Iterable<*>){
                setter(value.toList()).ifTrue { return true }
                setter(value.toSet()).ifTrue { return true }
            }

            return false
        }

        private fun KMutableProperty1<Config, *>.tryCall(c: Config): Any? {
            try {
                return this.call()
            }
            catch (e: IllegalArgumentException){
                return this.call(c)
            }
        }

        private inline fun Boolean.ifTrue(fn: ()->Unit){
            if (this) fn()
        }
    }
}