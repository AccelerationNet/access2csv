# access2csv

Simple program to extract data from Access databases into CSV files.

## Features

 * view the schema of the database
 * export all tables to csv files named after the table
 * export one table

## Examples

Dumping a schema:

    $ ./access2csv myfile.accdb --schema	
	CREATE TABLE Test(
		Id INT,
		Name TEXT,
	)
	CREATE TABLE Test2(
		Id INT,
		Name TEXT
	)
	
Exporting all tables:

    $ ./access2csv myfile.accdb
	Exporting 'Test' to /home/ryepup/Test.csv
	2 rows exported
	Exporting 'Test2' to /home/ryepup/Test2.csv
	100000 rows exported
	
Export one table:

    $ ./access2csv myfile.accdb Test
	1,"foo"
	2,"bar"

## Installation

Binaries are available at
https://github.com/AccelerationNet/access2csv/releases, download a jar
file from there then use it as shown above.

### Compile from source

    $ git clone https://github.com/AccelerationNet/access2csv.git
    $ cd access2csv
    $ mvn clean install
	
Now you should have a `access2csv.jar` in the target directory, ready to go.

## Depenencies

 * [Jackess](http://jackcess.sourceforge.net/) - a pure Java library
   for reading from and writing to MS Access databases
 * [opencsv](http://opencsv.sourceforge.net/) - CSV library

## Contributing

Use https://github.com/AccelerationNet/access2csv to open issues or
pull requests.
