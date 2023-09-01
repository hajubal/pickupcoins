FROM openjdk:17-alpine
LABEL description="coin pick server"
MAINTAINER hajubal@gmail.com

ADD ./pickupcoins.jar /

RUN mkdir /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/pickupcoins.jar", "--spring.profiles.active=dev"]
