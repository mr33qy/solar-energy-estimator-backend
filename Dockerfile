# Use official Java 21 runtime
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy everything into container
COPY . .

# Build the Spring Boot project using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Expose the port (Render injects it into $PORT)
EXPOSE 8080

# Run the JAR file (Render handles the port variable automatically)
CMD ["sh", "-c", "java -jar target/*.jar"]
