package com.levo.generics.g_reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Injector2 {
	
	private Map<Class<?>, Object> objectGraph = new HashMap<>();
	
	public Injector2 with(Object value) {
		objectGraph.put(value.getClass(), value);
		return this;
	}
	
	public <T> T newInstance(final Class<T> className) {
		return (T) objectGraph.computeIfAbsent(className, this::instantiate);
	}
	
	private Object instantiate(Class<?> type) {
		try {
			Constructor<?>[] constructors = type.getConstructors();
			if(constructors.length != 1) {
				throw new IllegalArgumentException(type + " must only have 1 constructor");
			}
			
			Constructor<?> constructor = constructors[0];
			Object[] args = Stream
								.of(constructor.getParameterTypes())
								.map(param -> (Object) newInstance(param))
								.toArray();
			
			return constructor.newInstance(args);
		} catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
