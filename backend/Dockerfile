# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-12@sha256:5524249c8281a4d3f196909dda3cf2c59487100ee99e5859cb04737d7017522b

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
