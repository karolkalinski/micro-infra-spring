package com.ofg.infrastructure.discovery.util

import org.apache.curator.x.discovery.strategies.RandomStrategy
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy
import org.apache.curator.x.discovery.strategies.StickyStrategy
import spock.lang.Specification
import spock.lang.Unroll

class ProviderStrategyFactorySpec extends Specification {

    @Unroll
    def 'should create #strategyProviderName provider for #strategyName'() {
        given:
            ProviderStrategyFactory factory = new ProviderStrategyFactory()
        when:
            def strategyProvider = factory.createProviderStartegy(strategyName)
        then:
            strategyProvider.class.simpleName == strategyProviderName
        where:
            strategyName  | strategyProviderName
            'sticky'      | StickyStrategy.class.simpleName
            'STICKY'      | StickyStrategy.class.simpleName
            'random'      | RandomStrategy.class.simpleName
            'RANDOM'      | RandomStrategy.class.simpleName
            'roundrobin'  | RoundRobinStrategy.class.simpleName
            'ROUNDROBIN'  | RoundRobinStrategy.class.simpleName
            'round_robin' | RoundRobinStrategy.class.simpleName
            'round-robin' | RoundRobinStrategy.class.simpleName
            null          | RoundRobinStrategy.class.simpleName
            ''            | RoundRobinStrategy.class.simpleName
            'unknown'     | RoundRobinStrategy.class.simpleName
    }
}
