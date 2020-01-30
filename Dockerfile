FROM clojure:openjdk-11-tools-deps AS build-env

# TODO ENVS
ENV SERVER_PORT="3000"
ENV AUTH_TOKEN="469a31fd9d773110f14057baecdcdd25"
ENV APP_ENV=":prod"

ARG APPLICATION_ID=iqueh09183y2rohr-91hfoiuhdf01h23r-193u2hjdsf
ARG CLOJURE_RUNNER=tools-deps

WORKDIR /usr/src/server

COPY ./* ./

EXPOSE 3000

CMD ["clj" "-m" "simple-http-server.core" "$APP_ENV"]
