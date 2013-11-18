package com.wolf.framework.lucene;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;

/**
 *
 * @author aladdin
 */
public class DeleteFilter extends Filter {

    private final DeleteFilterCache cache;
    private final Set<String> deleteIdSet = new ConcurrentSkipListSet<String>();
    private volatile String lastDeleteId = "";

    public DeleteFilter(DeleteFilterCache cache) {
        this.cache = cache;
    }

    public void addDeleteId(String deleteId) {
        this.lastDeleteId = deleteId;
        this.deleteIdSet.add(deleteId);
    }
    
    public void removeAll(Collection<String> c) {
        this.deleteIdSet.removeAll(c);
    }
    
    public Set<String> copyToNewDeleteSet() {
        Set<String> secondDeleteSet = new HashSet<String>(this.deleteIdSet.size(), 1);
        secondDeleteSet.addAll(this.deleteIdSet);
        return secondDeleteSet;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        String readerKey = reader.getCoreCacheKey().toString();
        FixedBitSet result = this.cache.getCache(readerKey, lastDeleteId);
        if (result == null) {
            //缓存不存在
            int maxDoc = reader.maxDoc();
            result = new FixedBitSet(maxDoc);
            Document doc;
            String id;
            for (int index = 0; index < maxDoc; index++) {
                doc = reader.document(index);
                id = doc.get(HdfsLucene.DOCUMENT_ID);
                if (this.deleteIdSet.contains(id) == false) {
                    result.set(index);
                }
            }
            this.cache.putCache(readerKey, lastDeleteId, result);
        }
        return result;
    }
}
