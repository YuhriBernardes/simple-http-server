# Gerar o JAR
FROM clojure:openjdk-11-tools-deps AS build-env
# ENV SERVER_PORT="3000"
ENV AUTH_TOKEN="469a31fd9d773110f14057baecdcdd25"
ENV APP_ENV=":prod"

ARG APPLICATION_ID=iqueh09183y2rohr-91hfoiuhdf01h23r-193u2hjdsf
ARG CLOJURE_RUNNER=tools-deps

WORKDIR /build

COPY deps.edn ./
COPY build build
COPY resources resources
COPY src src

# RUN clojure -R:build -e 'nil'
RUN clojure -A:build

# Start App
FROM openjdk:11.0.2-jdk-oraclelinux7 as app-env

WORKDIR /var/app

COPY --from=build-env /build/target/lib/lib lib
COPY --from=build-env /build/target/classes classes
COPY --from=build-env /build/resources resources

EXPOSE 3000

CMD java -cp "classes:lib/*:resources" simple_http_server.core
