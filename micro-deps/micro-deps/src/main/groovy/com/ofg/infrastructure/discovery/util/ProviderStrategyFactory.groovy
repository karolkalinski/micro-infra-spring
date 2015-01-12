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

    private static final Map<String, Closure> STRATEGY_CREATORS = [(STICKY)         :   { return new StickyStrategy<>(new RoundRobinStrategy()) },
                                                                   (RANDOM)         :   { return new RandomStrategy<>()},
                                                                   (ROUND_ROBIN)    :   { return new RoundRobinStrategy<>() }]

    ProviderStrategy createProviderStartegy(String originalStrategyName) {
        String strategyName = removeRoundRobinDelimiter(originalStrategyName)
        if (STRATEGY_CREATORS.containsKey(strategyName)) {
            return STRATEGY_CREATORS[strategyName]() as ProviderStrategy
        } else {
            throw new UnknownLoadBalancerStrategyName("Unknown load balancer strategy name: '$originalStrategyName'. Supported names are sticky, random, roundrobin, round_robin and round-robin.")
        }
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
