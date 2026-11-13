# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-20@sha256:6d8714d7cc29bee6abcf2fbf302ceb1ac21d59397af5d478dcfc502d33bf1fd3

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
