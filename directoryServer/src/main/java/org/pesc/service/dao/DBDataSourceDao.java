package org.pesc.service.dao;

import java.util.List;

public interface DBDataSourceDao<T> {
	public T byId(Integer id);
	
	public List<T> all();
	
	public T save(T saveItem);
	
	public T remove(T removeItem);
}
