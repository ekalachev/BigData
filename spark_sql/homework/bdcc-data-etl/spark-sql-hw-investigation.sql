-- Databricks notebook source
-- MAGIC %md # Spark SQL Homework (Investigation)
-- MAGIC 
-- MAGIC - Using Spark SQL to calculate and visualize in Databricks Notebooks, for queries use hotel_id (hotel id – join key), srch_ci (checkin), srch_co (checkout):
-- MAGIC   - Top 10 hotels with max absolute temperature difference by month.
-- MAGIC   - Top 10 busy (e.g. with the biggest visits count) hotels for each month. If visit dates refer to several months it should be counted for all affected months.
-- MAGIC   - For visits with extended stay (more than 7 days) calculate weather trend (the day temperature difference between last and first day of stay) and average temperature during stay.
-- MAGIC - For designed queries analyze execution plan. What is the bottleneck of your jobs? For each analysis you could create tables with proper structure and partitioning if necessary.

-- COMMAND ----------

-- MAGIC %run ./config

-- COMMAND ----------

CREATE DATABASE IF NOT EXISTS spark_sql_eldar;
USE spark_sql_eldar;

-- COMMAND ----------

DROP VIEW IF EXISTS hotel_weather;

CREATE TEMPORARY VIEW hotel_weather
USING org.apache.spark.sql.parquet
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/hotel-weather"
);

SELECT * FROM hotel_weather LIMIT 5;

-- COMMAND ----------

DESCRIBE QUERY SELECT * FROM hotel_weather;

-- COMMAND ----------

DROP VIEW IF EXISTS hotel_weather_prepared;
CREATE TEMPORARY VIEW hotel_weather_prepared AS
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
FROM hotel_weather;

-- COMMAND ----------

DROP VIEW IF EXISTS expedia;

CREATE TEMPORARY VIEW expedia
USING avro
OPTIONS (
  path "abfss://m6sparksql@bd201stacc.dfs.core.windows.net/expedia"
);

SELECT * FROM expedia LIMIT 5;

-- COMMAND ----------

DESCRIBE QUERY SELECT * FROM expedia;

-- COMMAND ----------

DROP VIEW IF EXISTS expedia_prepared;
CREATE TEMPORARY VIEW expedia_prepared AS
SELECT
  id AS expedia_id,
  TO_DATE(srch_ci, "yyyy-MM-dd") AS srch_ci,
  TO_DATE(srch_co, "yyyy-MM-dd") AS srch_co,
  hotel_id
FROM expedia;

-- COMMAND ----------

-- MAGIC %md ## Using Spark SQL to calculate and visualize in Databricks Notebooks, for queries use hotel_id (hotel id – join key), srch_ci (checkin), srch_co (checkout).

-- COMMAND ----------

-- MAGIC %md ### Top 10 hotels with max absolute temperature difference by month.

-- COMMAND ----------

WITH hotel_weather_diff AS
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
  FROM hotel_weather_prepared
  GROUP BY year, month, id
)

SELECT id, name, address, city, country, geoHash, latitude, longitude, year, month, ROUND(tmpr_diff_c, 1) AS tmpr_diff_c FROM
(
  SELECT *, ROW_NUMBER() OVER (PARTITION BY year, month ORDER BY tmpr_diff_c DESC) AS row_number
  FROM hotel_weather_diff
)
WHERE row_number < 11

-- COMMAND ----------

-- MAGIC %md ### Top 10 busy (e.g. with the biggest visits count) hotels for each month. If visit dates refer to several months it should be counted for all affected months.

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
  FROM hotel_weather_prepared AS hw
  INNER JOIN expedia_prepared AS e ON hw.id = e.hotel_id AND hw.wthr_date >= srch_ci AND hw.wthr_date <= srch_co
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

SELECT hotel_id, name, address, city, country, geoHash, latitude, longitude, year, month, busy_days_in_month FROM
(
  SELECT *, ROW_NUMBER() OVER (PARTITION BY year, month ORDER BY busy_days_in_month DESC) AS row_number
  FROM hotel_weather_expedia_grouped
)
WHERE row_number < 11

-- COMMAND ----------

-- MAGIC %md ### For visits with extended stay (more than 7 days) calculate weather trend (the day temperature difference between last and first day of stay) and average temperature during stay.

-- COMMAND ----------

WITH expedia_extended_stay AS
(
  SELECT * FROM expedia_prepared
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
  FROM hotel_weather_prepared AS hw
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
    expedia_id,
    array_sort(collect_list(struct(wthr_date, avg_tmpr_c))) AS tmprs,
    AVG(avg_tmpr_c) AS avg_tmpr_c
  FROM hotel_weather_expedia_extended_stay
  GROUP BY expedia_id
)

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
  ROUND(ABS(element_at(tmprs, 1).avg_tmpr_c - element_at(tmprs, -1).avg_tmpr_c), 2) AS diff_tmpr_c
FROM hotel_weather_expedia_extended_stay_grouped
ORDER BY diff_tmpr_c DESC
LIMIT 30

-- COMMAND ----------


