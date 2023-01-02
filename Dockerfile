FROM gradle:7.6-jdk19 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
RUN export GRADLE_HOME=/home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM eclipse-temurin:19-jdk-jammy
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/*all.jar /app/Opal.jar

ENTRYPOINT ["java","-jar","/app/Opal.jar"]
