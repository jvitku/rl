package massim.agent.mind.harm.components.hierarchy.util;

public interface HierarchyComponent {
	
	// prepare for the simulation step
	public void preSimulationStep();
	
	// post-processing
	public void postSimulationStep();
}
