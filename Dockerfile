# 1. Image de base avec OpenJDK 25
FROM openjdk:25-jdk-slim

# 2. Définir le répertoire de travail
WORKDIR /app

# 3. Copier le pom.xml et le dossier src
COPY pom.xml .
COPY src ./src

# 4. Installer Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 5. Construire l'application
RUN mvn clean package

# 6. Définir le point d'entrée pour lancer l'application
CMD ["mvn", "javafx:run"]
