package com.wolf.framework.lucene;

import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;

/**
 *
 * @author aladdin
 */
public class HdfsDirectory extends Directory {

    private final FileSystem dfs;
    private final Path dir;
    private final int ioFileBufferSize;
    private final Lock lock;

    public HdfsDirectory(FileSystem dfs, Path dir) throws IOException {
        this.dfs = dfs;
        this.dir = dir;
        this.ioFileBufferSize = this.dfs.getConf().getInt("io.file.buffer.size", 4096);
        boolean flag = this.dfs.exists(dir);
        if (!flag) {
            this.dfs.mkdirs(dir);
        }
        this.lock = new HdfsLock(this.dir);
    }

    private Path[] getPaths(FileStatus[] stats) {
        Path[] result;
        if (stats.length == 0) {
            result = new Path[0];
        } else {
            result = new Path[stats.length];
            for (int index = 0; index < stats.length; index++) {
                result[index] = stats[index].getPath();
            }
        }

        return result;
    }

    @Override
    public String[] listAll() throws IOException {
        this.ensureOpen();
        String[] result = null;
        FileStatus[] fstats = this.dfs.listStatus(dir);
        Path[] paths = this.getPaths(fstats);
        if (paths != null) {
            result = new String[paths.length];
            for (int i = 0; i < paths.length; i++) {
                result[i] = paths[i].getName();
            }
        }
        return result;
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        this.ensureOpen();
        return this.dfs.exists(new Path(this.dir, name));
    }

    @Override
    public void deleteFile(String name) throws IOException {
        this.ensureOpen();
        Path path = new Path(this.dir, name);
        this.dfs.delete(path, false);
    }

    @Override
    public long fileLength(String name) throws IOException {
        this.ensureOpen();
        Path path = new Path(this.dir, name);
        return this.dfs.getFileStatus(path).getLen();
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        Path path = new Path(this.dir, name);
        if (!this.dfs.exists(path) && !this.dfs.mkdirs(path)) {
            throw new IOException("Cannot create path: " + path);
        }
        if (this.dfs.exists(path) && !this.dfs.delete(path, false)) {
            throw new IOException("Cannot overwrite: " + path);
        }
        return new DfsIndexOutput(this.dfs, path, this.ioFileBufferSize);
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        Path path = new Path(this.dir, name);
        return new DfsIndexInput(this.dfs, path, this.ioFileBufferSize);
    }

    @Override
    public synchronized void close() throws IOException {
        this.isOpen = false;
        this.dfs.close();
    }

    @Override
    public Lock makeLock(final String name) {
        return this.lock;
    }
}
