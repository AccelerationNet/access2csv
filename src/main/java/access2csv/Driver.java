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

  
	static int export(Database db, String tableName, Writer csv, boolean withHeader, boolean applyQuotesToAll, String nullText) throws IOException{
		Table table = db.getTable(tableName);
		String[] buffer = new String[table.getColumnCount()];
		CSVWriter writer = new CSVWriter(new BufferedWriter(csv), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		int rows = 0;
		try{
			if (withHeader) {
				int x = 0;
				for(Column col : table.getColumns()){
					buffer[x++] = col.getName();
				}
				writer.writeNext(buffer, applyQuotesToAll);
			}
            
			for(Row row : table){
				int i = 0;
				for (Object object : row.values()) {
					buffer[i++] = object == null ? nullText : object.toString();
				}
				writer.writeNext(buffer, applyQuotesToAll);
				rows++;
			}
		}finally{
			writer.close();
		}
		return rows;
	}

	static void export(File inputFile, String tableName, boolean withHeader, File outputDir, String csvPrefix, boolean applyQuotesToAll, String nullText) throws IOException{
		Database db = DatabaseBuilder.open(inputFile);
		try{
			export(db, tableName, new FileWriter(new File(outputDir, csvPrefix + tableName + ".csv")), withHeader, applyQuotesToAll, nullText);
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

	static void exportAll(File inputFile, boolean withHeader, File outputDir, String csvPrefix, boolean applyQuotesToAll, String nullText) throws IOException{
		Database db = DatabaseBuilder.open(inputFile);
		try{
			for(String tableName : db.getTableNames()){
				String csvName = csvPrefix + tableName + ".csv";
				File outputFile = new File(outputDir, csvName);
				Writer csv = new FileWriter(outputFile);
				try{
					System.out.println(String.format("Exporting '%s' to %s",
							tableName, outputFile.toString()));
					int rows = export(db, tableName, csv, withHeader, applyQuotesToAll, nullText);
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

	public static void main(String[] args) throws Exception {
		final OptionParser parser = new OptionParser();

		final OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("help")).forHelp();
		final OptionSpec<String> schema = parser.accepts("schema").withOptionalArg()
				.describedAs("The schema is written to standard output.");
		final OptionSpec<String> withHeader = parser.accepts("with-header").withOptionalArg()
				.describedAs("When with-header is included, a header line of column names is written to each data file.");
		final OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required()
				.describedAs("The input accdb file.");
		final OptionSpec<String> table = parser.accepts("table").withRequiredArg().ofType(String.class).describedAs("The table name to export, or all if it is not specified.");
		final OptionSpec<File> output = parser.accepts("output").requiredUnless("schema").withRequiredArg().ofType(File.class)
				.describedAs("The output directory for data files. This is required for writing data output. This not required for schema output.");
		final OptionSpec<String> csvPrefix = parser.accepts("csv-prefix").withRequiredArg().ofType(String.class).defaultsTo("").describedAs("A prefix to add to all of the generated CSV file names");    
		final OptionSpec<Boolean> quoteAll = parser.accepts("quote-all").withOptionalArg().ofType(Boolean.class).defaultsTo(true)
				.describedAs("Set quote-all to true if all values are to be quoted. " +
				"Set to false if quotes are only to be applied to values which contain " + 
				"the separator, secape, quote, or new line characters. The default is true.");
		final OptionSpec<String> writeNull = parser.accepts("write-null").withOptionalArg().ofType(String.class).defaultsTo("")
				.describedAs("The text to write when entry is NULL. Defaults to empty output if not specified or if no argument supplied. " +
				"If quote-all is set to true then the value for write-null is also quoted.");
   
    
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
		
		File outputDir = null;
		if (options.has(output)) {
			outputDir = output.value(options);
			if(!outputDir.exists()) {
				outputDir.mkdirs();
			}	
		}
    		
		boolean applyQuotesToAll = quoteAll.value(options);    
    String nullText = writeNull.value(options);
    
		if (options.has(schema)) {
			schema(inputFile);
		}
		
		if (null != outputDir) {
			if (options.has(table)){
				export(inputFile, table.value(options), options.has(withHeader), outputDir, csvPrefix.value(options), applyQuotesToAll, nullText);
			}
			else {
				exportAll(inputFile, options.has(withHeader), outputDir, csvPrefix.value(options), applyQuotesToAll, nullText);
			}	
		}
	}

}
