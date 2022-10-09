package ch.scaille.tcwriter.server.webapi.controllers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.contollers.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestCaseMapper;

public class TestCaseController extends TestcaseApiController {

	private final DictionaryDao dictionaryDao;

	private final TestCaseDao testCaseDao;

	private final ContextService contextService;

	public TestCaseController(ContextService contextService, DictionaryDao dictionaryDao, TestCaseDao testcaseDao,
			NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
		this.testCaseDao = testcaseDao;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		var currentDictionary = contextService.get().getDictionary();
		if (currentDictionary == null) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		return new ResponseEntity<>(testCaseDao.listAll(dictionaryDao.load(currentDictionary)).stream()
				.map(MetadataMapper.MAPPER::convert).collect(Collectors.toList()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TestCase> current() {
		var currentDictionary = contextService.get().getDictionary();
		var currentTC = contextService.get().getTestCase();
		if (currentDictionary == null || currentTC == null) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		return new ResponseEntity<>(TestCaseMapper.MAPPER.convert(testCaseDao.load(contextService.get().getTestCase(),
				dictionaryDao.load(contextService.get().getDictionary()))), HttpStatus.OK);
	}
}
