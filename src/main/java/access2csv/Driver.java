package access2csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import com.healthmarketscience.jackcess.*;

public class Driver {

	static int export(Database db, String tableName, Writer csv, boolean withHeader) throws IOException{
		Table table = db.getTable(tableName);
		String[] buffer = new String[table.getColumnCount()];
		CSVWriter writer = new CSVWriter(new BufferedWriter(csv));
		int rows = 0;
		try{
			if (withHeader) {
				int x = 0;
				for(Column col : table.getColumns()){
					buffer[x++] = col.getName();
				}
				writer.writeNext(buffer);
			}

			for(Row row : table){
				int i = 0;
				for (Object object : row.values()) {
					buffer[i++] = object == null ? null : object.toString();
				}
				writer.writeNext(buffer);
				rows++;
			}
		}finally{
			writer.close();
		}
		return rows;
	}

	static void export(File inputFile, String tableName, File outputDir) throws IOException{
		Database db = DatabaseBuilder.open(inputFile);
		try{
			export(db, tableName, new FileWriter(new File(outputDir, tableName + ".csv")), false);
		}finally{
			db.close();
		}
	}

	static void schema(File inputFile) throws IOException{

		Database db = DatabaseBuilder.open(inputFile);
		try{
			for(String tableName : db.getTableNames()){
				Table table = db.getTable(tableName);
				System.out.println(String.format("CREATE TABLE %s (", tableName));
				for(Column col : table.getColumns()){
					System.out.println(String.format("  %s %s,", 
							col.getName(), col.getType()));
				}
				System.out.println(")");
			}
		}finally{
			db.close();
		}

	}

	static void exportAll(File inputFile, boolean withHeader, File outputDir) throws IOException{
		Database db = DatabaseBuilder.open(inputFile);
		try{
			for(String tableName : db.getTableNames()){
				String csvName = tableName + ".csv";
				File outputFile = new File(outputDir, csvName);
				Writer csv = new FileWriter(outputFile);
				try{
					System.out.println(String.format("Exporting '%s' to %s",
							tableName, outputFile.toString()));
					int rows = export(db, tableName, csv, withHeader);
					System.out.println(String.format("%d rows exported", rows));
				}finally{
					try{
						csv.flush();
						csv.close();
					}catch(IOException ex){}
				}
			}
		}finally{
			db.close();
		}

	}

	static void printUsage(){
		System.out.println("Usage:");
		System.out.println(" java -jar access2csv.jar [ACCESS FILE] [OPTIONS]");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("");
		System.out.println(" * if no options are provided, all tables will be exported to CSV files,");
		System.out.println("   one file per table. Output file paths will be printed to stdout");
		System.out.println(" * '--schema' - prints the database schema");
		System.out.println(" * '--with-header' - export the header with the field names");
		System.out.println(" * [TABLENAME] - prints the given table as CSV to stdout");
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		List<String> helpCommands = Arrays.asList(new String[]{"-h", "--help", "-H", "/?"});

		final OptionParser parser = new OptionParser();

		final OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("help")).forHelp();
		final OptionSpec<Void> schema = parser.accepts("schema");
		final OptionSpec<Void> withHeader = parser.accepts("with-header");
		final OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required()
				.describedAs("The input accdb file.");
		final OptionSpec<String> table = parser.accepts("table").withRequiredArg().ofType(String.class).describedAs("The table name to export, or all if it is not specified.");
		final OptionSpec<File> output = parser.accepts("output").withRequiredArg().ofType(File.class).required()
				.describedAs("The output directory.");

		OptionSet options = null;

		try {
			options = parser.parse(args);
		} catch (final OptionException e) {
			System.out.println(e.getMessage());
			parser.printHelpOn(System.out);
			throw e;
		}

		if (options.has(help)) {
			parser.printHelpOn(System.out);
			return;
		}

		File inputFile = input.value(options);
		if(!inputFile.exists()) {
			throw new FileNotFoundException("Could not find input file: " + inputFile.toString());
		}
		
		File outputDir = output.value(options);
		if(!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		if(options.has(schema)) {
			exportAll(inputFile, options.has(withHeader), outputDir);
		}
		else if (options.has(table)){
			export(inputFile, table.value(options), outputDir);
		}
		else {
			exportAll(inputFile, false, outputDir);
		}
	}

}
