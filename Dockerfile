FROM gradle:9.3.0-jdk21

WORKDIR /app

COPY . /app

RUN gradle bootJar

CMD java -jar build/libs/*.jar