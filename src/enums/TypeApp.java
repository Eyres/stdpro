package enums;

public enum TypeApp {
	RECOMMANDES("Recommand�s"),
	CODEBARRE("Code Barre"),
	COMPTAGEPDF("Comptage PDF"), 
	RENAME("Renommage PDF/TIFF"),
	ACHEMYTOTIFF("Alchemy To Tiff"),
	TIFFTOPDF("Tiff To PDF");
	
	private final String value;

	private TypeApp(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
