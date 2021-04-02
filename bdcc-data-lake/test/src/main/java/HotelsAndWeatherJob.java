import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.models.StorageAccountInfo;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import com.azure.storage.file.datalake.models.FileSystemItem;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import scala.collection.immutable.Seq;
import scala.reflect.io.ZipArchive;
import utilities.Hotel;

import java.io.*;
import java.time.Duration;
import java.util.zip.ZipOutputStream;

public class HotelsAndWeatherJob {
    public static void main(String[] args) {
        readFromAzure();
    }

    private static void readFromAzure() {
        String containerName = "<container-name>";

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("<end-point>")
                .sasToken("<sas-token>")
                .buildClient();

        BlobContainerClient blobContainerClient = blobServiceClient
                .getBlobContainerClient(containerName);

        ListBlobsOptions options = new ListBlobsOptions()
                .setPrefix("hotels/part")
                .setDetails(new BlobListDetails()
                        .setRetrieveDeletedBlobs(false)
                        .setRetrieveSnapshots(false));

        blobContainerClient
                .listBlobs(options, Duration.ofMinutes(10))
                .forEach(blob -> processHotel(blobContainerClient, blob));
    }

    public static void processHotel(BlobContainerClient blobContainerClient, BlobItem blob) {
        System.out.printf("Name: %s%n", blob.getName());

        BlobClient blobClient = blobContainerClient.getBlobClient(blob.getName());

        ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();

        blobClient.download(outputStream);

        final byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        SparkSession spark = SparkSession.builder()
                .master("local[*]")
                .appName("HotelsAndWeatherJob")
                .getOrCreate();

//        Seq<Hotel> hotels = inputStream
//                .getClass()
//                .getClassLoader()
//                .getResourceAsStream("hotels.csv.zip");



//        Dataset dataset = spark.createDataset(inputStream.read(), Encoders.bean(Hotel.class));
    }
}
