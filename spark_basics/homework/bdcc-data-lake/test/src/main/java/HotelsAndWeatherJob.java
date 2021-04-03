import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.models.StorageAccountInfo;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import com.azure.storage.file.datalake.models.FileSystemItem;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import utilities.Hotel;

import java.io.*;
import java.time.Duration;

public class HotelsAndWeatherJob {
    private static final String containerName = "m6sparkbasics";
    private static final String accountName = "bd201stacc";
    private static final String endpoint = "https://bd201stacc.blob.core.windows.net";
    private static final String sasToken = "sv=2020-04-08&st=2021-03-30T12%3A46%3A05Z&se=2031-03-31T12%3A46%3A00Z&sr=c&sp=rl&sig=H7H5Q2Tq5KL21wXPvIaI2mnVZYdoUEotK1Y%2Bca0NGNE%3D";


    public static void main(String[] args) throws InterruptedException {
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("HotelsAndWeatherJob")
                .getOrCreate();

//       Thread.sleep(10 * 60 * 1000);

        spark
                .read()
                .format("csv")
                .option("header", "true")
                .load("abfss://" + containerName + "@" + accountName + ".dfs.core.windows.net/hotels")
                .show();

//        Dataset dataset = spark.createDataset(, Encoders.bean(Hotel.class))
    }
}
