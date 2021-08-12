FROM luke19/spring-base-image:1628781512

ENV DB_ANSPAREN_HOST=tmp
ENV DB_ANSPAREN_DB=tmp
ENV DB_ANSPAREN_USER=tmp
ENV DB_ANSPAREN_PASS=tmp
ENV ANSPAREN_LOG_LEVEL=DEBUG
ENV AUTH_URL=test1234


COPY ./target/ansparen.jar /app/app.jar

ENTRYPOINT ["java", "-Djava.io.tmpdir=/app/tmp" ,"-jar", "app.jar"]

EXPOSE 8080