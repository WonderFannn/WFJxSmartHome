package com.tgb.lk.ahibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { java.lang.annotation.ElementType.FIELD })
public @interface Index {
	/**
	 * 列名
	 * 
	 * @return
	 */
	public abstract String indexname();

	public abstract String columnname();

	public abstract int indextype()default 0;
}