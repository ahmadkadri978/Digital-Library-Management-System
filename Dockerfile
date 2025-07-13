# Use official lightweight OpenJDK image
From eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy built JAR file into the container
COPY target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

