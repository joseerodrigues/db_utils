package io.github.joseerodrigues.utils.dbutil;

class SimpleQueryTranslator implements QueryTranslator{

	@Override
	public String translate(String originalText) {
		return originalText;
	}

}
