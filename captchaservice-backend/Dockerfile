# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-2.1778501187@sha256:6da5275dbb0148f3821e3f73fb3116522bee6f2a0d55e446f506607c75f77b4b

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar