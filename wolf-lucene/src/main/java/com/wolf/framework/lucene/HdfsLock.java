package com.wolf.framework.lucene;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.Lock;

/**
 *
 * @author aladdin
 */
public class HdfsLock extends Lock {

    private final Path path;

    public HdfsLock(Path path) {
        this.path = path;
    }

    @Override
    public boolean obtain() throws IOException {
        return true;
    }

    @Override
    public void release() throws IOException {
    }

    @Override
    public boolean isLocked() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "Lock@" + this.path;
    }
}
