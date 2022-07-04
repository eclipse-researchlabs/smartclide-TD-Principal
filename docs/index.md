# Smartclide TD Principal
SmartCLIDE TD Principal Backend Component

## Preconditions to build and run TD Principal

To build and run the backend service of TD Principal, the following software is required:

- Java (at least version 11)
- Apache Maven (at least version 3.2+)
- Docker (for building and running the final image)

## How to build TD Principal

TD Principal can be built using maven with the following command:

```shell
mvn install
```

In order to build a Docker image of the service that can be deployed, the following commands can be used:

```shell
mvn install
docker build -t ${IMAGE_NAME:IMAGE_TAG} .
```

More specifically:

```shell
mvn install
docker build -t smartclide-td-principal-backend:latest .
```

## How to run TD Principal

All the images of this component can be found [here](https://github.com/eclipse-researchlabs/smartclide-TD-Principal/pkgs/container/smartclide%2Ftdprincipal).

You can run the backend service with the following command:

```shell
docker run -p 8555:8555 smartclide-td-principal-backend:latest
```

## How to configure TD Principal

**application.properties**

The main properties of this backend service can be found [here](https://github.com/eclipse-researchlabs/smartclide-TD-Principal/blob/main/src/main/resources/application.properties), and are the following:
- server.port=8555, In order for the service to start in the 8555 port
- gr.nikos.smartclide.sonarqube.url=http://localhost:9000, In order to get the SonarQube instance and the in case this is not configured in the beginning of the service it's going to be the localhost.

**Docker**

The main thing that should be configured is the SonarQube instance, as in the majority of the cases the SonarQube is not going to be in the localhost.
This can be achieved by using the following environment variable:

```shell
docker run -p 8555:8555 -e GR_NIKOS_SMARTCLIDE_SONARQUBE_URL=${SONARQUBE_URL} smartclide-td-principal-backend:latest
```

More specifically:

```shell
docker run -p 8555:8555 -e GR_NIKOS_SMARTCLIDE_SONARQUBE_URL=http://1.1.1.1:9000 smartclide-td-principal-backend:latest
```
