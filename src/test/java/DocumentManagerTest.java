import org.example.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    public void testSaveNewDocumentGeneratesIdAndStoresIt() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test content")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("Test Document", savedDocument.getTitle());
    }

    @Test
    public void testFindByIdReturnsSavedDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test content")
                .author(author)
                .build();

        // Save the document
        DocumentManager.Document savedDocument = documentManager.save(document);

        // Find the document by its generated ID
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument.getId(), foundDocument.get().getId());
        assertEquals("Test Document", foundDocument.get().getTitle());
    }

    @Test
    public void testSearchFindsDocumentByTitlePrefix() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Test Document 1")
                .content("This is content 1")
                .author(author)
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Example Document 2")
                .content("This is content 2")
                .author(author)
                .build();

        // Save the documents
        documentManager.save(document1);
        documentManager.save(document2);

        // Search by title prefix "Test"
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Arrays.asList("Test"))
                .build();

        List<DocumentManager.Document> foundDocuments = documentManager.search(searchRequest);

        assertEquals(1, foundDocuments.size());
        assertEquals("Test Document 1", foundDocuments.get(0).getTitle());
    }

    @Test
    public void testSearchFindsDocumentByAuthorId() {
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Jane Smith")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Document by Author 1")
                .content("Content by Author 1")
                .author(author1)
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Document by Author 2")
                .content("Content by Author 2")
                .author(author2)
                .build();

        // Save the documents
        documentManager.save(document1);
        documentManager.save(document2);

        // Search by authorId "author1"
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .authorIds(Arrays.asList("author1"))
                .build();

        List<DocumentManager.Document> foundDocuments = documentManager.search(searchRequest);

        assertEquals(1, foundDocuments.size());
        assertEquals("Document by Author 1", foundDocuments.get(0).getTitle());
    }
}
