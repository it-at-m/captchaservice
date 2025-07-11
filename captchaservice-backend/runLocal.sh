#!/bin/bash
docker compose up -d
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local"
