# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-25-runtime:1.24-22@sha256:727dae67a171bdcba9e7f79881994f8959e2b0f5eecfaeb18ce695efedcd5dfa

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
