package io.github.leaguelugas.springmc.di.annotations

@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val command: String,
    val type: Type = Type.BOTH,
    val description: String = "",
    val usage: String = "",
    val aliases: Array<String> = [],
    val permissions: Array<String> = [],
) {
    enum class Type {
        CONSOLE_ONLY,
        PLAYER_ONLY,
        BOTH,
    }
}
