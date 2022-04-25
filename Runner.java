package ExportSchuelerCSV;

import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileWriter;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Runner {

	public static Connection createConnection(String url, String user, String password) {
		try {
			return DriverManager.getConnection(url, user, password);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Datenbank konnte nicht gefunden werden");
			System.exit(1);
			return null; 
		}
		finally {
			System.out.println("+++ Sind im finally +++");
			System.out.println();
		}
	}

	public static void main( String args[] ) {
		try{
			String url = "jdbc:mysql://localhost:3306/pupil2class";				//db verbinden
			String user = "mosi";
			String password = "3105seno";

			Connection c = createConnection(url, user, password);	
			System.out.println("--Connection erstellen--");
			System.out.println("Erfolgreich");
			System.out.println();
			c.setAutoCommit(true); 		//commit -> db Befehle werden ohne rollback sofort ausgeführt
			exportcsv(c);				
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static int createfile(String filename) {			//geht momentan nur einmal (file von hand löschen)
		try {
			File fileObj = new File(filename);
			// hier kontrollieren ob file existiert (exists methode) wenn ja: löschen
			if (fileObj.exists()) {		
				fileObj.delete();							
			}
			if (fileObj.createNewFile()) {					//file wird erzeugt
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void exportcsv (Connection c) { 
		String filename="C:\\Users\\Mosi\\Desktop\\SCHULE\\3te Klasse\\Infie\\pupil2class.csv";			//Pfad angeben (von filename)
		try {
			Statement stmt = c.createStatement();
			String sql = "select pupilname, zuorddatum, klassename from pupil inner join klasse on klasse.klasseid = pupil.pupilclassid;";			//sql abfrage
			ResultSet rs = stmt.executeQuery(sql);																									//ergebnis von sql abfrage steh im rs
			if (createfile(filename) == 1){									//wenn createfile erfolgreich (return = 1 siehe createfile) dann wird exportiert 
				FileWriter myWriter = new FileWriter(filename);				//öffnet datei filename
				while ( rs.next() ) {										//solang rs einträge hat werden diese ins file exportiert 
					String  pupilname = rs.getString("pupilname");
					Date zuorddatum = rs.getDate("zuorddatum");
					String klassename = rs.getString("klassename");
					DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");  
					String exportstr = klassename + ";" + pupilname + ";" + dateFormat.format(zuorddatum) + "\n";		//exportstr zusammenbauen 
					myWriter.write(exportstr);							//schreibt exportstr in file hinein
				}
				myWriter.close();										//schließt datei wieder
			}
			rs.close();
			stmt.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}