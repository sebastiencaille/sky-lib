package ch.scaille.tcwriter.server.webapi.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.services.TestCaseService;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestCaseMapper;

public class TestCaseController extends TestcaseApiController {

	private final DictionaryDao dictionaryDao;

	private final TestCaseDao testCaseDao;

	private final ContextService contextService;

	private final TestCaseService tcService;

	public TestCaseController(ContextService contextService, DictionaryDao dictionaryDao, TestCaseDao testcaseDao,
			NativeWebRequest request, TestCaseService tcService) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
		this.testCaseDao = testcaseDao;
		this.tcService = tcService;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		var currentDictionary = contextService.get().getDictionary();
		if (currentDictionary == null) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		return new ResponseEntity<>(testCaseDao.listAll(dictionaryDao.load(currentDictionary)).stream()
				.map(MetadataMapper.MAPPER::convert).toList(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TestCase> current() {
		var currentDictionaryId = contextService.get().getDictionary();
		var currentTCId = contextService.get().getTestCase();
		if (currentDictionaryId == null || currentTCId == null) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		var loadedTC = testCaseDao.load(contextService.get().getTestCase(),
				dictionaryDao.load(contextService.get().getDictionary()));
		var dto = TestCaseMapper.MAPPER.convert(loadedTC);
		var humanReadables = tcService.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}
