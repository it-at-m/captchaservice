# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-21@sha256:d6391cfd2fc03435b22db0245bb9dd638138475234d16bd97d204eb4e27395c3

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
