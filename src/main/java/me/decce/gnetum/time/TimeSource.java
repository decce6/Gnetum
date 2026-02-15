package me.decce.gnetum.time;

public interface TimeSource {
	double get();
	long millis();
	long nanos();
}
