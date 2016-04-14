public class Tuple<X, Y> { 
  private final X fst; 
  private final Y snd; 
  public Tuple(X x, Y y) { 
    this.fst = x; 
    this.snd = y; 
  } 
  
  public X fst() {
	  return this.fst;
  }
  
  public Y snd() {
	  return this.snd;
  }
}