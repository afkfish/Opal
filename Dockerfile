FROM gradle:7.6-jdk19 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:19-jdk
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/*all.jar /app/Opal.jar
CMD ls /app

ENTRYPOINT ["java","-jar","/app/Opal.jar"]