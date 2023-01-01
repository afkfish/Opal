FROM gradle:7.6-jdk19 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM eclipse-temurin:19-jdk-jammy
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/Opal.jar

ENTRYPOINT ["java","-jar","/app/Opal.jar"]