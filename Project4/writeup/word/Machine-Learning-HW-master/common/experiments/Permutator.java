package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


public class Permutator implements Iterator<int[]> {

	private final int[] sizes;
	private final int totalCount;
	private int[] next;
	private int numConsumed = 0;

	/**
	 * 
	 * @param sizes all elements must be > 0
	 */
	public Permutator(int[] sizes) {
		this.sizes = sizes;
		next = new int[sizes.length];
		
		totalCount = calcCount();
	}
	
	@Override
	public boolean hasNext() {
		return numConsumed < totalCount;
	}
	
	@Override
	public int[] next() {
		int[] ret = Arrays.copyOf(next, next.length);
		
		boolean shouldUpdate = true;
		
		for (int i = 0; i < next.length && shouldUpdate; i++) {
			next[i]++;
			if (next[i] < sizes[i]) {
				shouldUpdate = false;
			} else {
				for (int j = 0; j <= i; j++) {
					next[j] = 0;
				}
			}
		}
		
		numConsumed++;
		
		return ret;
	}
	
	private int calcCount() {
		int count = 1;
		
		for (int each : sizes) {
			count *= each;
		}
		
		return count;
	}
	
	public int getRemainingCount() {
		return totalCount - numConsumed;
	}
	
	public void reset() {
		for (int i = 0; i < next.length; i++) {
			next[i] = 0;
		}
		
		numConsumed = 0;
	}
	
	public Stream<int[]> stream() {
		List<int[]> configs = new ArrayList<>();
		while (hasNext()) {
			configs.add(next());
		}
		return configs.stream();
		//return Stream.generate(this::next).limit(getRemainingCount()); parallel will break this class
	}
}