package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import org.jspecify.annotations.Nullable;

public interface IDictionaryDao {

    List<Metadata> listAll(@Nullable Metadata testMetadata);

    @Nullable
    Metadata loadMetadata(String dictionaryId);

    @Nullable
	TestDictionary load(String dictionaryId);
	
}
