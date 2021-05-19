#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS builder
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests


#
# Packaging stage
#
FROM adoptopenjdk/openjdk13:x86_64-alpine-jre13u-nightly

WORKDIR /APP

COPY --from=builder /home/app/target/ansparen.jar app.jar

# run container as non root
RUN apk update && addgroup -S familygroup && adduser -S familyuser -G familygroup
USER familyuser

ENTRYPOINT java -jar app.jar

EXPOSE 8080