# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-17@sha256:d4ab41b179e2a10f7fd47a55a4d9644ac6e98ec998dbaaf85b3b0ebf63adcdd1

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
