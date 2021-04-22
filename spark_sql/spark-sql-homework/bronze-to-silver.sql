-- Databricks notebook source
-- MAGIC %run ./config

-- COMMAND ----------

-- for the idempotent working of the command
DROP VIEW IF EXISTS hotel_weather_bronze;

CREATE TEMPORARY VIEW hotel_weather_bronze
USING org.apache.spark.sql.parquet
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/hotel-weather"
);

-- COMMAND ----------

-- for the idempotent working of the command
DROP VIEW IF EXISTS hotel_weather_silver;

CREATE TEMPORARY VIEW hotel_weather_silver AS
SELECT
  address,
  avg_tmpr_c,
  avg_tmpr_f,
  city,
  country,
  geoHash,
  id,
  latitude,
  longitude,
  name,
  year,
  month,
  day,
  TO_DATE(wthr_date, "yyyy-MM-dd") AS wthr_date
FROM hotel_weather_bronze;

-- COMMAND ----------

-- for the idempotent working of the command
DROP VIEW IF EXISTS expedia_bronze;

CREATE TEMPORARY VIEW expedia_bronze
USING avro
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/expedia"
);

-- COMMAND ----------

-- for the idempotent working of the command
DROP VIEW IF EXISTS expedia_silver;

CREATE TEMPORARY VIEW expedia_silver AS
SELECT
  id AS expedia_id,
  TO_DATE(srch_ci, "yyyy-MM-dd") AS srch_ci,
  TO_DATE(srch_co, "yyyy-MM-dd") AS srch_co,
  hotel_id
FROM expedia_bronze;
