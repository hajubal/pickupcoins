FROM openjdk:17-alpine
LABEL version="1.0.3"
LABEL description="coin pick server"
MAINTAINER hajubal@gmail.com

ADD ./pickupcoins-1.0.3.jar /

RUN mkdir /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/pickupcoins-1.0.3.jar", "--spring.profiles.active=dev"]
