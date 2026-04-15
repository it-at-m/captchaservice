# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-2.1776048101@sha256:291fbe97f178d3bf1f99e4eeae8851f636cb1e8a2e12c48819a6c136fb8d4eb6

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar