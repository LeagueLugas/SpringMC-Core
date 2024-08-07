package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.di.BeanResolver
import io.github.leaguelugas.springmc.di.annotations.Service

class ServiceResolver : BeanResolver<Service> {
    override fun resolveBean(
        instance: Any,
        annotation: Service,
    ): Any = instance
}
