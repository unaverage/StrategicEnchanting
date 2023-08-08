package unaverage.tweaks.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.IllegalArgumentException
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
                result += name to value!!
            }

            return result
        }

        fun Config.override(
            map: Map<String, Any?>,
            logWhenInvalid: (String) -> Unit,
            logWhenNotFound: (String)->Unit,
        ) {
            map.forEach { (k, v) ->
                this.forEachNestedConfig { name, config ->
                    if (name != k) return@forEachNestedConfig

                    if (v !is Map<*, *>) {
                        logWhenInvalid(name)
                    }

                    @Suppress("UNCHECKED_CAST")
                    config.override(v as Map<String, Any?>, logWhenInvalid, logWhenNotFound)

                    return@forEach
                }

                this.forEachVar { name, _, setter ->
                    if (name != k) return@forEachVar

                    trySet(v, setter)
                        .ifFalse {
                            logWhenInvalid(name)
                        }

                    return@forEach
                }

                logWhenNotFound(k)
            }
        }

        fun Config.writeToFile(file: File){
            fun Map<String,Any?>.toText(): String {
                return GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(this)!!
            }
            fun String.toFile(file: File){
                if (!file.exists()){
                    file.createNewFile()
                }

                file.writeText(this)
            }

            this
            .toMap()
            .toText()
            .toFile(file)
        }

        fun Config.overwriteFromFile(
            file: File,
            logWhenInvalid: (String) -> Unit,
            logWhenNotFound: (String)->Unit,
        ) {
            fun String.toMap(): Map<String, Any?> {
                @Suppress("UNCHECKED_CAST")
                return Gson().fromJson(this, Map::class.java) as Map<String, Any?>
            }

            file
            .readText()
            .toMap()
            .let {
                this.override(it, logWhenInvalid, logWhenNotFound)
            }
        }

        private inline fun Config.forEachVar(fn: (String, Any?, setter: (Any?)->Boolean)->Unit){
            this::class
            .memberProperties
            .sortedBy { it.name }
            .forEach {property->
                fn(property.name, property.call()){
                    try{
                        if (property !is KMutableProperty<*>){
                            throw RuntimeException("${property.name} is not mutable")
                        }

                        try{
                            property.setter.call(it)
                        }
                        catch (e: IllegalArgumentException){
                            throw RuntimeException("field ${property.name} not jvm field", e)
                        }
                    }
                    catch (e: IllegalArgumentException){
                        return@fn true
                    }
                    false
                }
            }
        }

        private inline fun Config.forEachNestedConfig(fn: (String, Config)->Unit){
            this::class
            .nestedClasses
            .forEach{
                if (it.objectInstance == null){
                    throw RuntimeException("nested class ${it.simpleName} not singleton")
                }
                if (it.objectInstance !is Config){
                    throw RuntimeException("nested class ${it.simpleName} does not implement Config")
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