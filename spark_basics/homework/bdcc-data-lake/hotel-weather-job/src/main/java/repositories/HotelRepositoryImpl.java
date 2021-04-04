package repositories;

import dto.Hotel;
import org.apache.commons.lang3.Validate;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static de.cronn.reflection.util.PropertyUtils.getPropertyName;

public class HotelRepositoryImpl implements Repository<Hotel> {
    private final StructType hotelSchema = DataTypes.createStructType(new StructField[]{
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getId), DataTypes.LongType, false),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getAddress), DataTypes.StringType, false),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getCountry), DataTypes.StringType, false),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getCity), DataTypes.StringType, false),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getName), DataTypes.StringType, false),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getLatitude), DataTypes.DoubleType, true),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getLongitude), DataTypes.DoubleType, true),
            DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getGeoHash), DataTypes.StringType, true)
    });

    private final Encoder<Hotel> hotelEncoder;

    private final transient SparkSession spark;
    private final String baseUrl;

    public HotelRepositoryImpl(SparkSession spark, String baseUrl) {
        Validate.notNull(spark);
        Validate.notEmpty(baseUrl);

        this.spark = spark;
        this.baseUrl = baseUrl;
        this.hotelEncoder = Encoders.bean(Hotel.class);
    }

    public Dataset<Hotel> getDataset() {

        return this.spark
                .read()
                .schema(this.hotelSchema)
                .format("csv")
                .option("header", "true")
                .load(this.baseUrl + "/hotels")
                .as(hotelEncoder);
    }

    @Override
    public Encoder<Hotel> getEncoder() {
        return this.hotelEncoder;
    }
}
