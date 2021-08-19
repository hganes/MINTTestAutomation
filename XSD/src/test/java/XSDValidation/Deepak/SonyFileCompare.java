package XSDValidation.Deepak;

import java.util.List;

import xsdvali.breaksony.breaksonyheader.ChannelHeader;
import xsdvali.breaksony.breaksonyheader.ChannelTrailer;
import xsdvali.breaksony.breaksonyheader.DataRecord;
import xsdvali.breaksony.breaksonyheader.FileHeader;
import xsdvali.breaksony.breaksonyheader.FileTrailer;

public class SonyFileCompare {

	List<DataRecord> datas;
	List<FileHeader> header;
	List<ChannelHeader> ch_header;
	List<ChannelTrailer> ch_trailer;
	List<FileTrailer> file_trailer;

	
	public List<DataRecord> getDatas() {
		return datas;
	}
	public void setDatas(List<DataRecord> datas) {
		this.datas = datas;
	}
	public List<FileHeader> getHeader() {
		return header;
	}
	public void setHeader(List<FileHeader> header) {
		this.header = header;
	}
	public List<ChannelHeader> getCh_header() {
		return ch_header;
	}
	public void setCh_header(List<ChannelHeader> ch_header) {
		this.ch_header = ch_header;
	}
	public List<ChannelTrailer> getCh_trailer() {
		return ch_trailer;
	}
	public void setCh_trailer(List<ChannelTrailer> ch_trailer) {
		this.ch_trailer = ch_trailer;
	}
	public List<FileTrailer> getFile_trailer() {
		return file_trailer;
	}
	public void setFile_trailer(List<FileTrailer> file_trailer) {
		this.file_trailer = file_trailer;
	}
	
}
