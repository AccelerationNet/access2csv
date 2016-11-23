package access2csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

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
	
	static Database openDatabase( String filename, String db_passwd ) throws IOException{
		if( db_passwd.equals("")) {
			Database db = DatabaseBuilder.open(new File(filename));
			return( db ) ;
		} else {
			Database db = new DatabaseBuilder(new File(filename))
				.setCodecProvider(new CryptCodecProvider(db_passwd))
				.open();
			return( db ) ;
		}
	}

	static void export(String filename, String tableName, boolean withHeader, String db_passwd) throws IOException{
		Database db = openDatabase( filename, db_passwd ) ;
		
		try{
			export(db, tableName, new PrintWriter(System.out), withHeader);
		}finally{
			db.close();
		}
	}

	static void schema(String filename, String db_passwd) throws IOException{
		Database db = openDatabase( filename, db_passwd ) ;

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

	static void exportAll(String filename, boolean withHeader, String db_passwd) throws IOException{
		Database db = openDatabase( filename, db_passwd ) ;

		try{
			for(String tableName : db.getTableNames()){
				String csvName = tableName + ".csv";
				Writer csv = new FileWriter(csvName);
				try{
					System.out.println(String.format("Exporting '%s' to %s/%s",
							tableName, System.getProperty("user.dir"), csvName));
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
		System.out.println(" * '--password password' - tries to open an encrypted database with the specified password");
		System.out.println(" * '--schema' - prints the database schema");
		System.out.println(" * '--with-header' - export the header with the field names");
		System.out.println(" * [TABLENAME] - prints the given table as CSV to stdout");
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] cmdLineArgs) throws IOException {
		List<String> helpCommands = Arrays.asList(new String[]{"-h", "--help", "-H", "/?"});
		List<String> passwdCommands = Arrays.asList(new String[]{"-p", "--password", "--passwd"});
		
		// Make a copy of the command line args and then
		// process them to remove and record any passwords
		// and note any --with-header options
		String password = "" ;
		boolean includeHeaders = false ; // the default
		List<String> argList = new ArrayList<>() ;
		for( int j=0 ; j < cmdLineArgs.length ; j++ ) {
			if( passwdCommands.contains(cmdLineArgs[j]) ) {
				if( ++j < cmdLineArgs.length ) {
					password = cmdLineArgs[j] ;
				} else {
					printUsage();
					System.exit(0);
				}
			} 
			else if( cmdLineArgs[j].equals("--with-header") ) {
				includeHeaders = true ;
			}
			else {
				// Save the arg to handle later
				argList.add( cmdLineArgs[j] ) ;
			}
		}
				
		// Copy argList into an array so that the block below will work
		String[] args = argList.toArray(new String[argList.size()]) ;
		
		// Handle remaining args
		if( args.length == 1 ) {
			if( helpCommands.contains(args[0]) ){
				printUsage();
			}
			else {
				exportAll(args[0], includeHeaders, password);
			}
			
			System.exit(0);
		}
		else if( args.length == 2 ) {
			if( args[1].equals("--schema") ){
				schema(args[0], password) ;
			}
			else {
				export(args[0], args[1], includeHeaders, password);
			}
			
			System.exit(0) ;
		}
		else {
			System.err.println("Invalid arguments.");
			printUsage();
			System.exit(1);
		}
	}
}
