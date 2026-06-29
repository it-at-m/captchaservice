# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24-2.1782293370@sha256:b8be612ac873a91d2a9e05937453358e719b155dc0304c070ffe0e94cd6aa5e2

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar