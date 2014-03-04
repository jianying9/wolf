package com.wolf.framework.lucene;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.BufferedIndexOutput;

/**
 *
 * @author aladdin
 */
public class DfsIndexOutput extends BufferedIndexOutput {

    private final FileSystem dfs;
    private final FSDataOutputStream out;
    private final RandomAccessFile local;
    private final File localFile;
    private final int ioFileBufferSize;

    public DfsIndexOutput(FileSystem dfs, Path path, int ioFileBufferSize) throws IOException {
        this.dfs = dfs;
        String randStr = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        localFile = File.createTempFile("index_" + randStr, ".tmp");
        localFile.deleteOnExit();
        local = new RandomAccessFile(localFile, "rw");
        this.out = this.dfs.create(path);
        this.ioFileBufferSize = ioFileBufferSize;
    }

    @Override
    protected void flushBuffer(byte[] b, int offset, int len) throws IOException {
        local.write(b, offset, len);
    }

    @Override
    public long length() throws IOException {
        return this.local.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        super.seek(pos);
        local.seek(pos);
    }

    @Override
    public void close() throws IOException {
        super.close();
        // transfer to dfs from local
        byte[] buffer = new byte[this.ioFileBufferSize];
        local.seek(0);
        int read;
        while ((read = local.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
        local.close();
    }
}
