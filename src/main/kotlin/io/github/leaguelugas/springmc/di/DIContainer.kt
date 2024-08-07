package io.github.leaguelugas.springmc.di

import io.github.leaguelugas.springmc.SpringMC
import io.github.leaguelugas.springmc.di.annotations.Command
import io.github.leaguelugas.springmc.di.annotations.Component
import io.github.leaguelugas.springmc.di.annotations.EventListener
import io.github.leaguelugas.springmc.di.annotations.Service
import io.github.leaguelugas.springmc.di.annotations.SpringMCMain
import io.github.leaguelugas.springmc.di.resolvers.CommandResolver
import io.github.leaguelugas.springmc.di.resolvers.ComponentResolver
import io.github.leaguelugas.springmc.di.resolvers.EventListenerResolver
import io.github.leaguelugas.springmc.di.resolvers.ServiceResolver
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.jar.JarFile
import java.util.logging.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

@Suppress("UNCHECKED_CAST")
class DIContainer(
    private val plugin: SpringMC,
    private val pluginFile: File,
) {
    private val beans: MutableMap<String, Any> = mutableMapOf()
    private val creationStack: MutableSet<KClass<*>> = mutableSetOf()
    private val beanResolvers: MutableMap<KClass<out Annotation>, BeanResolver<out Annotation>> =
        mutableMapOf(
            Component::class to ComponentResolver(),
            Service::class to ServiceResolver(),
            Command::class to CommandResolver(plugin),
            EventListener::class to EventListenerResolver(plugin),
        )

    fun scanBeans() {
        plugin.logger.log(Level.SEVERE, "Scanning beans")
        scanAllClassesFromJarFile()
        plugin.logger.log(Level.SEVERE, "Beans scanned")
    }

    private fun scanAllClassesFromJarFile() {
        val jarFile = JarFile(pluginFile)
        val classLoader = plugin.javaClass.classLoader

        val entries = jarFile.entries()
        var mainClass: KClass<JavaPlugin>? = null
        val classes = mutableListOf<KClass<*>>()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name.endsWith(".class") && !entry.isDirectory) {
                val className = entry.name.replace("/", ".").removeSuffix(".class")
                if (className.startsWith(plugin.javaClass.packageName)) {
                    val clazz = Class.forName(className, true, classLoader).kotlin
                    if (clazz.hasAnnotation<SpringMCMain>()) {
                        mainClass = clazz as KClass<JavaPlugin>
                    }
                    getComponentAnnotation(clazz)?.run {
                        classes.add(clazz)
                    }
                }
            }
        }

        jarFile.close()

        if (mainClass == null) {
            plugin.logger.log(Level.FINEST, "No main class found in the jar file")
        } else {
            classes.forEach { clazz ->
                createAndRegisterInstance(clazz)
            }

            plugin.javaClass.declaredFields.forEach { field ->
                if (beans.containsKey(field.type.kotlin.qualifiedName)) {
                    field.isAccessible = true
                    field.set(plugin, beans[field.type.kotlin.qualifiedName])
                }
            }
            plugin.logger.info("Loaded ${beans.size} beans")
        }
    }

    private fun createAndRegisterInstance(clazz: KClass<*>) {
        if (beans.containsKey(clazz.qualifiedName)) return
        if (creationStack.contains(clazz)) {
            plugin.logger.info("Circular dependency detected")
            println("╔══════════╗")
            println(
                creationStack.joinToString("\n⇑          ⇓\n") {
                    "║       ${it.simpleName} defined in [${it.java.canonicalName}]"
                },
            )
            println("╚══════════╝")
            throw Exception("Circular dependency detected for class ${clazz.qualifiedName}")
        }
        creationStack.add(clazz)

        val annotation = getComponentAnnotation(clazz)!!
        val constructor = clazz.primaryConstructor ?: clazz.constructors.first()
        val parameters =
            constructor.parameters
                .map { parameter ->
                    val dependencyClass = parameter.type.classifier as KClass<*>
                    if (plugin.javaClass == dependencyClass.java) {
                        return@map plugin
                    }
                    getComponentAnnotation(dependencyClass) ?: throw Exception(
                        "Cannot create bean of type ${clazz.qualifiedName} with invalid parameter [${parameter.name}: ${parameter.type.classifier}]",
                    )
                    if (!beans.containsKey(dependencyClass.qualifiedName)) {
                        createAndRegisterInstance(dependencyClass)
                    }
                    beans[dependencyClass.qualifiedName!!]
                }.toTypedArray()

        val resolver = beanResolvers[annotation.annotationClass]
        if (resolver != null) {
            val instance = constructor.call(*parameters)
            (resolver as BeanResolver<Annotation>).resolveBean(instance, annotation).run {
                register(clazz.qualifiedName!!, this)
            }
        }

        creationStack.remove(clazz)
    }

    private fun getComponentAnnotation(clazz: KClass<*>): Annotation? =
        clazz.annotations.find { annotation ->
            return@find annotation.annotationClass == Component::class ||
                annotation.annotationClass.annotations.any { metaAnnotation ->
                    metaAnnotation.annotationClass == Component::class
                }
        }

    private fun register(
        key: String,
        instance: Any,
    ) {
        beans[key] = instance
    }

    fun <T> resolve(key: String): T = beans[key] as T
}
