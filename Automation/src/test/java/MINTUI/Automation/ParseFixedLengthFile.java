package MINTUI.Automation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.fissore.jrecordbind.DefinitionLoader;
import org.fissore.jrecordbind.RecordDefinition;
import org.fissore.jrecordbind.SimpleLineReader;
import org.fissore.jrecordbind.Unmarshaller;

import XSDClasses.ChannelHeader;


//import com.automation.FixedlengthValidation.ValidateFile;

public class ParseFixedLengthFile {
	
	public static void main(String[] args) {
		
		String inputFilePath ="C:\\Users\\DEM49\\JAVA_PRACTICE\\Automation\\File_Processing\\BREAK\\SONY\\Source_Files\\MOUK202105060539.BRK";
		
		//List<DataRecord> datas = parsefile(System.getProperty("user.dir")+"\\resources\\BreakSONY_DataRec.xsd",inputFilePath);
		//List<FileHeaderNew> header =parsefile(System.getProperty("user.dir")+"\\resources\\BreakSONY_HeaderRecord.xsd",inputFilePath);
		List<ChannelHeader> ch_header=parsefile(System.getProperty("user.dir")+"\\src\\test\\resources\\BreakSONY_ChannelHeader.xsd",inputFilePath);
		//List<ChannelTrailer> ch_trailer=parsefile(System.getProperty("user.dir")+"\\resources\\BreakSONY_ChannelTrailer.xsd",inputFilePath);
		//List<FileTrailer> file_trailer=parsefile(System.getProperty("user.dir")+"\\resources\\BreakSONY_FileTrailer.xsd",inputFilePath);
		
		
//		System.out.println(datas.size()+" "+datas.get(0).getBreakPosition());
//		System.out.println(header.size());
		System.out.println(ch_header.size());
//		System.out.println(ch_trailer.size());
//		System.out.println(file_trailer.size());
		
	}
	
	
	public static <T> List<T> parsefile(String xsd, String inputFile) {
		
		//URL fileUrl = ParseFixedLengthFile.class.getResource(xsd);
		File configFile =new File(xsd);
		RecordDefinition recordDefinition = new DefinitionLoader().load(configFile);

		 InputStream input = null;
		try {
			input = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Unmarshaller<T> data_unmarshaller = new Unmarshaller<T>(recordDefinition,
				new SimpleLineReader());
		List<T> datarecords =  data_unmarshaller.unmarshallToStream(new InputStreamReader(input))
				.collect(Collectors.toList());

		return datarecords;

	}
	
	
}
