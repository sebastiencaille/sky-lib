package ch.scaille.tcwriter.server.webapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.scaille.tcwriter.model.testapi.Metadata;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.services.AbstractContextualService;
import ch.scaille.tcwriter.server.services.ContextService;

@RequestMapping("/dictionaries")
public class DictionariesController extends AbstractContextualService {

	private final DictionaryDao dictionaryDao;

	public DictionariesController(ContextService contextService, DictionaryDao dictionaryDao) {
		super(contextService);
		this.dictionaryDao = dictionaryDao;
	}

	@GetMapping(path = "", produces = "application/json")
	@ResponseBody
	public List<Metadata> list() {
		return dictionaryDao.listDictionaries();
	}

	@GetMapping(path="/current", produces = "application/json")
	@ResponseBody
	public TestDictionary current() {
		return dictionaryDao.load(contextService.get().getDictionary());
	}
}
