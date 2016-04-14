package experiment;

import java.util.ArrayList;
import java.util.List;

import rl.Policy;

public class PolicyRangeFinder {

	private final double absoluteMin;
	private final double absoluteMax;
	private final double initialIncrease;
	private final double precision;
	private final MazeFactory mazeFactory;
	
	public final String settingName;

	public PolicyRangeFinder(MazeFactory mazeFactory, double absoluteMin,
			double absoluteMax, double initialIncrease, double precision, String settingName) {
		this.mazeFactory = mazeFactory;
		this.absoluteMin = absoluteMin;
		this.absoluteMax = absoluteMax;
		this.initialIncrease = initialIncrease;
		this.precision = precision;
		this.settingName = settingName;
	}
	
	public List<PolicyRange> findPolicyRanges(Distinguisher<Policy> policyDistinguisher,
			char[][] maze, PolicyCalculator policyCalculator) {
		List<PolicyRange> policies = new ArrayList<>();
		
		Policy absoluteUpperPolicy = policyCalculator.calcPolicy(mazeFactory.apply(maze, absoluteMax));
		
		Policy lowerPolicy = policyCalculator.calcPolicy(mazeFactory.apply(maze, absoluteMin));
		PolicyRange lowerRange = new PolicyRange(absoluteMin, absoluteMin, lowerPolicy);

		double upperMaxReward = 0;

		while (!policyDistinguisher.areEqual(lowerRange.policy, absoluteUpperPolicy)) {
			double increase = initialIncrease;
			upperMaxReward = lowerRange.maxReward + increase;

			Policy upperPolicy = policyCalculator.calcPolicy(mazeFactory.apply(maze, upperMaxReward));
			
			while (policyDistinguisher.areEqual(lowerRange.policy, upperPolicy)) {
				lowerRange.maxReward = upperMaxReward;
				upperMaxReward = upperMaxReward + 2 * increase;

				if (upperMaxReward >= absoluteMax) {
					upperPolicy = absoluteUpperPolicy;
					break;
				}

				upperPolicy = policyCalculator.calcPolicy(mazeFactory.apply(maze, upperMaxReward));
			}
			
			PolicyRange upperRange = new PolicyRange(upperMaxReward, upperMaxReward, upperPolicy);
			List<PolicyRange> newPolicyRanges = findPolicyRanges(
					policyDistinguisher, policyCalculator, lowerRange, upperRange, maze);
			policies.addAll(newPolicyRanges);
			
			lowerRange = upperRange;
		}
		
		lowerRange.maxReward = absoluteMax;
		policies.add(lowerRange);
		
		return policies;
	}
	
	/**
	 * 
	 * @param mazeFactory
	 * @param learnerFactory
	 * @param policyDistinguisher
	 * @param trainerFactory
	 * @param lowerPolicy
	 * @param upperPolicy Fields modified as a side-effect
	 * @return A list of policies between lowerPolicy (inclusive) and upperPolicy (exclusive)
	 */
	private List<PolicyRange> findPolicyRanges(Distinguisher<Policy> policyDistinguisher,
			PolicyCalculator policyCalculator, PolicyRange lowerPolicy,
			PolicyRange upperPolicy, char[][] maze) {
		List<PolicyRange> policies = new ArrayList<>();
		
		double middleReward = (upperPolicy.minReward + lowerPolicy.maxReward) / 2;
		while (upperPolicy.minReward - lowerPolicy.maxReward > precision) {
			Policy middle = policyCalculator.calcPolicy(mazeFactory.apply(maze, middleReward));

			if (policyDistinguisher.areEqual(middle, lowerPolicy.policy)) {
				lowerPolicy.maxReward = middleReward;
				middleReward = (upperPolicy.minReward + middleReward) / 2;
			} else if (policyDistinguisher.areEqual(middle, upperPolicy.policy)) {
				upperPolicy.minReward = middleReward;
				middleReward = (middleReward + lowerPolicy.maxReward) / 2;
			} else {
				PolicyRange middleRange = new PolicyRange(middleReward, middleReward, middle);
				List<PolicyRange> bottomPolicies = findPolicyRanges(
						policyDistinguisher, policyCalculator, lowerPolicy, middleRange, maze);

				policies.addAll(bottomPolicies);
				lowerPolicy = middleRange;

				middleReward = (lowerPolicy.maxReward + upperPolicy.minReward) / 2;
			}
		}
		
		policies.add(lowerPolicy);
		System.out.println("Found new policy shift after " + lowerPolicy.maxReward);

		return policies;
	}
}
