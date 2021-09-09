# 
#  This Source Code Form is subject to the terms of the Mozilla Public License, v.
#  2.0 with a Healthcare Disclaimer.
#  A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
#  be found under the top level directory, named LICENSE.
#  If a copy of the MPL was not distributed with this file, You can obtain one at
#  http://mozilla.org/MPL/2.0/.
#  If a copy of the Healthcare Disclaimer was not distributed with this file, You
#  can obtain one at the project website https://github.com/igia.
# 
#  Copyright (C) 2021-2022 Persistent Systems, Inc.
# 



#!/bin/bash

echo This script will build, and deploy all i2b2-cdi-api components to local docker server. 
echo 
echo please press any key to start...
read INPUT

SCRIPT_PATH=$(dirname "$0")
SCRIPT_DIR=$(pwd)
BUILD_DIR_PATH="${SCRIPT_DIR}/../../../"

CUSTOM_VAR_FILE_PARAM=""
SELECT=1
while :; do
    echo
    echo "i2b2-cdi-api Datasource:"
    echo
    echo "1. PostgreSQL"
    echo "2. MS-SQL"
    echo 
    read -p "Choose datasource (Default: 1)? " SELECT
    
    if [[ -z ${SELECT} ]]; then
        SELECT=1
    fi

    case "$SELECT" in
    1)  DOCKER_COMPOSE_FILE_PATH="i2b2-cdi-api-pgsql.yml"
	    DATASOURCE_SERVICE=i2b2-pg
        break ;;
    2)  DOCKER_COMPOSE_FILE_PATH="i2b2-cdi-api-mssql.yml"
            DATASOURCE_SERVICE=i2b2-mssql
        break ;;
    *)  echo
        echo "The entered datasource option is not in the list."
        echo
        ;;
    esac
done

SELECT=1
while :; do
    echo
    echo "i2b2-cdi-api Profile:"
    echo
    echo "1. Dev"
    echo "2. Prod"
    echo 
    read -p "Choose profile (Default: 1)? " SELECT

    if [[ -z ${SELECT} ]]; then
        SELECT=1
    fi

    case "$SELECT" in
    1)  SPRING_PROFILE=dev
        break ;;
    2)  SPRING_PROFILE=prod
        break ;;
    *)  echo
        echo "The entered profile option is not in the list."
        echo
        ;;
    esac
done


echo 
echo "Started i2b2-cdi-api components build and deployment on docker environment"
echo 
echo "Running the command: docker-compose -f ./i2b2-cdi-api-commons.yml -f ./i2b2-cdi-api-pgsql.yml -f ./i2b2-cdi-api-mssql.yml down"
echo
`docker-compose -f ./i2b2-cdi-api-commons.yml -f ./i2b2-cdi-api-pgsql.yml -f ./i2b2-cdi-api-mssql.yml down`

echo 
echo "Running the command: (cd "${BUILD_DIR_PATH}" && ./mvnw clean package -P"${SPRING_PROFILE}" -DskipTests=true jib:dockerBuild) && I2B2_SPRING_PROFILES_ACTIVE="${SPRING_PROFILE}" I2B2_COMMONS_DATASOURCE_SERVICE="${DATASOURCE_SERVICE}" docker-compose -f i2b2-cdi-api-commons.yml -f "${DOCKER_COMPOSE_FILE_PATH}" up -d"
echo
(cd "${BUILD_DIR_PATH}" && ./mvnw clean package -P"${SPRING_PROFILE}" -DskipTests=true jib:dockerBuild) && I2B2_SPRING_PROFILES_ACTIVE="${SPRING_PROFILE}" I2B2_COMMONS_DATASOURCE_SERVICE="${DATASOURCE_SERVICE}" docker-compose -f i2b2-cdi-api-commons.yml -f "${DOCKER_COMPOSE_FILE_PATH}" up -d

