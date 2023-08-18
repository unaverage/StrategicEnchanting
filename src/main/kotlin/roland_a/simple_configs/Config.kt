package roland_a.simple_configs

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

interface Config{
    companion object{
        fun Config.toMap(): Map<String, Any>{
            val result = mutableMapOf<String,Any>()

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
                            config.override(v as Map<String, Any>, whenKeyIsInvalid, whenKeyIsUnknown)
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
                        val default = property.tryCall(this)

                        val value = v.convert(default)
                        if (value == null){
                            whenKeyIsInvalid(key)
                            return
                        }

                        val flag = property.trySet(this, value)
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

        private fun KMutableProperty1<Config, *>.trySet(config: Config, value: Any): Boolean{
            try {
                this.setter.call(config, value)
            } catch (e: IllegalArgumentException) {
                try {
                    this.setter.call(value)
                } catch (e: IllegalArgumentException) {
                    return false
                }
            }
            catch (e: InvalidValueException){
                return false
            }
            return true
        }

        private fun KMutableProperty1<Config, *>.tryCall(c: Config): Any {
            try {
                return this.call() as Any
            }
            catch (e: IllegalArgumentException){
                return this.call(c) as Any
            }
        }

        @Suppress("UNCHECKED_CAST", "FoldInitializerAndIfToElvis", "LiftReturnOrAssignment")
        private fun Any?.convert(default: Any): Any?{
            when(default){
                is Map<*,*>->{
                    val first = default.values.firstOrNull()
                    if (first == null) throw RuntimeException("maps in default value must not be empty")

                    if (default.containsValue(null)) throw RuntimeException("nulls are not allowed")
                    default as Map<*, Any>

                    if (this is Map<*,*>) {
                        if (this.containsValue(null)) return null
                        this as Map<*, Any>

                        return this.mapValues { (_, it)->
                            it.convert(first) ?: return null
                        }
                    }

                    return null
                }
                is List<*>->{
                    val first = default.firstOrNull()
                    if (first == null) throw RuntimeException("maps in default value must not be empty")

                    if (default.contains(null)) throw RuntimeException("nulls are not allowed")
                    default as List<Any>

                    when (this){
                        is Iterable<*>->{
                            if (this.contains(null)) return null
                            this as Iterable<Any>

                            return this
                                .map {
                                    it.convert(first) ?: return null
                                }
                                .toList()
                        }
                        else ->{
                            return null
                        }
                    }
                }
                is Set<*>->{
                    val first = default.firstOrNull()
                    if (first == null) throw RuntimeException("maps in default value must not be empty")

                    if (default.contains(null)) throw RuntimeException("nulls are not allowed")
                    default as Set<Any>

                    when (this){
                        is Iterable<*>->{
                            if (this.contains(null)) return null
                            this as Iterable<Any>

                            return this
                                .map {
                                    it.convert(first) ?: return null
                                }
                                .toSet()
                        }
                        else ->{
                            return null
                        }
                    }
                }
                is String->{
                    when (this){
                        is String->return this
                        else->return null
                    }
                }
                is Double->{
                    if (this is Double) return this
                    if (this is Int) return this.toDouble()

                    return null
                }
                is Int->{
                    when (this){
                        is Int->{
                            return this
                        }
                        is Double->{
                            if (!this.isRounded()) return null

                            return this.toInt()
                        }
                        else->{
                            return null
                        }
                    }
                }
                is Boolean->{
                    when (this){
                        is Boolean->return this
                        else->return null
                    }
                }
                else->{
                    throw RuntimeException("type ${default::class} is not supported")
                }
            }
        }

        private fun Double.isRounded(): Boolean{
            return floor(this) == ceil(this)
        }
    }
}