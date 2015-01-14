package com.ofg.infrastructure.discovery.util

import groovy.transform.CompileStatic
import org.apache.curator.x.discovery.ProviderStrategy
import org.apache.curator.x.discovery.strategies.RandomStrategy
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy
import org.apache.curator.x.discovery.strategies.StickyStrategy

@CompileStatic
class ProviderStrategyFactory {

    static final String STICKY = 'sticky'
    static final String RANDOM = 'random'
    static final String ROUND_ROBIN = 'roundrobin'

    private static final Closure ROUND_ROBIN_CREATOR = { return new RoundRobinStrategy<>() }
    private static final Map<String, Closure> STRATEGY_CREATORS = [(STICKY)         :   { return new StickyStrategy<>(new RoundRobinStrategy()) },
                                                                   (RANDOM)         :   { return new RandomStrategy<>()},
                                                                   (ROUND_ROBIN)    :   ROUND_ROBIN_CREATOR,
                                                                   ''               :   ROUND_ROBIN_CREATOR].withDefault { ROUND_ROBIN_CREATOR }

    ProviderStrategy createProviderStartegy(String originalStrategyName) {
        String strategyName = toAcceptableName(originalStrategyName)
        return STRATEGY_CREATORS[strategyName]() as ProviderStrategy
    }

    private String toAcceptableName(String originalStrategyName) {
        def nonNullName = originalStrategyName ? originalStrategyName.toLowerCase() : ''
        return removeRoundRobinDelimiter(nonNullName)
    }

    private String removeRoundRobinDelimiter(String originalStrategyName) {
        if (originalStrategyName ==~ /round(-|_)?robin/) {
            return ROUND_ROBIN
        } else {
            return originalStrategyName
        }
    }

}

class UnknownLoadBalancerStrategyName extends Exception {

    UnknownLoadBalancerStrategyName(String message) {
        super(message)
    }

}
