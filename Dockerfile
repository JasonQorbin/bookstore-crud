FROM openjdk:23-slim
LABEL authors="JasonQorbin"

COPY . /usr/src/myapp/

WORKDIR /usr/src/myapp

CMD ["java", "-cp", "./out/:./lib/mysql-connector-j-8.2.0.jar:./lib/json-20231013.jar", "BookstoreProgram", "-i", "database.ini"]
