# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-19@sha256:a8045b2744d75d5b41e967943e475928a7c67ef91f3af7669c5a360056d535e9

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
