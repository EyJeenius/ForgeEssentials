package com.ForgeEssentials.core.moduleLauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface FEModule
{
	/**
	 * this may be null
	 */
	Class<? extends IModuleConfig> configClass();
	
	/**
	 * "Module" is not automatically
	 * ie: "WorldControlModule" "SnooperModule" etc..
	 * this is what will show up in logs, especially about errors.
	 */
	String name();
	
	/**
	 * Marks it as core. Core modules are laoded first. 
	 * @return
	 */
	boolean isCore() default false;
	
	/**
	 * For all built in modules, this had better be the ForgeEssentials class.
	 * For everyone else, this should be your @mod file.
	 * @return
	 */
	Class parentMod();
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface PreInit {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Init {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface PostInit {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface ServerStart {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface ServerStarted {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface ServerStop {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Reload {}
}
