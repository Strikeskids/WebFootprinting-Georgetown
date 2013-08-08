package com.sk.util;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class LazyField<T> {

	private AtomicBoolean initialized = new AtomicBoolean(false);
	private T value;
	private final Callable<T> initializer;
	private final Object initLock = new Object();

	public LazyField(Callable<T> initializer) {
		this.initializer = initializer;
	}

	public T get() {
		if (!initialized.get()) {
			synchronized (initLock) {
				if (!initialized.get())
					initialize();
			}
		}
		return value;
	}

	public void set(T value) {
		synchronized (initLock) {
			this.value = value;
			this.initialized.set(true);
		}
	}

	private void initialize() {
		synchronized (initLock) {
			try {
				value = initializer.call();
				initialized.set(true);
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
	}
}
