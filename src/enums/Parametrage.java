package enums;

public enum Parametrage {
	DISQUE_X("X://"),
	DISQUE_C("C://"),
	RESULTAT("Resultat//");
	
	private final String value;

	private Parametrage(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
