package ch.scaille.util.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

class FileSystemLocatorTest {

    public static final String TEST_1 = "test1";
    private Path tempFolder;

    @BeforeEach
    void setUp() throws IOException {
        tempFolder = Files.createTempDirectory(getClass().getSimpleName());
    }

    @AfterEach
    void tearDown() throws IOException {
        try (var tempContent = Files.list(tempFolder)) {
            for (var file : tempContent.toList()) {
                Files.delete(file);
            }
        }
        Files.delete(tempFolder);
    }

    @Test
    void test() throws IOException {
        
        final var otherFile = Files.createTempFile(tempFolder, "test", ".nottxt");
        try {
            final var dao = new FileSystemDao<>(String.class, tempFolder, new StorageDataHandlerRegistry(new TextStorageHandler()));
            final var savedResource = dao.saveOrUpdate(TEST_1, "Test 1 content");
            validateResourceTest1(savedResource);

            Assertions.assertEquals(List.of(TEST_1), dao.list().map(ResourceMetaData::getLocator).toList());
            
            final var loadedResource = dao.loadResource(TEST_1);
            validateResourceTest1(loadedResource);
            
        } finally {
            Files.delete(otherFile);
        }
    }

    private void validateResourceTest1(Resource<String> resource) {
        Assertions.assertNotNull(resource);
        Assertions.assertEquals(TEST_1, resource.getLocator());
        Assertions.assertEquals(tempFolder.resolve("test1.txt").toString(), resource.getStorageLocator());
        Assertions.assertEquals(TextStorageHandler.TEXT_MIMETYPE, resource.getMimeType());
    }

}
