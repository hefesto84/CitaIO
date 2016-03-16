package es.ubiqua.citaio.model;

public class GCMModel {
	
	public String getUrl() {
		return url;
	}

	public long getFrom() {
		return from;
	}

	public String getKind() {
		return kind;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	private String url;
	private long from;
	private String kind;
	
	public GCMModel(){
		
	}
}
