package com.proj4;

import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.oomdp.core.Domain;
import burlap.oomdp.statehashing.HashableStateFactory;

/**
 * code
 */
public class QLearningAgentFactory implements LearningAgentFactory {
    private final Domain domain;
    private final HashableStateFactory hashingFactory;

    public QLearningAgentFactory(Domain domain, HashableStateFactory hashingFactory) {
        this.domain = domain;
        this.hashingFactory = hashingFactory;
    }

    @Override
    public String getAgentName() {
        return "Q-learning";
    }

    @Override
    public LearningAgent generateAgent() {
        return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.1);
    }
}
