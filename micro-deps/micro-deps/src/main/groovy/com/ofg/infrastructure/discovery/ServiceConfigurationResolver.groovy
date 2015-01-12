package com.ofg.infrastructure.discovery

import groovy.json.JsonSlurper

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.*

class ServiceConfigurationResolver {

    final String basePath
    private final Object parsedConfiguration

    ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        (basePath, parsedConfiguration) = parseConfig(configuration)
    }

    private static List parseConfig(String config) {
        Map json = new JsonSlurper().parseText(config)
        checkThatJsonHasOneRootElement(json)
        String basePath = json.keySet().first()
        def serviceMetadata = json[basePath]
        checkThatServiceMetadataContainsValidElements(serviceMetadata)
        convertFlatDependenciesToMapFormat(serviceMetadata)
        validateDependencyEntries(serviceMetadata)
        setDefaultsForMissingOptionalElements(serviceMetadata)
        serviceMetadata.dependencies = convertDependenciesToMapWithNameAsKey(serviceMetadata.dependencies)
        return [basePath, serviceMetadata]
    }

    private static void checkThatJsonHasOneRootElement(Map json) {
        if (json.size() != 1) {
            throw new InvalidMicroserviceConfigurationException('multiple root elements')
        }
    }

    private static Map convertDependenciesToMapWithNameAsKey(Map dependencies) {
        Map convertedDependencies = [:]
        dependencies.each {convertedDependencies[it.key] = it.value}
        return convertedDependencies
    }

    private static void checkThatServiceMetadataContainsValidElements(serviceMetadata) {
        if (!(serviceMetadata.this && serviceMetadata.this instanceof String)) {
            throw new InvalidMicroserviceConfigurationException('invalid or missing "this" element')
        }
        if (serviceMetadata.dependencies && !(serviceMetadata.dependencies instanceof Map)) {
            throw new InvalidMicroserviceConfigurationException('invalid "dependencies" element')
        }
    }

    private static void convertFlatDependenciesToMapFormat(serviceMetadata) {
        serviceMetadata.dependencies.each {
            if (it.value instanceof String) {
                it.value = [(PATH):it.value]
            }
        }
    }

    private static void validateDependencyEntries(serviceMetadata) {
        List invalidDependenciesNames = serviceMetadata.dependencies
                .findAll { !(it.value instanceof Map) }
                .collect { key, value -> key }
        if (!invalidDependenciesNames.isEmpty()) {
            throw new InvalidMicroserviceConfigurationException("following dependencies have invalid format: " +
                    " $invalidDependenciesNames (Check documentation for details.)")
        }
    }

    private static void setDefaultsForMissingOptionalElements(serviceMetadata) {
        if (serviceMetadata.dependencies == null) {
            serviceMetadata.dependencies = [:]
        }
        serviceMetadata.dependencies.each {
            if (!it.value[REQUIRED]) {
                it.value[REQUIRED] == false
            }
        }
    }

    String getMicroserviceName() {
        return parsedConfiguration.this
    }

    Map getDependencies() {
        return parsedConfiguration.dependencies
    }

    String getDependencyConfigByName(String dependencyName) {
        return parsedConfiguration[dependencyName]
    }

    String getLoadBalancerTypeOf(String dependencyPath) {
        def dependency = getDependencyConfigByPath(dependencyPath)
        if (dependency) {
            return dependency['load-balancer']
        } else {
            throw new DependencyNotDefinedInConfigException("Unable to find dependency with path '$dependencyPath' in microservice configuration.")
        }
    }

    private Map getDependencyConfigByPath(String dependencyPath) {
        parsedConfiguration.dependencies.findResult { if (it.value['path'] == dependencyPath) return it.value }
    }
}
