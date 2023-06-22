FROM openjdk:11
EXPOSE 8080
ADD target/registration-service-0.0.1.jar registration-service-0.0.1.jar
ENTRYPOINT ["java","-jar","/registration-service-0.0.1.jar"]