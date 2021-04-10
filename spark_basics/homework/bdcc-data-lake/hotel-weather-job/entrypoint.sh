#!/bin/bash

readonly DRIVER_MEMORY='1g'
readonly EXECUTOR_MEMORY='3g'
readonly KUBERNETES_CONTAINER_IMAGE="spark/hotel-weather-job"
readonly SOURCE_DIR="/tmp/spark-k8s-demo"

readonly SPARK_NAMESPACE=default
readonly SA=spark-minion
readonly K8S_CACERT=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
readonly K8S_TOKEN=/var/run/secrets/kubernetes.io/serviceaccount/token

readonly PROJECT='hotel-weather-job'

#oc create sa spark -n ${PROJECT}
#oc adm policy add-scc-to-user anyuid -z spark -n ${PROJECT}

exec /opt/spark/bin/spark-submit \
--verbose \
--master "${SPARK_MASTER}" \
--name ${PROJECT} \
--conf spark.submit.deployMode=cluster \
--conf spark.kubernetes.namespace=${PROJECT} \
--conf spark.jars.ivy=/tmp/.ivy \
--conf spark.kubernetes.container.image="${KUBERNETES_CONTAINER_IMAGE}" \
--conf spark.hadoop.fs.azure.account.key."${BLOB_READ_ACCOUNT_NAME}".dfs.core.windows.net="${BLOB_READ_CONTAINER_KEY}" \
--conf spark.fs.azure.account.key."${BLOB_WRITE_ACCOUNT_NAME}".dfs.core.windows.net="${BLOB_WRITE_CONTAINER_KEY}" \
--conf spark.kubernetes.file.upload.path="${SOURCE_DIR}" \
--conf spark.driver.memory=${DRIVER_MEMORY} \
--conf spark.executor.memory=${EXECUTOR_MEMORY} \
--conf spark.executor.instances=2 \
--conf spark.kubernetes.authenticate.subdmission.caCertFile=$K8S_CACERT  \
--conf spark.kubernetes.authenticate.submission.oauthTokenFile=$K8S_TOKEN  \
--conf spark.kubernetes.authenticate.driver.serviceAccountName=$SA  \
--conf spark.kubernetes.container.image.pullPolicy=Always \
--conf spark.kubernetes.namespace=$SPARK_NAMESPACE  \
--class "HotelWeatherJob" \
"/opt/spark/jars/hotel-weather-job.jar"