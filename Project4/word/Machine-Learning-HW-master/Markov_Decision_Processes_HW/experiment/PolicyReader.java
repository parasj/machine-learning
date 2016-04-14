package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import rl.Policy;
import maze.NegativeMazeMDP;

public class PolicyReader {
	
	private Map<Character, Integer> actionMap;

	public PolicyReader(Map<Character, Integer> actionMap) {
		this.actionMap = actionMap;
	}

	public Policy readPolicy(String policyPath, NegativeMazeMDP maze) throws FileNotFoundException {
		int[] policy = new int[maze.getWidth() * maze.getHeight()];
		try(Scanner scan = new Scanner(new File(policyPath))) {
			for (int row = 0; row < maze.getHeight(); row++) {
				String line = scan.nextLine();
				for (int col = 0; col < maze.getWidth(); col++) {
					char curr = line.charAt(col);
					
					int action;
					if (actionMap.containsKey(curr)) {
						action = actionMap.get(curr);
					} else {
						action = -1;
					}
					
					policy[maze.stateFor(col, row)] = action;
				}
			}
		}
		
		return new Policy(policy);
	}
}
