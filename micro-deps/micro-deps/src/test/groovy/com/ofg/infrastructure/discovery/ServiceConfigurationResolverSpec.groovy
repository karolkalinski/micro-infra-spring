package com.ofg.infrastructure.discovery

import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.*

class ServiceConfigurationResolverSpec extends Specification {

    def 'should parse configuration with path element only'() {
        when:
            def resolver = new ServiceConfigurationResolver(CONFIGURATION_WITH_PATH_ELEM)
        then:
            resolver.basePath == 'pl'
            resolver.microserviceName == 'com/ofg/service'
            resolver.dependencies == ['ping':['path':'com/ofg/ping'],
                                      'pong':['path':'com/ofg/pong']]
    }

    def 'should parse flat configuration'() {
        when:
            def resolver = new ServiceConfigurationResolver(FLAT_CONFIGURATION)
        then:
            resolver.basePath == 'pl'
            resolver.microserviceName == 'com/ofg/service'
            resolver.dependencies == ['ping':['path':'com/ofg/ping'],
                                      'pong':['path':'com/ofg/pong']]
    }

    def 'should fail on missing "this" element'() {
        when:
            new ServiceConfigurationResolver(MISSING_THIS_ELEMENT)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should fail on invalid dependencies'() {
        when:
            new ServiceConfigurationResolver(INVALID_DEPENDENCIES_ELEMENT)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should fail on multiple root elements'() {
        when:
            new ServiceConfigurationResolver(MULTIPLE_ROOT_ELEMENTS)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should set default values for missing optional elements'() {
        when:
            def resolver = new ServiceConfigurationResolver(ONLY_REQUIRED_ELEMENTS)
        then:
            resolver.dependencies == [:]
    }

    def 'should provide load balancer type for given dependency name'() {
        given:
            def resolver = new ServiceConfigurationResolver(LOAD_BALANCING_DEPENDENCIES)
        expect:
            resolver.getLoadBalancerTypeOf('com/ofg/ping') == 'sticky'
            resolver.getLoadBalancerTypeOf('com/ofg/pong') == null
    }

    def 'should throw exception when looking for load balancer type of a dependency with unknown name'() {
        given:
            def resolver = new ServiceConfigurationResolver(LOAD_BALANCING_DEPENDENCIES)
        when:
            resolver.getLoadBalancerTypeOf('com/ofg/unknown')
        then:
            def ex = thrown(DependencyNotDefinedInConfigException)
            ex.message == 'Unable to find dependency with path \'com/ofg/unknown\' in microservice configuration.'
    }
}
