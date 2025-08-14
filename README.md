# SpringMC Framework

[![Maven Central](https://img.shields.io/maven-central/v/io.github.leaguelugas/springmc-core.svg)](https://central.sonatype.com/artifact/io.github.leaguelugas/springmc-core)

Spring Boot의 개발 방식에서 영감을 받아, 마인크래프트 플러그인 개발의 생산성을 향상시키기 위해 만들어진 코틀린 기반 프레임워크입니다.
어노테이션 기반의 DI(의존성 주입)와 다양한 모듈을 통해 반복적이고 불필요한 코드를 줄여보세요!

## 설치 (Installation)

<details>
<summary>Maven, Gradle 설정</summary>

**Maven**
```xml
<dependency>
    <groupId>io.github.leaguelugas</groupId>
    <artifactId>springmc-core</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

**Gradle (Groovy)**
```groovy
implementation 'io.github.leaguelugas:springmc-core:LATEST_VERSION'
```

**Gradle (Kotlin)**
```kotlin
implementation("io.github.leaguelugas:springmc-core:LATEST_VERSION")
```
</details>

---

## 주요 기능 (Features)

SpringMC는 `@Component` 계열 어노테이션(`@Service`, `@Command`, `@EventListener` 등)이 붙은 클래스를 자동으로 스캔하여 DI 컨테이너(Bean)에 등록합니다. 등록된 객체는 다른 클래스에서 주입받아 쉽게 사용할 수 있습니다.

### 1. 의존성 주입 (Dependency Injection)

플러그인의 메인 클래스에 `@SpringMCMain` 어노테이션을 붙여주세요. 이 어노테이션이 없으면 프레임워크가 정상적으로 동작하지 않습니다.

```kotlin
@SpringMCMain
class MyPlugin : SpringMC() {
    override fun onEnable() {
        super.onEnable() // onEnable을 오버라이드 할 경우, 반드시 super.onEnable()을 호출해야 합니다.
        logger.info("MyPlugin has been enabled!")
    }
}
```

`@Service` 어노테이션으로 비즈니스 로직을 정의하고, 다른 곳에서 생성자나 필드를 통해 주입받을 수 있습니다.

```kotlin
@Service
class UserManager {
    fun getUser(name: String): User {
        // ...
    }
}

// 생성자 주입 예시
@Component
class GameManager(private val userManager: UserManager) {
    fun startGame(playerName: String) {
        val user = userManager.getUser(playerName)
        // ...
    }
}
```

### 2. 커맨드 (Commands)

`@Command` 어노테이션 하나로 커맨드를 간단하게 등록할 수 있습니다.

```kotlin
@Command(
    command = "hello",
    description = "Sends a greeting.",
    usage = "/hello",
    permission = "myplugin.hello",
    aliases = ["hi", "greeting"]
)
class HelloCommand : SpringCommand {
    override fun execute(sender: CommandSender, args: Array<String>) {
        sender.sendMessage("Hello, world!")
    }
}
```

### 3. 이벤트 리스너 (Event Listeners)

`@EventListener`를 클래스에 붙여 이벤트를 처리하는 리스너를 등록합니다.

```kotlin
@EventListener
class PlayerJoinListener : SpringListener {

    @EventHandler // Bukkit의 @EventHandler 사용
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Welcome to the server!")
    }
}
```

### 4. 스케줄러 (Schedulers)

`@Scheduled` 어노테이션을 사용해 동기/비동기 작업을 간단하게 예약할 수 있습니다.

- `delay`: 최초 실행까지의 대기 시간 (tick)
- `period`: 반복 실행 간격 (tick)
- `async`: true로 설정 시 비동기 실행

```kotlin
@Component
class BroadcastTask {

    // 20틱(1초) 후 최초 실행, 1200틱(1분)마다 반복
    @Scheduled(delay = 20L, period = 1200L)
    fun broadcastMessage() {
        Bukkit.broadcastMessage("This is a scheduled broadcast!")
    }

    // 100틱(5초) 후 비동기로 한 번 실행
    @Scheduled(delay = 100L, async = true)
    fun runAsyncTask() {
        // ... 오래 걸리는 비동기 작업
    }
}
```