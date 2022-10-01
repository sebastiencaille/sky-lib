package ch.scaille.tcwriter.server.webapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.DictionaryApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestDictionary;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestDictionaryMapper;

public class DictionariesController extends DictionaryApiController {

	private final DictionaryDao dictionaryDao;

	private final ContextService contextService;

	public DictionariesController(ContextService contextService, DictionaryDao dictionaryDao,
			NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return new ResponseEntity<>(dictionaryDao.listDictionaries().stream().map(MetadataMapper.MAPPER::convert)
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TestDictionary> current() {
		return new ResponseEntity<>(
				TestDictionaryMapper.MAPPER.convert(dictionaryDao.load(contextService.get().getDictionary())),
				HttpStatus.OK);
	}
}
