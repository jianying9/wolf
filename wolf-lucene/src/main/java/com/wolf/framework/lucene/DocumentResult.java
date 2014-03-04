package com.wolf.framework.lucene;

import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author aladdin
 */
public interface DocumentResult {

    public int getTotal();
    
    public int getPageSize();

    public String getNextPageIndex();

    public List<Document> getResultList();
    
    public boolean isEmpty();
}
