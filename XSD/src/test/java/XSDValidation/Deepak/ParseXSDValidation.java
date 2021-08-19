package XSDValidation.Deepak;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.List;
//import java.util.stream.Collectors;

import org.fissore.jrecordbind.DefinitionLoader;
import org.fissore.jrecordbind.RecordDefinition;
import org.fissore.jrecordbind.SimpleLineReader;
import org.fissore.jrecordbind.Unmarshaller;

import xsdvali.breaksony.breaksonyheader.FileHeader;
import xsdvali.breaksony.breaksonyheader.FileTrailer;
import xsdvali.breaksony.breaksonyheader.ChannelHeader;
import xsdvali.breaksony.breaksonyheader.ChannelTrailer;
import xsdvali.breaksony.breaksonyheader.DataRecord;


//import org.jrecordbind.schemas.jrb.BreakSonyHeader.ChannelHeader;





//import com.automation.FixedlengthValidation.ValidateFile;

public class ParseXSDValidation {
	
	public static <T> void main(String[] args) {
		
		String inputFilePath ="C:\\Users\\DEM49\\JAVA_PRACTICE\\Automation\\File_Processing\\BREAK\\SONY\\Source_Files\\DummyFile.BRK";
		
		SonyFileCompare obj = new SonyFileCompare();
		
		
		List<DataRecord> datas = parsefile(System.getProperty("user.dir")+"\\src\\main\\java\\XSDValidation\\Deepak\\BreakSONY_DataRec.xsd",inputFilePath);
		List<FileHeader> header =parsefile(System.getProperty("user.dir")+"\\src\\main\\java\\XSDValidation\\Deepak\\BreakSonyFileHeader.xsd",inputFilePath);
		List<ChannelHeader> ch_header=parsefile(System.getProperty("user.dir")+"\\src\\main\\java\\XSDValidation\\Deepak\\BreakSONY_ChannelHeader.xsd",inputFilePath);
		List<ChannelTrailer> ch_trailer=parsefile(System.getProperty("user.dir")+"\\src\\main\\java\\XSDValidation\\Deepak\\BreakSONY_ChannelTrailer.xsd",inputFilePath);
		List<FileTrailer> file_trailer=parsefile(System.getProperty("user.dir")+"\\src\\main\\java\\XSDValidation\\Deepak\\BreakSONY_FileTrailer.xsd",inputFilePath);
		
		obj.setHeader(header);
		
		Map<String, List<T>> SonySourceFile = new HashMap<String, List<T>>();
		SonySourceFile.put("fileheader", (List<T>) header);
		//System.out.println("Printing:" + SonySourceFile.get("fileheader").get(0).toString());
		//SonySourceFile.get(key)
		
		System.out.println("Printing:" + obj.getHeader().get(0).getRecordType());
		
//		System.out.println(datas.size()+" "+datas.get(0).getBreakPosition());
		System.out.println(header.size());
		System.out.println(ch_header.size());
		System.out.println(datas.size());
		int i;
		if(ch_header.size()>0) {
		for (i=0;i<ch_header.size();i++) {
		System.out.println(ch_header.get(i).getRecordType());
		System.out.println(ch_header.get(i).getChannelCode());
		System.out.println(ch_header.get(i).getTramissionRegion());
		System.out.println(ch_header.get(i).getLogNumber());
		System.out.println(ch_header.get(i).getDate());
		}
		}
		System.out.println(ch_trailer.size());
     	System.out.println(file_trailer.size());
		
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
