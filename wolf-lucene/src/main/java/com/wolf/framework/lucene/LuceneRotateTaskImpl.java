package com.wolf.framework.lucene;

import com.wolf.framework.task.Task;

/**
 *
 * @author aladdin
 */
public class LuceneRotateTaskImpl extends Task {

    private final HdfsLuceneImpl hdfsLuceneImpl;

    public LuceneRotateTaskImpl(HdfsLuceneImpl hdfsLuceneImpl) {
        this.hdfsLuceneImpl = hdfsLuceneImpl;
    }

    @Override
    public void doWhenRejected() {
        this.hdfsLuceneImpl.releaseRotateLock();
    }

    @Override
    protected void execute() {
        this.hdfsLuceneImpl.rotate();
    }
}
