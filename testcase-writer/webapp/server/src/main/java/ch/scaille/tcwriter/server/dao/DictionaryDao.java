package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;

public interface DictionaryDao {

	List<Metadata> listAll();

	TestDictionary load(String dictionaryName);
	
}