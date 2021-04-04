package repositories;

import dto.Weather;
import org.apache.commons.lang3.Validate;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static de.cronn.reflection.util.PropertyUtils.getPropertyName;

public class WeatherRepositoryImpl implements Repository<Weather> {
    private final StructType weatherSchema = DataTypes.createStructType(new StructField[]{
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getLng), DataTypes.DoubleType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getLat), DataTypes.DoubleType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getGeoHash), DataTypes.StringType, true),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getAvg_tmpr_f), DataTypes.DoubleType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getAvg_tmpr_c), DataTypes.DoubleType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getWthr_date), DataTypes.StringType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getYear), DataTypes.IntegerType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getMonth), DataTypes.IntegerType, false),
            DataTypes.createStructField(getPropertyName(Weather.class, Weather::getDay), DataTypes.IntegerType, false)
    });

    private final Encoder<Weather> weatherEncoder;

    private final transient SparkSession spark;
    private final String baseUrl;

    public WeatherRepositoryImpl(SparkSession spark, String baseUrl) {
        Validate.notNull(spark);
        Validate.notEmpty(baseUrl);

        this.spark = spark;
        this.baseUrl = baseUrl;
        this.weatherEncoder = Encoders.bean(Weather.class);
    }

    public Dataset<Weather> getDataset() {

        return this.spark
                .read()
                .schema(this.weatherSchema)
                .format("parquet")
                .option("header", "true")
                .load(this.baseUrl + "/weather")
                .as(weatherEncoder);
    }

    @Override
    public Encoder<Weather> getEncoder() {
        return this.weatherEncoder;
    }
}
