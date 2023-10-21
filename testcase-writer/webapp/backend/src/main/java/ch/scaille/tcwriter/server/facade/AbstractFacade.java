package ch.scaille.tcwriter.server.facade;

import static ch.scaille.tcwriter.server.webapi.controllers.exceptions.ValidationHelper.dictionaryFound;

import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;

public class AbstractFacade {

	protected final IDictionaryDao dictionaryDao;

	protected AbstractFacade(IDictionaryDao dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}

	protected TestDictionary loadDictionary(String dictionaryId) {
		return dictionaryFound(dictionaryId, dictionaryDao.load(dictionaryId));
	}
}
