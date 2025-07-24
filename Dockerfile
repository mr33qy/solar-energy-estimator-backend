# Use official Java 21 image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy all files from backend folder into the container
COPY . .

# Ensure Maven wrapper is executable (fixes permission denied)
RUN chmod +x mvnw

# Build the Spring Boot project without running tests
RUN ./mvnw clean package -DskipTests

# Expose the port (Render sets this using the $PORT env variable)
EXPOSE 8080

# Run the application JAR (matches output from Maven package)
CMD ["sh", "-c", "java -jar target/*.jar"]
