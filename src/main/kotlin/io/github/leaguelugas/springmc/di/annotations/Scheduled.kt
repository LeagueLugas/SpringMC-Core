package io.github.leaguelugas.springmc.di.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scheduled(
    val delay: Long = 0L,
    val period: Long = 0L,
    val async: Boolean = false
)
