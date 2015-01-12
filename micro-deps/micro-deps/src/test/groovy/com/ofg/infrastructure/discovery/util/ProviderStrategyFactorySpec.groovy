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
    }

    def 'should throw exception for unknown strategy name'() {
        when:
            new ProviderStrategyFactory().createProviderStartegy('unknown')
        then:
            def ex = thrown(UnknownLoadBalancerStrategyName)
            ex.message == 'Unknown load balancer strategy name: \'unknown\'. Supported names are sticky, random, roundrobin, round_robin and round-robin.'
    }

}
