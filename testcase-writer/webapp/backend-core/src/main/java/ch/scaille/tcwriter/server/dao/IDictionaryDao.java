package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;

public interface IDictionaryDao {

	List<Metadata> listAll();

	Metadata loadMetadata(String dictionaryId);

	TestDictionary load(String dictionaryId);
	
}
