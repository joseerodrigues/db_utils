package com.cave.utils.dbutil;

class SimpleQueryTranslator implements QueryTranslator{

	@Override
	public String translate(String originalText) {
		return originalText;
	}

}
