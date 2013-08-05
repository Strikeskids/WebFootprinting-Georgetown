package com.sk.util;

public interface StringProcessor {
	public String process(String input);

	public static final StringProcessor NIL = new StringProcessor() {
		@Override
		public String process(String input) {
			return input;
		}
	};
}
