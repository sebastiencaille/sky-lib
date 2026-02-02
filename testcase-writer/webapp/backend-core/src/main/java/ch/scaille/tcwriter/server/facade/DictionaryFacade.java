package ch.scaille.tcwriter.server.facade;

import java.util.List;

import ch.scaille.tcwriter.server.dao.IDictionaryDao;

public class DictionaryFacade extends AbstractFacade {

	public DictionaryFacade(IDictionaryDao dictionaryDao) {
		super(dictionaryDao);
	}

	public List<ch.scaille.tcwriter.model.Metadata> listAll() {
		return dictionaryDao.listAll(null);
	}
	
	public ch.scaille.tcwriter.model.dictionary.TestDictionary load(String dictionaryId) {
		return loadDictionary(dictionaryId);
	}

}
