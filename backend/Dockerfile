# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-16@sha256:5e194bae22a94e56aeb47dfe487331557a1cec5e52b9b17df76c1f7892774144

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
