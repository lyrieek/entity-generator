# Entity Generator
Firstly, generate a cache file based on the database file. Then, developers refer to the cache file to configure the annotation file, and finally generate classes according to the annotations.

## Configuration
database file:
```properties
jdbc.driverClassName=oracle.jdbc.OracleDriver
jdbc.url=jdbc:oracle:thin:@127.0.0.1:1521:data
jdbc.username=user
jdbc.password=qwe123
```

Setting Output Directory
Set the output directory to specify where the generated class files will be stored:
```groovy
entityGenerator {
	output 'generated/po'
}
```
In addition, you need to include the output directory in classes so that the project can use the generated class files, for example:
```groovy
dependencies {
    implementation files("build/generated/po")
}
```
If the generated class files are not needed at runtime and are only used after compilation, the above configuration is not necessary.

## Excluding Fields
Use the following two configurations to exclude fields, and these fields will not be generated in all tables:
- _default_exclude_fields: Used to exclude ordinary fields.
- _default_log_exclude_fields: Used to exclude log-related fields.

## Parent Class Configuration
Use the following two configurations for parent classes, and the generated Entity will inherit these parent classes:
- _default_super: Configure the parent class for ordinary Entities.
- _default_log_super: Configure the parent class for log-related Entities.
Please note that during the generation process, it is necessary to create false temporary parent classes with the same name in memory (not output). The reasons are as follows:

A classloader to load the user's classes has not been found temporarily.
There is no suitable timing to load these parent classes.
## Sequence Configuration
Use the following two configurations for sequences:
- _default_seq: Configure the sequence for ordinary Entities.
- _default_log_seq: Configure the sequence for log-related Entities.

## File Explanation
Cache File: Generated based on the database file, it records information such as the database table structure for developers to refer to.
Annotation File: Developers refer to the cache file to make annotations, which guide the generation of class files.

## Plan Supplements
Optimization of Cache File Generation
Objective: Improve the efficiency and accuracy of cache file generation.
Measures:
Analyze the structure of the database file and optimize the generation algorithm to reduce unnecessary calculations and data processing.
Add version management for cache files. When the database file is updated, only update the changed parts of the cache file to avoid full regeneration.
Provide a validation function for cache files to ensure that the generated cache files are consistent with the database files.
