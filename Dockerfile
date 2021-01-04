FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 10222
ADD /build/libs/bank-0.0.1-SNAPSHOT.jar bank-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/bank-0.0.1-SNAPSHOT.jar"]