-- Databricks notebook source
-- MAGIC %md # Top 10 busy (e.g. with the biggest visits count) hotels for each month. If visit dates refer to several months it should be counted for all affected months.

-- COMMAND ----------

-- MAGIC %run ./config

-- COMMAND ----------

DROP VIEW IF EXISTS hotel_weather_bronze;

CREATE TEMPORARY VIEW hotel_weather_bronze
USING org.apache.spark.sql.parquet
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/hotel-weather"
);

-- COMMAND ----------

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

DROP VIEW IF EXISTS expedia_bronze;

CREATE TEMPORARY VIEW expedia_bronze
USING avro
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/expedia"
);

-- COMMAND ----------

DROP VIEW IF EXISTS expedia_silver;
CREATE TEMPORARY VIEW expedia_silver AS
SELECT
  id AS expedia_id,
  TO_DATE(srch_ci, "yyyy-MM-dd") AS srch_ci,
  TO_DATE(srch_co, "yyyy-MM-dd") AS srch_co,
  hotel_id
FROM expedia_bronze;

-- COMMAND ----------

-- for the idempotent working of the command
DROP TABLE IF EXISTS top_10_busy_hotels;

-- create final storage
CREATE TABLE top_10_busy_hotels
(
  hotel_id STRING,
  name STRING,
  address STRING,
  city STRING,
  country STRING,
  geoHash STRING,
  latitude DOUBLE,
  longitude DOUBLE,
  year INT,
  month INT,
  busy_days_in_month INT
)
USING parquet
OPTIONS (
  path "abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/top_10_busy_hotels"
)

-- COMMAND ----------

WITH hotel_weather_expedia AS
(
  SELECT
    hw.id AS hotel_id,
    hw.address,
    hw.city,
    hw.country,
    hw.geoHash,
    hw.latitude,
    hw.longitude,
    hw.name,
    hw.wthr_date,
    hw.year,
    hw.month,
    hw.day,
    e.srch_ci,
    e.srch_co,
    CASE 
      WHEN year(e.srch_ci) = year(e.srch_co) AND month(e.srch_ci) = month(e.srch_co) 
        THEN datediff(e.srch_co, e.srch_ci) 
      WHEN year(e.srch_co) = hw.year AND month(e.srch_co) = hw.month
        THEN day(e.srch_co)
      WHEN year(e.srch_ci) = hw.year AND month(e.srch_ci) = hw.month AND day(e.srch_ci) = day(last_day(e.srch_ci))
        THEN 1
        ELSE day(last_day(e.srch_ci)) - day(e.srch_ci) END
      AS duration_in_month
  FROM hotel_weather_silver AS hw
  INNER JOIN expedia_silver AS e ON hw.id = e.hotel_id AND hw.wthr_date >= srch_ci AND hw.wthr_date <= srch_co
),
hotel_weather_expedia_grouped AS
(
  SELECT
    hotel_id,
    FIRST(address) AS address,
    FIRST(city) AS city,
    FIRST(country) AS country,
    FIRST(geoHash) AS geoHash,
    FIRST(latitude) AS latitude,
    FIRST(longitude) AS longitude,
    FIRST(name) AS name,
    year,
    month,
    SUM(duration_in_month) AS busy_days_in_month
  FROM hotel_weather_expedia
  GROUP BY year, month, hotel_id
)

INSERT INTO top_10_busy_hotels 
SELECT hotel_id, name, address, city, country, geoHash, latitude, longitude, year, month, busy_days_in_month FROM
(
  SELECT *, ROW_NUMBER() OVER (PARTITION BY year, month ORDER BY busy_days_in_month DESC) AS row_number
  FROM hotel_weather_expedia_grouped
)
WHERE row_number < 11

-- COMMAND ----------

-- MAGIC %python
-- MAGIC spark.read.format("parquet").option("header", "true").load("abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/top_10_busy_hotels").show(30)

-- COMMAND ----------


