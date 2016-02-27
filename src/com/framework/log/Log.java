package com.framework.log;

public interface Log {

	public void error(Throwable e);

	public void info(Exception e);

	public void info(String msg);

	public void error(String msg);

	public void warn(String msg);

	public void debug(String msg);

}
