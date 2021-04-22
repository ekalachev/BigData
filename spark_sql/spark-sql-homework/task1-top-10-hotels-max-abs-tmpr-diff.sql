-- Databricks notebook source
-- MAGIC %md # Top 10 hotels with max absolute temperature difference by month.

-- COMMAND ----------

-- DBTITLE 1,Setup configuration
-- MAGIC %run ./config

-- COMMAND ----------

CREATE DATABASE IF NOT EXISTS spark_sql_eldar;
USE spark_sql_eldar;

-- COMMAND ----------

-- DBTITLE 1,Bonze pipeline step
DROP VIEW IF EXISTS hotel_weather_bronze;

CREATE TEMPORARY VIEW hotel_weather_bronze
USING org.apache.spark.sql.parquet
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/hotel-weather"
);

-- COMMAND ----------

-- DBTITLE 1,Silver pipeline step
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
DROP TABLE IF EXISTS top_10_hotels_max_tmpr_diff;

-- create final storage
CREATE TABLE top_10_hotels_max_tmpr_diff
(
  id STRING,
  name STRING,
  address STRING,
  city STRING,
  country STRING,
  geoHash STRING,
  latitude DOUBLE,
  longitude DOUBLE,
  year INT,
  month INT,
  tmpr_diff_c DOUBLE
)
USING parquet
OPTIONS (
  path "abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/top_10_hotels_max_tmpr_diff"
)

-- COMMAND ----------

-- DBTITLE 1,Gold pipeline step
WITH hotel_weather_diff_grouped AS
(
  SELECT
    id,
    FIRST(address) AS address,
    FIRST(city) AS city,
    FIRST(country) AS country,
    FIRST(latitude) AS latitude,
    FIRST(longitude) AS longitude,
    FIRST(geoHash) AS geoHash,
    FIRST(name) AS name,
    year,
    month,
    MAX(avg_tmpr_c) - MIN(avg_tmpr_c) AS tmpr_diff_c
  FROM hotel_weather_silver
  GROUP BY year, month, id
),
calculate_top_10_hotels_by_month AS
(
  -- select necessary information for presentation
  SELECT id, name, address, city, country, geoHash, latitude, longitude, year, month, ROUND(tmpr_diff_c, 1) AS tmpr_diff_c FROM
  (
    -- calculate top 10 hotels with max absolute temperature difference by month.
    SELECT *, ROW_NUMBER() OVER (PARTITION BY year, month ORDER BY tmpr_diff_c DESC) AS row_number
    FROM hotel_weather_diff_grouped
  )
  -- take top 10 for each month
  WHERE row_number < 11
)

INSERT INTO top_10_hotels_max_tmpr_diff 
SELECT * FROM calculate_top_10_hotels_by_month

-- COMMAND ----------

-- MAGIC %python
-- MAGIC spark.read.format("parquet").option("header", "true").load("abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/top_10_hotels_max_tmpr_diff").show(30)

-- COMMAND ----------


