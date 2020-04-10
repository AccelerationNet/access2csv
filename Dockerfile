FROM openjdk:8-jdk-alpine as build

RUN apk update && apk add git maven

ADD ./ /app

WORKDIR /app

RUN mvn clean install

FROM openjdk:8-jre-alpine

COPY --from=build /app/target /app
WORKDIR /app

ENTRYPOINT [ "/app/appassembler/bin/access2csv" ]