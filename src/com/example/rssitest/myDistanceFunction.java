package com.example.rssitest;

import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;


public class myDistanceFunction implements DistanceFunction {

	@Override
	public double distance(double[] p1, double[] p2) {
		return Math.abs(p1[0]-p2[0]);
	}

	@Override
	public double distanceToRect(double[] point, double[] min, double[] max) {
		// TODO Auto-generated method stub
		return 0;
	}

}
