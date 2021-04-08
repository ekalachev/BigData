# Build stage
FROM maven:3.8.1-openjdk-8-slim AS build

COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package

# Run stage
FROM openjdk:8-jdk-alpine

ARG spark_uid=777

ARG SPARK_VERSION_ARG=3.1.1
ARG HADOOP_VERSION=3.2.1

ARG spark_master

ARG blob_read_account_name
ARG blob_read_container_name
ARG blob_read_container_key

ARG blob_write_account_name
ARG blob_write_container_name
ARG blob_write_container_key

ARG opencage_api_key

ENV SPARK_HOME         /opt/spark
ENV HADOOP_HOME        /opt/hadoop
ENV SPARK_CONF_DIR     $SPARK_HOME/conf
ENV PATH               $PATH:$SPARK_HOME/bin

ENV SPARK_MASTER       $spark_master

ENV BLOB_READ_ACCOUNT_NAME  $blob_read_account_name
ENV BLOB_READ_CONTAINER_NAME $blob_read_container_name
ENV BLOB_READ_CONTAINER_KEY $blob_read_container_key

ENV BLOB_WRITE_ACCOUNT_NAME $blob_write_account_name
ENV BLOB_WRITE_CONTAINER_NAME $blob_write_container_name
ENV BLOB_WRITE_CONTAINER_KEY $blob_write_container_key

ENV OPENCAGE_API_KEY   $opencage_api_key

ENV SPARK_VERSION      $SPARK_VERSION_ARG

RUN set -ex && \
    apk upgrade --no-cache && \
    apk --update add --no-cache bash tini libstdc++ glib gcompat libc6-compat linux-pam krb5 krb5-libs nss openssl wget sed curl && \
    rm /bin/sh && \
    ln -sv /bin/bash /bin/sh && \
    echo "auth required pam_wheel.so use_uid" >> /etc/pam.d/su && \
    chgrp root /etc/passwd && chmod ug+rw /etc/passwd

RUN wget -O /spark-${SPARK_VERSION}-bin-without-hadoop.tgz https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-without-hadoop.tgz && \
    tar -xzf /spark-${SPARK_VERSION}-bin-without-hadoop.tgz -C /opt/ && \
    ln -s /opt/spark-${SPARK_VERSION}-bin-without-hadoop $SPARK_HOME && \
    rm -f /spark-${SPARK_VERSION}-bin-without-hadoop.tgz && \
    mkdir -p $SPARK_HOME/work-dir && \
    mkdir -p $SPARK_HOME/spark-warehouse

RUN wget -O /hadoop-${HADOOP_VERSION}.tar.gz https://archive.apache.org/dist/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz && \
    tar -xzf /hadoop-${HADOOP_VERSION}.tar.gz -C /opt/ && \
    ln -s /opt/hadoop-${HADOOP_VERSION} $HADOOP_HOME && \
    rm -f /hadoop-${HADOOP_VERSION}.tar.gz

ENV PATH "$SPARK_HOME/bin:$HADOOP_HOME/bin:$PATH"
ENV SPARK_DIST_CLASSPATH $HADOOP_HOME/etc/hadoop:$HADOOP_HOME/share/hadoop/common/lib/*:$HADOOP_HOME/share/hadoop/common/*:$HADOOP_HOME/share/hadoop/hdfs:$HADOOP_HOME/share/hadoop/hdfs/lib/*:$HADOOP_HOME/share/hadoop/hdfs/*:$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/yarn:$HADOOP_HOME/share/hadoop/yarn/lib/*:$HADOOP_HOME/share/hadoop/yarn/*:$HADOOP_HOME/share/hadoop/tools/lib/*
ENV SPARK_CLASSPATH $HADOOP_HOME/etc/hadoop:$HADOOP_HOME/share/hadoop/common/lib/*:$HADOOP_HOME/share/hadoop/common/*:$HADOOP_HOME/share/hadoop/hdfs:$HADOOP_HOME/share/hadoop/hdfs/lib/*:$HADOOP_HOME/share/hadoop/hdfs/*:$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/yarn:$HADOOP_HOME/share/hadoop/yarn/lib/*:$HADOOP_HOME/share/hadoop/yarn/*:$HADOOP_HOME/share/hadoop/tools/lib/*

#RUN wget -O $SPARK_HOME/jars/spark-core_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-core_2.12/3.1.1/spark-core_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-sql_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-sql_2.12/3.1.1/spark-sql_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-catalyst_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-catalyst_2.12/3.1.1/spark-catalyst_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-kvstore_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-kvstore_2.12/3.1.1/spark-kvstore_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-launcher_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-launcher_2.12/3.1.1/spark-launcher_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-network-common_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-network-common_2.12/3.1.1/spark-network-common_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-network-shuffle_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-network-shuffle_2.12/3.1.1/spark-network-shuffle_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-sketch_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-sketch_2.12/3.1.1/spark-sketch_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-tags_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-tags_2.12/3.1.1/spark-tags_2.12-3.1.1.jar
#RUN wget -O $SPARK_HOME/jars/spark-unsafe_2.12-3.1.1.jar https://repo1.maven.org/maven2/org/apache/spark/spark-unsafe_2.12/3.1.1/spark-unsafe_2.12-3.1.1.jar
#
#
#RUN wget -O $SPARK_HOME/jars/log4j-1.2.17.jar https://repo1.maven.org/maven2/log4j/log4j/1.2.17/log4j-1.2.17.jar
#RUN wget -O $SPARK_HOME/jars/hadoop-azure-3.2.1.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-azure/3.2.1/hadoop-azure-3.2.1.jar
#RUN wget -O $SPARK_HOME/jars/hadoop-client-3.2.1.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-client/3.2.1/hadoop-client-3.2.1.jar
#RUN wget -O $SPARK_HOME/jars/azure-storage-8.6.6.jar https://repo1.maven.org/maven2/com/microsoft/azure/azure-storage/8.6.6/azure-storage-8.6.6.jar
#RUN wget -O $SPARK_HOME/jars/azure-keyvault-core-1.2.4.jar https://repo1.maven.org/maven2/com/microsoft/azure/azure-keyvault-core/1.2.4/azure-keyvault-core-1.2.4.jar
#RUN wget -O $SPARK_HOME/jars/azure-data-lake-store-sdk-2.3.9.jar https://repo1.maven.org/maven2/com/microsoft/azure/azure-data-lake-store-sdk/2.3.9/azure-data-lake-store-sdk-2.3.9.jar


COPY entrypoint.sh /opt/
COPY Dockerfile /my_docker/
COPY conf/* $SPARK_CONF_DIR/

ENV JAR_APP $SPARK_HOME/jars/hotel-weather-job.jar

COPY --from=build /app/target/hotel-weather-job*.jar $JAR_APP

COPY conf/log4j.properties /my_docker/conf/

RUN chmod +x /opt/*.sh

WORKDIR $SPARK_HOME/work-dir

ENTRYPOINT [ "/opt/entrypoint.sh" ]

# Specify the User that the actual main process will run as
USER ${spark_uid}