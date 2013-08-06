package com.sk.util;

import java.util.concurrent.Callable;

public class LazyField<T> {

	private volatile boolean initialized = false;
	private T value;
	private final Callable<T> initializer;
	private final Object initLock = new Object();

	public LazyField(Callable<T> initializer) {
		this.initializer = initializer;
	}

	public T get() {
		if (!initialized) {
			synchronized (initLock) {
				if (!initialized)
					initialize();
			}
		}
		return value;
	}

	public void set(T value) {
		synchronized (initLock) {
			this.value = value;
			this.initialized = true;
		}
	}

	private void initialize() {
		try {
			value = initializer.call();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}
}
