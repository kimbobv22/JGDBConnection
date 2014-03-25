package com.jg.db;

import java.io.InputStream;
import java.io.Reader;

public abstract class JGDBLoggingDef {
	abstract protected void beforeExecuteQuery(String query_, Object[] parameters_);
	abstract protected void beforeExecuteUpdate(String query_, Object[] parameters_);
	abstract protected void beforeCallProcedure(String query_, Object[] parameters_);
	
	abstract protected void beforeSelectBLOB(String query_, Object[] parameters_);
	abstract protected void beforeInsertBLOB(String query_, InputStream inputStream_);
	abstract protected void beforeUpdateBLOB(String query_, InputStream inputStream_, Object[] parameters_);
	
	abstract protected void beforeSelectCLOB(String query_, Object[] parameters_);
	abstract protected void beforeInsertCLOB(String query_, Reader reader_);
	abstract protected void beforeUpdateCLOB(String query_, Reader reader_, Object[] parameters_);
}
