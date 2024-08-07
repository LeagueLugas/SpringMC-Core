package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.di.BeanResolver
import io.github.leaguelugas.springmc.di.annotations.Component

class ComponentResolver : BeanResolver<Component> {
    override fun resolveBean(
        instance: Any,
        annotation: Component,
    ): Any = instance
}
