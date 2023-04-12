# UFCity Handler

UFCity Handler is a software component that integrates the UFCity solution for smart cities. This component belongs to Fog Computing and has the following characteristics:

* Data processing
* Data cleaning
* Identification and removal of outliers
* Data grouping

## How to use?
Create a configuration file `ufcity-handler.config` inside the `config` directory:
- ./
  - ufcity-handler.jar
  - config/
    - ufcity-handler.config

Example of a `ufcity-handler.config` file
```
--edge-address 172.20.0.2
--fog-address 172.23.0.4
--cloud-address 172.23.0.5
--edge-port 1883
--fog-port 1883
--cloud-port 1883
```
