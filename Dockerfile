FROM maven:3.9.12-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B -DskipTests package
FROM amazoncorretto:17-al2023-headless
WORKDIR /app
COPY --from=build /app/target/tour-2.0.0.jar app.jar
USER 10001
EXPOSE 8086
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java","-jar","/app/app.jar"]
