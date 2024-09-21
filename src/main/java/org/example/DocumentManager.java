package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * To implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for store data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>(); //It's not violate demant of save method implementation be uperst?

    /**
     * Implementation of this method should uperst the document to your storage
     * And generate unique id. If it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(generateUniqueId());
            if (document.getCreated() == null) {
                document.setCreated(Instant.now());
            }
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        return storage.values().stream()
                .filter(doc -> matchesRequest(doc, request))
                .collect(Collectors.toList());
    }

    /**
     * Auxially method to check if a document matches the search request
     *
     * @param doc - document from DB to check whether it's match
     * @param request - search request, each field could be null
     * @return boolean if it doc matches request
     */
    private boolean matchesRequest(Document doc, SearchRequest request) {
        boolean matchesTitle = request.getTitlePrefixes() == null || request.getTitlePrefixes().stream()
                .anyMatch(prefix -> doc.getTitle().startsWith(prefix));

        boolean matchesContent = request.getContainsContents() == null || request.getContainsContents().stream()
                .anyMatch(content -> doc.getContent().contains(content));

        boolean matchesAuthor = request.getAuthorIds() == null || request.getAuthorIds().contains(doc.getAuthor().getId());

        boolean matchesCreatedFrom = request.getCreatedFrom() == null || !doc.getCreated().isBefore(request.getCreatedFrom());

        boolean matchesCreatedTo = request.getCreatedTo() == null || !doc.getCreated().isAfter(request.getCreatedTo());

        return matchesTitle && matchesContent && matchesAuthor && matchesCreatedFrom && matchesCreatedTo;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(storage.get(id));
    }

    // Generates a unique ID for a document
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}