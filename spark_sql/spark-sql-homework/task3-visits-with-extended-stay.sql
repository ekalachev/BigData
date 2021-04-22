-- Databricks notebook source
-- MAGIC %md # For visits with extended stay (more than 7 days) calculate weather trend (the day temperature difference between last and first day of stay) and average temperature during stay.

-- COMMAND ----------

-- MAGIC %run ./bronze-to-silver

-- COMMAND ----------

-- for the idempotent working of the command
DROP TABLE IF EXISTS visits_with_extended_stay;

-- create final storage
CREATE TABLE visits_with_extended_stay
(
  hotel_id STRING,
  expedia_id STRING,
  address STRING,
  city STRING,
  country STRING,
  geoHash STRING,
  latitude DOUBLE,
  longitude DOUBLE,
  name STRING,
  avg_tmpr_c DOUBLE,
  diff_tmpr_c DOUBLE,
  srch_ci DATE,
  srch_co DATE,
  year INT,
  month INT
)
USING parquet
PARTITIONED BY (year, month)
OPTIONS (
  path "abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/visits_with_extended_stay"
)

-- COMMAND ----------

WITH expedia_extended_stay AS
(
  SELECT * FROM expedia_silver
  WHERE datediff(srch_co, srch_ci) > 7
),
hotel_weather_expedia_extended_stay AS
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
    hw.avg_tmpr_c,
    hw.avg_tmpr_f,
    e.srch_ci,
    e.srch_co,
    e.expedia_id
  FROM hotel_weather_silver AS hw
  INNER JOIN expedia_extended_stay AS e
    ON hw.id = e.hotel_id AND hw.wthr_date >= srch_ci AND hw.wthr_date <= srch_co
),
hotel_weather_expedia_extended_stay_grouped AS
(
  SELECT
    FIRST(hotel_id) AS hotel_id,
    FIRST(address) AS address,
    FIRST(city) AS city,
    FIRST(country) AS country,
    FIRST(geoHash) AS geoHash,
    FIRST(latitude) AS latitude,
    FIRST(longitude) AS longitude,
    FIRST(name) AS name,
    FIRST(srch_ci) AS srch_ci,
    FIRST(srch_co) AS srch_co,
    expedia_id,
    array_sort(collect_list(struct(wthr_date, avg_tmpr_c))) AS tmprs,
    AVG(avg_tmpr_c) AS avg_tmpr_c
  FROM hotel_weather_expedia_extended_stay
  GROUP BY expedia_id
)

INSERT INTO visits_with_extended_stay
SELECT 
  hotel_id,
  expedia_id,
  address,
  city,
  country,
  geoHash,
  latitude,
  longitude,
  name,
  ROUND(avg_tmpr_c, 2) AS avg_tmpr_c,
  ROUND(ABS(element_at(tmprs, 1).avg_tmpr_c - element_at(tmprs, -1).avg_tmpr_c), 2) AS diff_tmpr_c,
  srch_ci,
  srch_co,
  year(srch_ci) AS srch_ci_year,
  month(srch_ci) AS srch_ci_month
FROM hotel_weather_expedia_extended_stay_grouped

-- COMMAND ----------

-- MAGIC %python
-- MAGIC spark.read.format("parquet").option("header", "true").load("abfss://hotel-weather@eldarbigdata.dfs.core.windows.net/visits_with_extended_stay").show(30)

-- COMMAND ----------


