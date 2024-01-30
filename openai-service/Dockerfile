FROM openjdk:17
ADD target/backend-app.jar backend-app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar" , "backend-app.jar"]