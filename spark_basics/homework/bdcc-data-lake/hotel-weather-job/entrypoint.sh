#!/bin/bash

readonly DRIVER_MEMORY='1g'
readonly EXECUTOR_MEMORY='3g'
readonly KUBERNETES_CONTAINER_IMAGE="spark/hotel-weather-job"
readonly SOURCE_DIR="/tmp/spark-k8s-demo"

# Execute the container CMD under tini for better hygiene
exec /sbin/tini -s -- spark-submit --master "${SPARK_MASTER}" \
--deploy-mode cluster \
--name hotels-weather \
--conf spark.jars.ivy=/tmp/.ivy \
--conf spark.kubernetes.container.image="${KUBERNETES_CONTAINER_IMAGE}" \
--conf spark.hadoop.fs.azure.account.key."${BLOB_READ_ACCOUNT_NAME}".dfs.core.windows.net="${BLOB_READ_CONTAINER_KEY}" \
--conf spark.fs.azure.account.key."${BLOB_WRITE_ACCOUNT_NAME}".dfs.core.windows.net="${BLOB_WRITE_CONTAINER_KEY}" \
--conf spark.kubernetes.file.upload.path="${SOURCE_DIR}" \
--conf spark.driver.memory=${DRIVER_MEMORY} \
--conf spark.executor.memory=${EXECUTOR_MEMORY} \
--conf spark.executor.instances=2 \
--conf spark.hadoop.security.authentication=simple \
--conf spark.hadoop.security.authorization=false \
--conf spark.kubernetes.authenticate.caCertFile=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt  \
--conf spark.kubernetes.authenticate.oauthTokenFile=/var/run/secrets/kubernetes.io/serviceaccount/token  \
--conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
--class "HotelWeatherJob" \
"/opt/spark/jars/hotel-weather-job.jar"