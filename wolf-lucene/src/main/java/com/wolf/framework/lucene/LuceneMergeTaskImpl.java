package com.wolf.framework.lucene;

import com.wolf.framework.task.Task;

/**
 *
 * @author aladdin
 */
public class LuceneMergeTaskImpl implements Task {

    private final HdfsLuceneImpl hdfsLuceneImpl;

    public LuceneMergeTaskImpl(HdfsLuceneImpl hdfsLuceneImpl) {
        this.hdfsLuceneImpl = hdfsLuceneImpl;
    }

    @Override
    public void doWhenRejected() {
        this.hdfsLuceneImpl.releaseMergeLock();
    }

    @Override
    public void run() {
        this.hdfsLuceneImpl.merge();
    }
}
