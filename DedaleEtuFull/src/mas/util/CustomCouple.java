package mas.util;


/**
 * Classe qui est utile pour stocker les données avec leur date de saisie
 *
 * @param <T1> 
 * @param <T2> 
 */
public class CustomCouple<T1,T2> {
	private T1 comp1;
	private T2 comp2;

	public CustomCouple(T1 etat, T2 i) {
		this.comp1 = etat;
		this.comp2 = i;
	}

	public T1 getLeft() {
		return this.comp1;
	}

	public T2 getRight() {
		return this.comp2;
	}

}