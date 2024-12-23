# Stage 1: Build the application using Maven and OpenJDK 17
FROM openjdk:17-jdk AS build
WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml and source code
COPY pom.xml .
COPY src src

# Set execution permission for Maven wrapper
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final image with OpenJDK 17
FROM openjdk:17-jdk
VOLUME /tmp

# Copy the JAR file from the build stage
COPY --from=build /app/target/ozitre-0.0.1-SNAPSHOT.jar ozitre-0.0.1-SNAPSHOT.jar

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "/ozitre-0.0.1-SNAPSHOT.jar"]

# Expose the application port
EXPOSE 8080
