# UFCity Handler

UFCity Handler is a software component that integrates the UFCity solution for smart cities. This component belongs to Fog Computing and has the following characteristics:

* Data processing
* Data cleaning
* Identification and removal of outliers
* Data grouping

## How to use?
Create a configuration file `config.yaml`:
- ./
  - ufcity-handler.jar
  - ufcity-handler.config

Example of a `config.yaml` file:
```
fog-computing:
 - address: mqtt
 - port: 1883
cloud-computing:
 - address: 200.137.134.98
 - port: 1889
database:
 - address: mongo
 - port: 27017
data-grouping:
 - method: FIXED_SIZE_GROUPING
 - size: 5 # or 'time' in sec. 
missing-data:
 - method: MEAN_MISSING_DATA_METHOD
removing-outilers:
 - method: Z_SCORE_REMOVE_OUTLIERS_METHOD
 - threshold: 3
# To remove outliers using the method Percentile
#- upper-percentile: 1.5
#- lower-percentile: 1.5
aggregating-data:
 - method: MEAN_AGGREGATION_METHOD
```

- Note 1: Into the Docker environment can use the hostname instead IP.
- Note 2: Use `-h or --help` for see all options.

All methods available:
* Data grouping:
  * FIXED_SIZE_GROUPING
  * HAPPENS_FIRST_GROUPING
  * AT_LEAST_TIME_GROUPING
  * AT_LEAST_TIME_AND_SIZE_GROUPING
* Data aggregation:
  * MEAN_AGGREGATION_METHOD
  * MEDIAN_AGGREGATION_METHOD
  * MAX_AGGREGATION_METHOD
  * MIN_AGGREGATION_METHOD
* Remove outliers:
  * IQR_REMOVE_OUTLIERS_METHOD
  * PERCENTILE_REMOVE_OUTLIERS_METHOD
  * TUKEY_REMOVE_OUTLIERS_METHOD
  * Z_SCORE_REMOVE_OUTLIERS_METHOD
* Missing data:
  * MEAN_MISSING_DATA_METHOD
  * MEDIAN_MISSING_DATA_METHOD
  * LOCF_MISSING_DATA_METHOD
  * INTERPOLATION_MISSING_DATA_METHOD
  * NOCB_MISSING_DATA_METHOD
  * MODE_MISSING_DATA_METHOD


#### Download  UFCity Handler
Download: [ufcity-fog-handler-1.0-SNAPSHOT.jar](build%2Flibs%2Fufcity-fog-handler-1.0-SNAPSHOT.jar)

#### Running the UFCity Handler
`java -jar ufcity-fog-handler-1.0-SNAPSHOT.jar`
