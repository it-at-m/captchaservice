# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-15@sha256:caed17a0814988d0aa1a67f1e03c1965d6e9e1414a754ba60d6c0da1a7b2695e

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
