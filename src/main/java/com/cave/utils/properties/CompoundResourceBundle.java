package com.cave.utils.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class CompoundResourceBundle extends ResourceBundle{

	private static final String CLASSNAME = CompoundResourceBundle.class.getSimpleName();
	private static final Logger logger = LoggerFactory.getLogger(CompoundResourceBundle.class);
	
	private Map<String, List<ResourceBundle>> bundleKeys = new HashMap<String, List<ResourceBundle>>();
	
	private static class SetEnumeration<T> implements Enumeration<T>{	
		private Iterator<T> it = null;
		
		SetEnumeration(Set<T> set) {
			this.it = set.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return this.it.hasNext();
		}

		@Override
		public T nextElement() {			
			return this.it.next();
		}		
	}
	
	CompoundResourceBundle(List<ResourceBundle> bundles) {				
		for (ResourceBundle bundle : bundles){
			Set<String> keys = bundle.keySet();
			
			for (String key : keys){
				
				List<ResourceBundle> elegibleBundles = this.bundleKeys.get(key);
				
				if (elegibleBundles == null){
					elegibleBundles = new ArrayList<ResourceBundle>();
					this.bundleKeys.put(key, elegibleBundles);
				}
				
				elegibleBundles.add(bundle);
			}
		}
	}

	CompoundResourceBundle(ResourceBundle ... bundles) {
		this(Arrays.asList(bundles));
	}
	
	@Override
	public Enumeration<String> getKeys() {
		return new SetEnumeration<String>(this.bundleKeys.keySet());
	}

	@Override
	protected Object handleGetObject(String key) {					
		List<ResourceBundle> elegibleBundles = this.bundleKeys.get(key);
		
		if (elegibleBundles == null){
			throw new MissingResourceException(key + " not found", CLASSNAME, key);
		}
		
		ResourceBundle bundle = elegibleBundles.get(0);
		
		if (elegibleBundles.size() > 1){			
			logger.warn("key '" + key + "' found in multiple ResourceBundles. Returning the value from the first one.");		
		}
		
		return bundle.getObject(key);
	}

}
