# Homework
- Create Spark etl job to joining hotels and weather data.
- Read data from Azure ADLS gen2 storage: https://<ACCOUNT_NAME>.dfs.core.windows.net/<CONTAINER_NAME> using storage access key: <ACCOUNT_KEY> (can explore data via <a href="https://azure.microsoft.com/en-us/features/storage-explorer" target="_blank">Azure Storage Explorer</a> if needed)
- Check hotels data on incorrect (null) values (Latitude & Longitude). For incorrect values map (Latitude & Longitude) from <a href="https://opencagedata.com/api" target="_blank">OpenCage Geocoding API</a> in job on fly (Via REST API).
- Generate geohash by Latitude & Longitude using one of geohash libraries (like geohash-java) with 4-characters length in extra column.
- Deploy Spark job on Azure Kubernetes Service (AKS), to setup infrastructure use terraform scripts from module. For this use <a href="https://spark.apache.org/docs/latest/running-on-kubernetes.html" target="_blank">Running Spark on Kubernetes</a> deployment guide and corresponding to your spark version <a href="https://github.com/apache/spark/tree/v3.1.1/resource-managers/kubernetes/docker/src/main/dockerfiles/spark" target="_blank">docker image</a>. Development and testing is recommended to do locally in your IDE environment.
- Store enriched data in provisioned with terraform Azure ADLS gen2 storage.

## Expected results
- Repository with Docker, configuration scripts, application sources and etc.
- Upload in task Readme MD file with link on repo, fully documented homework with screenshots and comments.
- Evaluation Criteria

## Evaluation
- Application is working as expected - 45 points.
- Code quality - 15 points.
- Unit tests are provided - 20 points.
- Code is well-documented - 10 points.
- Screenshots and comment - 10 points.
