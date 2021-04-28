# Kafka Azure Common Storage Connectors

A multi Maven Module [Kafka Connector](http://kafka.apache.org/documentation.html#connect) project that targets specifically Azure Storage services.

# Development

To build a development version you'll need a recent version of Kafka. You can build
*kafka-connect-azure-storage-parent* with Maven using the standard lifecycle phases.

# Modules

* [azure-storage-common](azure-storage-common): Responsible for the shared code between all Azure Storage [Kafka Connectors](http://kafka.apache.org/documentation.html#connect). Changes which impact all Azure Storage Connectors should be made here to propagate down to the other connectors.
* [kafka-connect-azure-blob-storage](kafka-connect-azure-blob-storage): The connector responsible for writing to Azure Blob Storage.    


# Contribute

- Source Code: https://github.com/confluentinc/kafka-connect-azure-blob-storage
- Issue Tracker: https://github.com/confluentinc/kafka-connect-azure-blob-storage/issues

