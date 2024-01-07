FROM openjdk:23-slim
LABEL authors="JasonQorbin"

COPY ./lib/ /usr/src/myapp/lib
COPY ./src/ /usr/src/myapp/src
COPY ./out/ /usr/src/myapp/out
COPY ./build.sh /usr/src/myapp/build.sh
COPY ./run.sh /usr/src/myapp/run.sh

WORKDIR /usr/src/myapp

CMD ["java", "-cp", "./out/:./lib/mysql-connector-j-8.2.0.jar", "BookstoreProgram"]
