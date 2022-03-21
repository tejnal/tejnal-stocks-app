FROM openjdk:11-jdk-slim as build
RUN mkdir -p /usr/app-api
COPY ./target/tejnal-stock-exchange-0.0.1-SNAPSHOT.jar /usr/app-api
WORKDIR /usr/app-api

ENTRYPOINT ["java","-jar","tejnal-stock-exchange-0.0.1-SNAPSHOT.jar"]
