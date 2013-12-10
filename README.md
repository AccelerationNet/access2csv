# access2csv

Simple program to extract data from Access databases into CSV files.

## Features

 * view the schema of the database
 * export all tables to csv files named after the table
 * export one table

## Examples

Dumping a schema:

    $ java -jar access2csv.jar myfile.accdb --schema	
	CREATE TABLE Test(
		Id INT,
		Name TEXT,
	)
	CREATE TABLE Test2(
		Id INT,
		Name TEXT
	)
	
Exporting all tables:

    $ java -jar access2csv.jar myfile.accdb
	Exporting 'Test' to /home/ryepup/Test.csv
	2 rows exported
	Exporting 'Test2' to /home/ryepup/Test2.csv
	100000 rows exported
	
Export one table:

    $ java -jar access2csv.jar myfile.accdb Test
	1,"foo"
	2,"bar"

## Installation

Binaries are available at
https://github.com/AccelerationNet/access2csv/releases, download a jar
file from there then use it as shown above.

### Compile from source

    $ git clone https://github.com/AccelerationNet/access2csv.git
	$ cd access2csv
	$ ant
	
Now you should have an `access2csv.jar`, ready to go.

## Depenencies

 * [Jackess](http://jackcess.sourceforge.net/) - a pure Java library
   for reading from and writing to MS Access databases
 * [opencsv](http://opencsv.sourceforge.net/) - CSV library

## Contributing

Use https://github.com/AccelerationNet/access2csv to open issues or
pull requests.
