package org.hanns.rl.discrete.sarsa.config.impl;

import org.hanns.rl.discrete.sarsa.config.Configuration;

public class BasicConfiguration implements Configuration{
	

	// stochastic return predictor settings
	private double alpha;
	private double gamma;
	private double minEpsilon;
	// eligibility settings
	private int eligibilityLength;
	private double lambda;
	
	@Override
	public void setAlpha(double alpha) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getAlpha() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setGamma(double gamma) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getGamma() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setMinEpsilon(double min) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getMinEpsilon(double min) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setEligibilityLength(int length) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getEligibilityLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	

}
