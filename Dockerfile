FROM openjdk:17-alpine
LABEL version="1.0.0"
LABEL description="coin pick server"
MAINTAINER hajubal@gmail.com

ADD ./build/libs/pickupcoins-1.0.0.jar /

RUN mkdir /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/pickupcoins-1.0.0.jar"]
