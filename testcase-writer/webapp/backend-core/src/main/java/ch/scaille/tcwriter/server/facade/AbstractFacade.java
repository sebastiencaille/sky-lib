package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;

public class AbstractFacade {

	protected final IDictionaryDao dictionaryDao;

	protected AbstractFacade(IDictionaryDao dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}

	protected TestDictionary loadDictionary(String dictionaryId) {
		return ValidationHelper.dictionaryFound(dictionaryId, dictionaryDao.load(dictionaryId));
	}

	protected Metadata loadDictionaryMetadata(String dictionaryId) {
		return ValidationHelper.dictionaryFound(dictionaryId, dictionaryDao.loadMetadata(dictionaryId));
	}
}
