package com.jg.db;

public abstract class JGDBLoggingDef {
	abstract protected void beforeExecuteQuery(String query_, Object[] parameters_);
	abstract protected void beforeExecuteUpdate(String query_, Object[] parameters_);
	abstract protected void beforeCallProcedure(String query_, Object[] parameters_);
}
