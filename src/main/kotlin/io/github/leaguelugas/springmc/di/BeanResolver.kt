package io.github.leaguelugas.springmc.di

interface BeanResolver<T : Annotation> {
    fun resolveBean(
        instance: Any,
        annotation: T,
    ): Any
}
