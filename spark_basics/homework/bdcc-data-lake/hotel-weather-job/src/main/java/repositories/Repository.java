package repositories;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;

import java.io.Serializable;

public interface Repository<T> extends Serializable {
    Dataset<T> getDataset();
    Encoder<T> getEncoder();
}
