package conf;

import scala.Serializable;

public final class SystemVariables implements Serializable {
    private static volatile SystemVariables instance;

    private final String sparkMaster;

    private final String blobReadUrl;
    private final String blobReadKeyProperty;
    private final String blobReadContainerKey;

    private final String blobWriteUrl;
    private final String blobWriteKeyProperty;
    private final String blobWriteContainerKey;

    private final String opencageApiKey;

    private SystemVariables() {
        this.sparkMaster = System.getenv("SPARK_MASTER");

        String blobReadAccountName = System.getenv("BLOB_READ_ACCOUNT_NAME");
        String blobReadContainerName = System.getenv("BLOB_READ_CONTAINER_NAME");
        this.blobReadContainerKey = System.getenv("BLOB_READ_CONTAINER_KEY");
        this.blobReadUrl = String.format(
                "abfss://%s@%s.dfs.core.windows.net",
                blobReadContainerName,
                blobReadAccountName);
        this.blobReadKeyProperty = String.format(
                "spark.hadoop.fs.azure.account.key.%s.dfs.core.windows.net",
                blobReadAccountName);

        String blobWriteAccountName = System.getenv("BLOB_WRITE_ACCOUNT_NAME");
        String blobWriteContainerName = System.getenv("BLOB_WRITE_CONTAINER_NAME");
        this.blobWriteContainerKey = System.getenv("BLOB_WRITE_CONTAINER_KEY");
        this.blobWriteUrl = String.format(
                "abfss://%s@%s.dfs.core.windows.net",
                blobWriteContainerName,
                blobWriteAccountName);
        this.blobWriteKeyProperty = String.format(
                "fs.azure.account.key.%s.dfs.core.windows.net",
                blobWriteAccountName);

        this.opencageApiKey = System.getenv("OPENCAGE_API_KEY");
    }

    public static SystemVariables getInstance() {
        SystemVariables systemVariables = instance;
        if (systemVariables != null) {
            return systemVariables;
        }

        synchronized (SystemVariables.class) {
            if (instance == null) {
                instance = new SystemVariables();
            }
            return instance;
        }
    }

    public String getSparkMaster() {
        return sparkMaster;
    }

    public String getBlobReadUrl() {
        return blobReadUrl;
    }

    public String getBlobWriteUrl() {
        return blobWriteUrl;
    }

    public String getBlobReadKeyProperty() {
        return blobReadKeyProperty;
    }

    public String getBlobWriteKeyProperty() {
        return blobWriteKeyProperty;
    }

    public String getBlobReadContainerKey() {
        return blobReadContainerKey;
    }

    public String getBlobWriteContainerKey() {
        return blobWriteContainerKey;
    }

    public String getOpencageApiKey() {
        return opencageApiKey;
    }
}
