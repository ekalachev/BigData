# Spark basics
- bdcc-data-etl - module with source code to implement etl logic
- terraform - infrastructure as code to deploy all resources in cloud

## Spark for windows
- [Installation guide](https://phoenixnap.com/kb/install-spark-on-windows-10).

## Homework
- [Homework Repository](./homework)

## Qustions:
- [That is spark?](#what_is_spark)
- [Spark Installation and Deployment](#spark_instalation_deployment)
- [Spark RDD. Transformations and Actions](#Spark_RDD_transformations_and_actions)
- [Pair RDD. Shared Variables. Shared Variables Caching. Smart Sources](#pair_RDD_shared_variables)
- [Datasets and DataFrames. Spark ETL](#datasets_and_dataFrames_Spark_ETL)
- [SQL Joins. Tuning Techniques. Debug and Test. Catalyst. CBO. Tungsten](#SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten)

### That is spark? <a name="what_is_spark"></a>
1. What is TRUE about Spark? ![What is TRUE about Spark?](./images/what_is_spark1.png)
2. Which of the following is NOT a characteristic shared by Hadoop and Spark? ![Which of the following is NOT a characteristic shared by Hadoop and Spark?](./images/what_is_spark2.png)
3. MapReduce program can be developed in spark. ![MapReduce program can be developed in spark.](./images/what_is_spark3.png)
4. Choose correct statement about Spark Context. ![Choose correct statement about Spark Context.](./images/what_is_spark4.png)
5. How many Spark Context can be active per JVM? ![How many Spark Context can be active per JVM?](./images/what_is_spark5.png)

### Spark Installation and Deployment <a name="spark_instalation_deployment"></a>
1. Which Cluster Manager does Spark support? ![Which Cluster Manager does Spark support?](./images/spark_instalation_deployment1.png)
2. Deployment modes are supported by Spark for YARN cluster: ![Deployment modes are supported by Spark for YARN cluster](./images/spark_instalation_deployment2.png)
3. What cluster scheduler(s) is/are supported by Spark? ![What cluster scheduler(s) is/are supported by Spark?](./images/spark_instalation_deployment3.png)
4. Spark UI is unavailable while Driver is running. ![Spark UI is unavailable while Driver is running](./images/spark_instalation_deployment4.png)
5. The second stage of word counting application from the example in the video includes the following operations: ![The second stage of word counting application from the example in the video includes the following operations](./images/spark_instalation_deployment5.png)

### Spark RDD. Transformations and Actions <a name="Spark_RDD_transformations_and_actions"></a>
1. What is Spark Core Abstraction? ![What is Spark Core Abstraction?](./images/Spark_RDD_transformations_and_actions1.png)
2. Fault Tolerance in RDD is achieved using: ![Fault Tolerance in RDD is achieved using](./images/Spark_RDD_transformations_and_actions2.png)

### Pair RDD. Shared Variables. Shared Variables Caching. Smart Sources: <a name="pair_RDD_shared_variables"></a>
1. RDD operations: ![RDD operations](./images/pair_RDD_shared_variables1.png)
2. Identify correct transformation. ![Identify correct transformation](./images/pair_RDD_shared_variables2.png)
3. What types of shared variables does spark provide? ![](./images/pair_RDD_shared_variables3.png)
4. What is true regarding shared variables? ![What types of shared variables does spark provide?](./images/pair_RDD_shared_variables4.png)
5. Identify correct ways to cache rdd. ![Identify correct ways to cache rdd.](./images/pair_RDD_shared_variables5.png)
6. Sparks provides different storage levels for performing caching data across partitions. ![Sparks provides different storage levels for performing caching data across partitions](./images/pair_RDD_shared_variables6.png)
7. What advantages in terms of “Smart” are correct? ![What advantages in terms of “Smart” are correct?](./images/pair_RDD_shared_variables7.png)

###  Datasets and DataFrames. Spark ETL <a name="datasets_and_dataFrames_Spark_ETL"></a>
1. DataFrame is a Dataset of Row. ![DataFrame is a Dataset of Row.](./images/datasets_and_dataFrames_Spark_ETL1.png)
2. DataFrame API is: ![DataFrame API is](./images/datasets_and_dataFrames_Spark_ETL2.png)
3. Which of the following is wrong Spark terminology? ![Which of the following is wrong Spark terminology?](./images/datasets_and_dataFrames_Spark_ETL3.png)
4. Which of the following is not true for DataFrame? ![Which of the following is not true for DataFrame?](./images/datasets_and_dataFrames_Spark_ETL4.png)
5. ETL is short for: ![ETL is short for](./images/datasets_and_dataFrames_Spark_ETL5.png)
6. The purpose of Extraction step is: ![The purpose of Extraction step is](./images/datasets_and_dataFrames_Spark_ETL6.png)
7. Data Type Conversion, Data Aggregation, Data Encryption are examples of: ![Data Type Conversion, Data Aggregation, Data Encryption are examples of](./images/datasets_and_dataFrames_Spark_ETL7.png)

### SQL Joins. Tuning Techniques. Debug and Test. Catalyst. CBO. Tungsten <a name="SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten"></a>
1. How can we make spark to perform BroadcastJoin? ![How can we make spark to perform BroadcastJoin?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten1.png)
2. What optimization setting should help in case 95-99% of a task is already finished and the rest works too slow? ![What optimization setting should help in case 95-99% of a task is already finished and the rest works too slow?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten2.png)
3. What are possible options to increase the level of parallelism in Spark? ![What are possible options to increase the level of parallelism in Spark?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten3.png)
4. What are possible ways to monitor spark job? ![What are possible ways to monitor spark job?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten4.png)
5. What are the major components of Spark SQL execution engine? ![What are the major components of Spark SQL execution engine?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten5.png)
6. What types of statistics does Cost-Based Optimizer support? ![What types of statistics does Cost-Based Optimizer support?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten6.png)
7. What benefits does Tungsten project provide? ![What benefits does Tungsten project provide?](./images/SQL_joins_Tuning_echniques_Debug_and_test_Catalyst_CBO_Tungsten7.png)
