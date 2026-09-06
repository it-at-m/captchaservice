# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi10/openjdk-21-runtime:1.24-14@sha256:026353602959376cdf81e0a9477c749865bb61a91fb4a13dc4e71f017ec6fafb

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar
