package experiment;

public interface Distinguisher<T> {

	public boolean areEqual(T o1, T o2);
}
