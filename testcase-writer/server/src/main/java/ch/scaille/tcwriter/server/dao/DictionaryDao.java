package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.testapi.Metadata;
import ch.scaille.tcwriter.model.testapi.TestDictionary;

public interface DictionaryDao {

	List<Metadata> listDictionaries();

	TestDictionary load(String dictionaryName);
	
}
