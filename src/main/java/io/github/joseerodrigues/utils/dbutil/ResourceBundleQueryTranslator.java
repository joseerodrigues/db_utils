package io.github.joseerodrigues.utils.dbutil;

import java.util.ResourceBundle;

import io.github.joseerodrigues.utils.properties.InterpolationResourceBundle;

public class ResourceBundleQueryTranslator implements QueryTranslator{

	private ResourceBundle bundle = null;
	
	
	public ResourceBundleQueryTranslator(ResourceBundle bundle) {
		this.bundle = bundle;
	}


	@Override
	public String translate(String originalText) {		
		return InterpolationResourceBundle.interpolate(originalText, this.bundle);
	}

}
