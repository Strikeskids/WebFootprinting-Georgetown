package com.sk.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskGroup extends ArrayList<Runnable> {

	private static final long serialVersionUID = -2101132946453242018L;
	private List<Future<?>> futures = new ArrayList<Future<?>>();

	public TaskGroup() {

	}

	public void submit(ExecutorService exec) {
		futures.clear();
		for (Runnable run : this) {
			futures.add(exec.submit(run));
		}
	}

	public boolean isDone() {
		for (Future<?> f : futures) {
			if (!f.isDone())
				return false;
		}
		return true;
	}

	public boolean waitFor() {
		for (Future<?> f : futures) {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
