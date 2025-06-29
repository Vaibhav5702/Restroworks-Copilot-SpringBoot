# Use base image
FROM openjdk

# Set working dir
WORKDIR /app

# Copy Spring Boot fat jar
COPY target/*.jar app.jar

# Run with external config
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
