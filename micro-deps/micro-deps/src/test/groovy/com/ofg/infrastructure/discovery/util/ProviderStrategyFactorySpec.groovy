package com.ofg.infrastructure.discovery.util

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
            'sticky'      | 'StickyStrategy'
            'random'      | 'RandomStrategy'
            'roundrobin'  | 'RoundRobinStrategy'
            'round_robin' | 'RoundRobinStrategy'
            'round-robin' | 'RoundRobinStrategy'
            null          | 'RoundRobinStrategy'
            ''            | 'RoundRobinStrategy'
            'unknown'     | 'RoundRobinStrategy'
    }
}
