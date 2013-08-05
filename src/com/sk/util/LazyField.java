package com.sk.util;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class LazyField<T> {

	private volatile boolean initialized = false;
	private T value;
	private Callable<T> initializer;
	private ReentrantLock initializing = new ReentrantLock();

	public LazyField(Callable<T> initializer) {
		this.initializer = initializer;
	}

	public T get() {
		if (!initialized) {
			try {
				initializing.lock();
				if (!initialized)
					initialize();
			} finally {
				initializing.unlock();
			}
		}
		return value;
	}

	public void set(T value) {
		try {
			initializing.lock();
			this.value = value;
			this.initialized = true;
		} finally {
			initializing.unlock();
		}
	}

	private void initialize() {
		try {
			value = initializer.call();
		} catch (Exception ignored) {
		}
	}
}
