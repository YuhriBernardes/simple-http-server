# Simple HTTP Server

A clojure HTTP Server. This application use a database on memory and don't have any data validation (schema).

To start the app, follow de instructions bellow:

## Run locally with clojure

``` shell
clj -m simple-http-server.core arg1
```

- arg1: App environment. Must be one of "prod" "test" "dev"

## Run on docker

1. Build docker image

``` shell
docker build . --no-cache -t simple-server:0.0.1
```

2. Run docker container

``` shell
docker run --rm -d -p <your-port>:3000 simple-server:0.0.1
```

### Docker envs:

| Name        | Default | Description                                                                                |
|:-----------:|:-------:|:-------------------------------------------------------------------------------------------|
| APP_ENV     | "prod"  | App environment. If "dev" or "test" was choosen, the app will insert mock data on database |
| SERVER_PORT | "3000"  | Which port will be used by the server                                                      |

# Routes

## GET `/person`

List all persons

Request example:

``` shell
curl --request GET \
  --url http://localhost:3030/person
```

## GET `/person/:id`

Get a person by id

``` shell
curl --request GET \
  --url http://localhost:3030/person/341234=13kbsadf89132=1234obds07f1
```
## POST `/person`

Insert a new person on database.

``` shell
curl --request POST \
  --url http://localhost:3030/person \
  --header 'content-type: application/json' \
  --data '{"name" : "Mary Mine",
"age": 23}'
```

## PUT `/person/:id`

Update a person by id

``` shell
curl --request PUT \
  --url http://localhost:3030/person/b3456d4a-29b7-42d2-b742-65fadf6b6f83 \
  --header 'content-type: application/json' \
  --data '{"age": 100}'
```

## DELETE `/person/:id`

Delete a person by id

``` shell
curl --request DELETE \
  --url http://localhost:3030/person/b3456d4a-29b7-42d2-b742-65fadf6b6f83
```
