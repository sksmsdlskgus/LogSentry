FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/LogSentry-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]

