package ch.scaille.tcwriter.server.webapi.controllers;

import static ch.scaille.tcwriter.server.webapi.controllers.ControllerHelper.validateDictionarySet;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.DictionaryApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestDictionary;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestDictionaryMapper;

public class DictionaryController extends DictionaryApiController {

	private final IDictionaryDao dictionaryDao;

	private final ContextService contextService;

	public DictionaryController(ContextService contextService, IDictionaryDao dictionaryDao, NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return ResponseEntity.ok(dictionaryDao.listAll().stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Override
	public ResponseEntity<TestDictionary> current() {
		final var dictionaryName = validateDictionarySet(contextService.get().getDictionary());
		final var dictionary = validateDictionarySet(dictionaryDao.load(dictionaryName));
		return ResponseEntity.ok(TestDictionaryMapper.MAPPER.convert(dictionary));
	}
}
