# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.23-6@sha256:2b6fcee4f6e1d4803dfcec6b7b25f6c7966080a2fb8da5175645886df9a7765b

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar