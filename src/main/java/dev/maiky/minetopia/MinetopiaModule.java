package dev.maiky.minetopia;

public interface MinetopiaModule {

	boolean isEnabled();
	void reload();
	void enable();
	void disable();
	String getName();

}
