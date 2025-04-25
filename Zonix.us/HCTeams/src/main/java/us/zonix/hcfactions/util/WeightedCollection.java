package us.zonix.hcfactions.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author Peter Lawrey
 */

public class WeightedCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private final Random random;
	private double total = 0;
	
	public WeightedCollection() {
		this(new Random());
	}
	
	public WeightedCollection(Random random) {
		this.random = random;
	}
	
	public void add(double weight, E result) {
		if(weight <= 0) return;
		total += weight;
		map.put(total, result);
	}
	
	public E next() {
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}
}