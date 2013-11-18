package com.wolf.framework.lucene;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.BufferedIndexInput;

/**
 *
 * @author aladdin
 */
public class DfsIndexInput extends BufferedIndexInput {

    private final FileSystem dfs;
    private final Descriptor descriptor;
    private final long length;
    private boolean isClone = false;

    public DfsIndexInput(FileSystem dfs, Path path, int ioFileBufferSize) throws IOException {
        super(path.getName(), ioFileBufferSize);
        this.dfs = dfs;
        final FSDataInputStream in = this.dfs.open(path);
        this.descriptor = new Descriptor(in);
        this.length = this.dfs.getFileStatus(path).getLen();
    }

    @Override
    protected void readInternal(byte[] b, int offset, int length) throws IOException {
        FSDataInputStream in = this.descriptor.getIn();
        synchronized (descriptor) {
            long position = getFilePointer();
            if (position != descriptor.getPosition()) {
                in.seek(position);
                descriptor.setPosition(position);
            }
            int total = 0;
            do {
                int index = in.read(b, offset + total, length - total);
                if (index == -1) {
                    throw new IOException("read past EOF");
                }
                descriptor.addPosition(index);
                total += index;
            } while (total < length);
        }
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
    }

    @Override
    public void close() throws IOException {
        if (!this.isClone) {
            this.descriptor.getIn().close();
        }
    }

    @Override
    public long length() {
        return this.length;
    }

    @Override
    public DfsIndexInput clone() {
        DfsIndexInput clone = (DfsIndexInput) super.clone();
        clone.isClone = true;
        return clone;
    }
}
