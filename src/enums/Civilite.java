package enums;

public enum Civilite {
	MME ("Madame"),

	MADAME ("Madame"),

	MMES ("Mesdames"),

	MESDAMES ("Mesdames"),

	MLLE ("Mademoiselle"),

	MADEMOISELLE ("Mademoiselle"),

	MLLES ("Mesdemoiselles"),

	MESDEMOISELLES ("Mesdemoiselles"),

	MONSIEUR ("Monsieur"),

	MR ("Monsieur"),

	MRS ("Messieurs"),

	MONSIEURS ("Messieurs"),

	VVE ("Veuve"),

	VEUVE ("Veuve"),

	DR ("Docteur"),

	DOCTEUR ("Docteur"),

	DRS ("Docteurs"),

	DOCTEURS ("Docteurs"),

	PR ("Professeur"),

	PROFESSEUR ("Professeur"),

	PRS ("Professeurs"),

	PROFESSEURS ("Professeurs"),

	ME ("Maître"),

	MAITRE ("Maître"),

	MES ("Maîtres"),

	MAITRES ("Maîtres"),

	MGR ("Monseigneur"),

	MONSEIGNEUR ("Monseigneur")
	;
	private final String value;

	private Civilite(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}