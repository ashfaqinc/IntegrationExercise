package org.integration.persistance;

import java.util.List;

public interface IPersist {
	public void insert(String name) throws Exception;
	public void delete(String name) throws Exception;
	public List<String> getUsers() throws Exception;
}
