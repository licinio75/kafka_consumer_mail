# Dockerfile

# Paso 1: Usar una imagen base de Java
FROM openjdk:17-jdk-slim

# Paso 2: Establecer el directorio de trabajo en /app
WORKDIR /app

# Paso 3: Copiar el archivo JAR de la aplicación al contenedor
# Puedes reemplazar app.jar con el nombre específico de cada archivo JAR
COPY target/mail-0.0.1-SNAPSHOT.jar consumer-mail.jar

# Paso 4: Configurar variables de entorno (se pueden sobreescribir en Kubernetes)
ENV KAFKA_BROKER=kafka-service:9092

# Paso 5: Comando para ejecutar la aplicación
CMD ["java", "-jar", "consumer-mail.jar"]
