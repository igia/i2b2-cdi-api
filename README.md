# i2b2-api
I2B2 API provides RESTful interface over I2B2 data mart. 

##Pre-requisite
* I2B2 database (postgres or mssql)

## Configuration
* You can update following properties in the `application.yml` to point to the I2B2 CRC database.
```yaml
spring:
    datasource:
        url: jdbc:sqlserver://localhost:1433;databaseName=i2b2demodata
        username: 
        password: 
        driver: com.microsoft.sqlserver.jdbc.SQLServerDriver
```
* You can update following properties in the `application.yml` to point to the I2B2 Ontology database.
```yaml
ontology:
    datasource:
        url: jdbc:sqlserver://localhost:1433;databaseName=i2b2metadata
        username: 
        password: 
        driver: com.microsoft.sqlserver.jdbc.SQLServerDriver
```
* You can configure following properties in the `application.yml` to specify defaults used in `Fact`, `Patient`,
 and `Encounter` dimension records.
```yaml
application:
    lenient-validation: false
    source-system-code: DEMO
    project-id: PRJ001
    patient-source: DEMO
    patient-status: A
    encounter-source: DEMO
    encounter-status: A
```
You can set the `lenient-validation` property to `true` to disable the provider identifier validation in the fact API.

## Development
### Development profile
You can start the application in the development mode using pre-configured `dev` profile. It's default profile and
is configured to point to the local running I2B2 postgres database instance. Use command:
```bash
./mvnw
```
    
### Production profile
You can start the application in the production mode using pre-configured `prod` profile. It is configured to point to
 the local running I2B2 MSSQL database instance. Use command:
```bash
./mvnw -Pprod
```

### Swagger documentation
Swagger documentation is accessible at `http://localhost:5002/v2/api-docs?group=I2B2%20REST%20API` and Swagger UI is 
accessible at `http://localhost:5002/swagger-ui.html`

## Package
To package the application as a `production` JAR, use command:
```bash
./mvnw -Pprod clean package
```
It will generate executable jar in the `target` directory. To start application, use command:
```bash
java -jar i2b2-cdi-api-0.0.1-SNAPSHOT.jar
```

## Testing

### Unit Tests
You can execute unit tests with command:
```bash
./mvnw test
```
To skip execution of unit tests, use `-DskipTests` flag.

### Integration Tests
We use docker maven plugin to start the I2b2 postgres database server for Integration tests. 
You can start integration tests with command:
```bash
./mvnw verify
```
To skip execution of integration tests, use `-DskipITs` flag.

## Launch

You can use `src/main/docker/start.sh` script. It launches the pre-configured i2b2-cdi-api stack with selected datasource - `PostgreSQL` or `MSSQL`.

```
$ ./start.sh 

This script will build, and deploy all i2b2-cdi-api components to local docker server.

please press any key to start...

i2b2-cdi-api Datasource:

1. PostgreSQL
2. MS-SQL

Choose datasource (Default: 1)? 1

i2b2-cdi-api Profile:

1. Dev
2. Prod

Choose profile (Default: 1)? 2

Started i2b2-cdi-api components build and deployment on docker environment

Running the command: docker-compose -f ./i2b2-cdi-api-commons.yml -f ./i2b2-cdi-api-pgsql.yml -f ./i2b2-cdi-api-mssql.yml down

Running the command: (cd ~/i2b2-cdi-api/src/main/docker/../../../ && ./mvnw clean package -Pprod -DskipTests=true jib:dockerBuild) && I2B2_SPRING_PROFILES_ACTIVE=prod I2B2_COMMONS_DATASOURCE_SERVICE=i2b2-pg docker-compose -f i2b2-cdi-api-commons.yml -f i2b2-cdi-api-pgsql.yml up -d

```
