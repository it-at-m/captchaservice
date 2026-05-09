# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-2.1777977956@sha256:c90cac905559f62d1b63705f3eb48411465ce9966e1fe84f7ad620aa95011f80

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar