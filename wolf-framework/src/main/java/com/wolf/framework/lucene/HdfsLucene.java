package com.wolf.framework.lucene;

import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

/**
 *
 * @author aladdin
 */
public interface HdfsLucene {
    
    public String DOCUMENT_ID = "L_ID";
    
    public DocumentResult searchAfter(String pageIndex, Query query, int pageSize);
    
    public Document getByKey(String keyValue);
    
    public List<Document> getByKeys(List<String> keyValueList);

    public void addDocument(Document doc);

    public void addDocument(List<Document> docList);

    public void updateDocument(Document doc);

    public void updateDocument(List<Document> docList);

    public void deleteDocument(String keyValue);

    public void deleteDocument(List<String> keyValues);
    
    public void tryToRotate();
    
    public void tryToMerge();
}
