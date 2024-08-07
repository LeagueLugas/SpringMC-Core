# SpringMC Framework for Minecraft plugin development

This project provides various solutions to make it easier to develop Minecraft plugins.
Enhance your productivity by solving unnecessary and repetitive code with this framework!

## Install
###### <a href="https://central.sonatype.com/artifact/io.github.leaguelugas/springmc-core" target="_blank">(click to visit maven central)</a>


Maven
```
<dependency>
    <groupId>io.github.leaguelugas</groupId>
    <artifactId>springmc-core</artifactId>
    <version>{version}</version>
</dependency>
```
Gradle
```
implementation 'io.github.leaguelugas:springmc-core:{version}'
```
Gradle (Kotlin)
```
implementation("io.github.leaguelugas:springmc-core:{version}")
```

## Usage
SpringMC automatically stores classes annotated with @Component or its meta-annotations (@Command, @EventListener, @Service) in the Bean container, and the registered beans can be easily used in other beans through dependency injection. 

### Main class
If the @SpringMCMain annotation is not present, the framework will not recognize the main class. Please ensure to add it.
```kotlin
@SpringMCMain
class TestPlugin : SpringMC() {
    override fun onEnable() {
        super.onEnable() // If you override onEnable method, You must call super method  
        logger.info("Hello, world!")
    }
}
```
### Command
You need to implement the SpringCommand interface, and if this interface is not present, an error will occur when loading the plugin.
```kotlin
@Command(
    command = "test",
    // Since it's set to PLAYER_ONLY type, if a sender other than a player executes the command, the errorPlayerOnly() function's error message is sent.
    type = Command.Type.PLAYER_ONLY,
    aliases = ["test2"],
    description = "This is a test command",
    usage = "/test",
    permissions = ["some.required.perm"],
)
class TestCommand : SpringCommand {
    override fun execute(
        sender: CommandSender,
        args: Array<String>,
    ) {
        if (sender is Player) {
            sender.sendMessage("Hello, Player!")
        }
    }

    override fun errorPlayerOnly(): String = "&4You can override message what you want"
}
```
### Event Listener
You need to implement the SpringListener<T : Event> interface, and if this interface is not present, an error will occur when loading the plugin.
```kotlin
@EventListener
class TestListener : SpringListener<InventoryOpenEvent> {
    override fun onEvent(event: InventoryOpenEvent) {
        val player = event.player
        player.sendMessage(MessageUtil.color("Opened inventory"))
    }
}
```
### Dependency Injection
You can follow the DI flow in the following format. Circular references are not allowed, and it is not recommended to inject @Command or @EventListener beans to call functions.
```kotlin
@Service
class UserManager {
    fun returnHello(): String {
        return "Hello"
    }
}

@Command("test")
class TestCommand(
    private val userManager: UserManager,
): SpringCommand {
    override fun execute(sender: CommandSender, args: Array<String>) {
        sender.sendMessage(userManager.returnHello())
    }
}
```
