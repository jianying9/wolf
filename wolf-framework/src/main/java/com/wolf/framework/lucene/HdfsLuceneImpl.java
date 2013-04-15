package com.wolf.framework.lucene;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.task.Task;
import com.wolf.framework.task.TaskExecutor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public class HdfsLuceneImpl implements HdfsLucene {

    //
    private final String keyName;
    //合并锁
    private volatile boolean mergeLock = false;
    //轮转锁
    private volatile boolean rotateLock = false;
    //hdfs写入配置对象
    private final IndexWriterConfig iwc;
    //任务处理对象
    private final TaskExecutor taskExecutor;
    //hdfs文件管理对象
    private final FileSystem fileSystem;
    //ip
    private final String ip;
    //根路径
    private final Path rootPath;
    //主索引
    private final Path mainPath;
    private volatile IndexReader mainIndexReader;
    //当前内存索引
    private final IndexWriterConfig ramIwc;
    private volatile RAMDirectory RAMDirectory;
    private volatile IndexWriter ramIndexWriter;
    private volatile IndexReader ramIndexReader;
    //临时内存索引
    private volatile IndexReader tempRamIndexReader;
    //内存索引文件备份
    private volatile Path ramPath;
    //删除索引文档id
    private final DeleteFilter deleteFilter;
    private volatile Path deleteIdPath;
    //其他只读索引
    private final Map<String, IndexReader> readOnlyIndexReaderMap = new HashMap<String, IndexReader>(8, 1);
    //组合索引读取对象
    private volatile MultiReader multiReader;
    //日志对象
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.LUCENE);

    public HdfsLuceneImpl(String keyName, Path rootPath, FileSystem fileSystem, IndexWriterConfig iwc, IndexWriterConfig ramIwc, TaskExecutor taskExecutor, String ip, DeleteFilterCache deleteFilterCache) throws IOException {
        this.keyName = keyName;
        this.fileSystem = fileSystem;
        this.rootPath = rootPath;
        this.iwc = iwc;
        this.ramIwc = ramIwc;
        this.taskExecutor = taskExecutor;
        this.ip = ip;
        //判断rootPath是否存在，否则创建
        if (!this.fileSystem.exists(this.rootPath)) {
            this.fileSystem.mkdirs(this.rootPath);
        }
        Path testPath;
        this.logger.info("load {} main index start...", this.rootPath.getName());
        //构造主索引目录
        this.mainPath = new Path(this.rootPath, "main");
        //判断mainPath是否存在，否则创建
        if (this.fileSystem.exists(this.mainPath)) {
            //主索引目录存在
            //判断是否有可用索引
            testPath = new Path(this.mainPath, IndexFileNames.SEGMENTS_GEN);
            if (this.fileSystem.exists(testPath)) {
                //如果段文件存在，则初始化主索引reader
                HdfsDirectory mainDir = new HdfsDirectory(this.fileSystem, this.mainPath);
                this.mainIndexReader = DirectoryReader.open(mainDir);
            } else {
                this.mainIndexReader = null;
            }
        } else {
            //主索引目录不存在，则创建
            this.fileSystem.mkdirs(this.mainPath);
            this.mainIndexReader = null;
        }
        this.logger.info("load {} main index success", this.rootPath.getName());
        //初始化临时索引目录对象
        this.logger.info("load read only index start...");
        Path tempPath;
        String readOnlyPathName;
        String mainPathName = this.mainPath.getName();
        HdfsDirectory tempDir;
        IndexReader readOnlyIndexReader;
        FileStatus fileStatus;
        FileStatus[] fstats = this.fileSystem.listStatus(this.rootPath);
        for (int index = 0; index < fstats.length; index++) {
            fileStatus = fstats[index];
            if (fileStatus.isDir()) {
                //是文件目录
                tempPath = fileStatus.getPath();
                readOnlyPathName = tempPath.getName();
                if (!readOnlyPathName.equals(mainPathName)) {
                    //不为主索引目录
                    testPath = new Path(tempPath, IndexFileNames.SEGMENTS_GEN);
                    if (this.fileSystem.exists(testPath)) {
                        tempDir = new HdfsDirectory(this.fileSystem, tempPath);
                        readOnlyIndexReader = DirectoryReader.open(tempDir);
                        this.readOnlyIndexReaderMap.put(readOnlyPathName, readOnlyIndexReader);
                    } else {
                        this.fileSystem.delete(tempPath, true);
                    }
                }
            }
        }
        this.logger.info("load read only index success");
        this.RAMDirectory = new RAMDirectory();
        this.ramIndexWriter = new IndexWriter(RAMDirectory, this.ramIwc);
        this.ramIndexWriter.commit();
        this.ramIndexReader = null;
        //创建内存索引文件备份对象
        String tempPathName;
        String ramPrefix = this.ip.concat("-RAM-");
        for (int index = 0; index < fstats.length; index++) {
            fileStatus = fstats[index];
            if (fileStatus.isDir() == false) {
                //是文件
                tempPath = fileStatus.getPath();
                tempPathName = tempPath.getName();
                if (tempPathName.indexOf(ramPrefix) == 0) {
                    this.ramPath = tempPath;
                    break;
                }
            }
        }
        if (this.ramPath == null) {
            String ramTempDir = ramPrefix.concat(Long.toString(System.currentTimeMillis()));
            this.ramPath = new Path(this.rootPath, ramTempDir);
            this.fileSystem.createNewFile(ramPath);
        }
        //创建删除过滤对象
        this.deleteFilter = new DeleteFilter(deleteFilterCache);
        //创建已删除索引缓存
        String deletePrefix = this.ip.concat("-DELETE-");
        for (int index = 0; index < fstats.length; index++) {
            fileStatus = fstats[index];
            if (fileStatus.isDir() == false) {
                //是文件
                tempPath = fileStatus.getPath();
                tempPathName = tempPath.getName();
                if (tempPathName.indexOf(deletePrefix) == 0) {
                    this.deleteIdPath = tempPath;
                    break;
                }
            }
        }
        if (this.deleteIdPath == null) {
            String deleteTempDir = deletePrefix.concat(Long.toString(System.currentTimeMillis()));
            this.deleteIdPath = new Path(this.rootPath, deleteTempDir);
            this.fileSystem.createNewFile(deleteIdPath);
        } else {
            //加载被删除的文档id
            FSDataInputStream deleteInputStream = this.fileSystem.open(this.deleteIdPath);
            BufferedReader deleteBufferedReader = new BufferedReader(new InputStreamReader(deleteInputStream));
            String line = deleteBufferedReader.readLine();
            while (line != null) {
                this.deleteFilter.addDeleteId(line);
                line = deleteBufferedReader.readLine();
            }
        }
        //创建组合索引对象
        this.logger.info("create multiReader...");
        this.buildMultiReader();
    }

    /**
     * 重建已有的reader
     */
    private void buildMultiReader() {
        List<IndexReader> indexReaderList = new ArrayList<IndexReader>(this.readOnlyIndexReaderMap.size() + 2);
        if (this.mainIndexReader != null) {
            indexReaderList.add(this.mainIndexReader);
        }
        if (this.ramIndexReader != null) {
            indexReaderList.add(this.ramIndexReader);
        }
        if (this.tempRamIndexReader != null) {
            indexReaderList.add(this.tempRamIndexReader);
        }
        if (!this.readOnlyIndexReaderMap.isEmpty()) {
            Collection<IndexReader> indexReaderCollection = this.readOnlyIndexReaderMap.values();
            indexReaderList.addAll(indexReaderCollection);
        }
        IndexReader[] indexReaders = indexReaderList.toArray(new IndexReader[indexReaderList.size()]);
        this.multiReader = new MultiReader(indexReaders, false);
    }

    void releaseRotateLock() {
        this.rotateLock = false;
    }

    void rotate() {
        this.logger.debug("lucene rotate {} index...", this.rootPath.getName());
        //缓存已有的内存索引对象
        RAMDirectory secondRAMDirectory = this.RAMDirectory;
        IndexWriter secondRamIndexWriter = this.ramIndexWriter;
        IndexReader secondIndexReader = this.ramIndexReader;
        Path secondRamTempPath = this.ramPath;
        try {
            //创建新的内存索引文件备份对象
            StringBuilder ramTempDirBuilder = new StringBuilder(32);
            ramTempDirBuilder.append(this.ip).append("-RAM-").append(System.currentTimeMillis());
            String ramTempDir = ramTempDirBuilder.toString();
            this.ramPath = new Path(this.rootPath, ramTempDir);
            this.fileSystem.createNewFile(this.ramPath);
            //将当前内存索引读取对象所以放到临时内存索引读取对象
            this.tempRamIndexReader = this.ramIndexReader;
            //创建新的内存索引对象
            this.RAMDirectory = new RAMDirectory();
            this.ramIndexWriter = new IndexWriter(RAMDirectory, this.ramIwc);
            this.ramIndexWriter.commit();
            //将当前内存索引置空
            this.ramIndexReader = null;
            //重新组合索引
            this.buildMultiReader();
            //创建只读索引目录
            StringBuilder readOnlyDirBuilder = new StringBuilder(32);
            readOnlyDirBuilder.append(this.ip).append('-').append(System.currentTimeMillis());
            final String readOnlyDir = readOnlyDirBuilder.toString();
            final Path readOnlyPath = new Path(this.rootPath, readOnlyDir);
            this.fileSystem.mkdirs(readOnlyPath);
            //保存第二内存索引内容全部提交
            secondRamIndexWriter.commit();
            //将第二内存索引写入只读索引目录
            HdfsDirectory readOnlyDirectory = new HdfsDirectory(this.fileSystem, readOnlyPath);
            IndexWriter readOnlyWriter = new IndexWriter(readOnlyDirectory, iwc);
            readOnlyWriter.addIndexes(secondRAMDirectory);
            readOnlyWriter.commit();
            readOnlyWriter.close();
            //读取只读所以，放入只读索引集合
            final IndexReader readOnlyIndexReader = DirectoryReader.open(readOnlyDirectory);
            this.readOnlyIndexReaderMap.put(readOnlyPath.getName(), readOnlyIndexReader);
            //将第二内存索引读取对象置空
            this.tempRamIndexReader = null;
            //重新组合索引
            this.buildMultiReader();
            //释放第二内存索引相关资源
            if (secondIndexReader != null) {
                secondIndexReader.close();
            }
            secondRamIndexWriter.close();
            secondRAMDirectory.close();
            this.fileSystem.delete(secondRamTempPath, true);
        } catch (IOException ex) {
            this.logger.error("lucene rotate {} index error...", this.rootPath.getName(), ex);
        }
        //释放轮转锁
        this.releaseRotateLock();
    }

    @Override
    public void tryToRotate() {
        if (!this.rotateLock) {
            this.rotateLock = true;
            Task task = new LuceneRotateTaskImpl(this);
            this.taskExecutor.submet(task);
        }
    }

    @Override
    public void tryToMerge() {
        if (!this.mergeLock) {
            this.mergeLock = true;
            Task task = new LuceneMergeTaskImpl(this);
            this.taskExecutor.submet(task);
        }
    }

    void releaseMergeLock() {
        this.mergeLock = false;
    }

    void merge() {
        try {
            //获取当前索引目录，查看是否有临时只读索引目录存在
            FileStatus[] fstats = this.fileSystem.listStatus(this.rootPath);
            if (fstats.length > 1) {
                //获取临时只读索引目录
                Path readOnlyPath;
                String mainPathName = this.mainPath.getName();
                String readOnlyPathName;
                List<Path> tempPathList = new ArrayList<Path>(fstats.length - 1);
                Path testPath;
                FileStatus fileStatus;
                for (int index = 0; index < fstats.length; index++) {
                    fileStatus = fstats[index];
                    if (fileStatus.isDir()) {
                        readOnlyPath = fileStatus.getPath();
                        readOnlyPathName = readOnlyPath.getName();
                        if (!readOnlyPathName.equals(mainPathName)) {
                            //目录名称不是主目录并且不是当前写入目录，则为临时目录
                            //判断是否存在索引
                            testPath = new Path(readOnlyPath, IndexFileNames.SEGMENTS_GEN);
                            if (this.fileSystem.exists(testPath)) {
                                tempPathList.add(readOnlyPath);
                            }
                        }
                    }
                }
                //如果存在临时只读目录，则开始合并
                if (!tempPathList.isEmpty()) {
                    List<Directory> tempDirList = new ArrayList<Directory>(tempPathList.size());
                    HdfsDirectory tempDir;
                    for (Path path : tempPathList) {
                        tempDir = new HdfsDirectory(this.fileSystem, path);
                        tempDirList.add(tempDir);
                    }
                    Directory[] tempDirs = tempDirList.toArray(new Directory[tempDirList.size()]);
                    //写入主索引
                    HdfsDirectory mainDir = new HdfsDirectory(this.fileSystem, this.mainPath);
                    IndexWriter mainWriter = new IndexWriter(mainDir, iwc);
                    mainWriter.addIndexes(tempDirs);
                    mainWriter.commit();
                    //切换删除缓存目录
                    Path tempDeleteIdPath = this.deleteIdPath;
                    StringBuilder deleteTempDirBuilder = new StringBuilder(32);
                    deleteTempDirBuilder.append(this.ip).append("-DELETE-").append(System.currentTimeMillis());
                    String deleteTempDir = deleteTempDirBuilder.toString();
                    this.deleteIdPath = new Path(this.rootPath, deleteTempDir);
                    //根据文档id删除被标记为删除的文档
                    Term term;
                    Set<String> secondDeleteSet = this.deleteFilter.copyToNewDeleteSet();
                    List<Term> deleteTermList = new ArrayList<Term>(secondDeleteSet.size());
                    for (String deleteId : secondDeleteSet) {
                        term = new Term(HdfsLucene.DOCUMENT_ID, deleteId);
                        deleteTermList.add(term);
                    }
                    if (deleteTermList.isEmpty() == false) {
                        Term[] terms = deleteTermList.toArray(new Term[deleteTermList.size()]);
                        mainWriter.deleteDocuments(terms);
                        mainWriter.commit();
                    }
                    mainWriter.close();
                    //删除已经被合并的deleteId
                    this.fileSystem.delete(tempDeleteIdPath, true);
                    this.deleteFilter.removeAll(secondDeleteSet);
                    //缓存已有主索引reader和已有的只读reader
                    IndexReader oldMainReader = this.mainIndexReader;
                    Map<String, IndexReader> oldReadOnlyIndexReaderMap = new HashMap<String, IndexReader>(this.readOnlyIndexReaderMap.size(), 1);
                    oldReadOnlyIndexReaderMap.putAll(this.readOnlyIndexReaderMap);
                    //构造新的主索引reader
                    this.mainIndexReader = DirectoryReader.open(mainDir);
                    //清空只读索引
                    this.readOnlyIndexReaderMap.clear();
                    //重建组合索引
                    this.buildMultiReader();
                    //关闭旧的主索引reader
                    if (oldMainReader != null) {
                        oldMainReader.close();
                    }
                    //关闭旧的只读索引
                    for (IndexReader indexReader : oldReadOnlyIndexReaderMap.values()) {
                        indexReader.close();
                    }
                    //删除临时只读目录
                    for (Path path : tempPathList) {
                        this.fileSystem.delete(path, true);
                    }
                }
            }
        } catch (IOException ex) {
            this.logger.error("lucene merge {} index error...", this.rootPath.getName(), ex);
        }
        this.releaseMergeLock();
    }

    private void reopenIndexReaderWhenOperate() throws IOException {
        //判断如果内存文件大小达到8M，则触发轮转索引
        long size = this.RAMDirectory.sizeInBytes();
        if (size >= 8388608) {
            this.tryToRotate();
        }
        //重新读取当前写入索引目录
        IndexReader oldRamIndexReader = this.ramIndexReader;
        this.ramIndexReader = DirectoryReader.open(this.ramIndexWriter, false);
        //重建组合索引对象
        this.buildMultiReader();
        //关闭旧的写入目录读取索引
        if (oldRamIndexReader != null) {
            oldRamIndexReader.close();
            
        }
    }

    @Override
    public void addDocument(Document doc) {
        try {
            //生成索引id，用于标识文档的唯一性
            String id = UUID.randomUUID().toString();
            Field idField = new StringField(HdfsLucene.DOCUMENT_ID, id, Field.Store.YES);
            doc.add(idField);
            //写入内存
            this.ramIndexWriter.addDocument(doc);
            //刷新内存，重新组合索引
            this.reopenIndexReaderWhenOperate();
            //将当前文档key值写入当前临时缓存文件
            String keyValue = doc.get(this.keyName);
            FSDataOutputStream outputStream = this.fileSystem.create(this.ramPath);
            outputStream.writeBytes(keyValue);
            outputStream.writeBytes("\r");
            outputStream.close();
        } catch (IOException ex) {
            this.logger.error("{} directory addDocument error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addDocument(List<Document> docList) {
        String id;
        Field idField;
        String keyValue;
        List<String> keyValueList = new ArrayList<String>(docList.size());
        try {
            //写入内存
            for (Document document : docList) {
                //生成索引id，用于标识文档的唯一性
                id = UUID.randomUUID().toString();
                idField = new StringField(HdfsLucene.DOCUMENT_ID, id, Field.Store.YES);
                document.add(idField);
                //写入内存
                this.ramIndexWriter.addDocument(document);
                //将当前文档key值写入当前临时缓存文件
                keyValue = document.get(this.keyName);
                keyValueList.add(keyValue);
            }
            //刷新内存，重新组合索引
            this.reopenIndexReaderWhenOperate();
            //写如缓存
            FSDataOutputStream outputStream = this.fileSystem.create(this.ramPath);
            for (String key : keyValueList) {
                outputStream.writeBytes(key);
                outputStream.writeBytes("\r");
            }
            outputStream.close();
        } catch (IOException ex) {
            this.logger.error("{} directory batch addDocument error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateDocument(Document doc) {
        //获取文档的对象
        String keyValue = doc.get(this.keyName);
        Term term = new Term(this.keyName, keyValue);
        Query query = new TermQuery(term);
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(query, this.deleteFilter, 1);
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                //文档对象存在
                Document oldDoc = searcher.doc(hits[0].doc);
                String deleteId = oldDoc.get(HdfsLucene.DOCUMENT_ID);
                //缓存被删除的document id
                this.deleteFilter.addDeleteId(deleteId);
                //删除原文档id
                oldDoc.removeField(HdfsLucene.DOCUMENT_ID);
                //生成新文档id
                String insertId = UUID.randomUUID().toString();
                Field idField = new StringField(HdfsLucene.DOCUMENT_ID, insertId, Field.Store.YES);
                oldDoc.add(idField);
                //替换旧的field
                Iterator<IndexableField> newIterator = doc.iterator();
                IndexableField field;
                while (newIterator.hasNext()) {
                    field = newIterator.next();
                    oldDoc.removeField(field.name());
                    oldDoc.add(field);
                }
                //写入内存
                this.ramIndexWriter.addDocument(oldDoc);
                //刷新内存，重新组合索引
                this.reopenIndexReaderWhenOperate();
                //将删除id写入缓存
                FSDataOutputStream deleteOutputStream = this.fileSystem.create(this.deleteIdPath);
                deleteOutputStream.writeBytes(deleteId);
                deleteOutputStream.writeBytes("\r");
                deleteOutputStream.close();
                //将当前文档key值写入当前临时缓存文件
                FSDataOutputStream insertOutputStream = this.fileSystem.create(this.ramPath);
                insertOutputStream.writeBytes(keyValue);
                insertOutputStream.writeBytes("\r");
                insertOutputStream.close();
            }
        } catch (IOException ex) {
            this.logger.error("{} directory updateDocument error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateDocument(List<Document> docList) {
        //获取文档的对象
        Term term;
        String keyValue;
        Query query;
        Map<String, Document> docMap = new HashMap<String, Document>(docList.size(), 1);
        BooleanQuery booleanQuery = new BooleanQuery();
        for (Document doc : docList) {
            keyValue = doc.get(this.keyName);
            docMap.put(keyValue, doc);
            term = new Term(this.keyName, keyValue);
            query = new TermQuery(term);
            booleanQuery.add(query, BooleanClause.Occur.SHOULD);
        }
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(booleanQuery, this.deleteFilter, docList.size());
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                //文档对象存在
                Document oldDoc;
                Document newDoc;
                String deleteId;
                String insertId;
                Field idField;
                Iterator<IndexableField> newIterator;
                IndexableField field;
                //缓存被删除的document id
                List<String> deleteIdList = new ArrayList<String>(hits.length);
                for (int index = 0; index < hits.length; index++) {
                    oldDoc = searcher.doc(hits[index].doc);
                    deleteId = oldDoc.get(HdfsLucene.DOCUMENT_ID);
                    this.deleteFilter.addDeleteId(deleteId);
                }
                //删除原文档id,并生成新的文档
                List<String> keyValueList = new ArrayList<String>(hits.length);
                for (int index = 0; index < hits.length; index++) {
                    oldDoc = searcher.doc(hits[index].doc);
                    oldDoc.removeField(HdfsLucene.DOCUMENT_ID);
                    keyValue = oldDoc.get(this.keyName);
                    keyValueList.add(keyValue);
                    //生成新文档id
                    insertId = UUID.randomUUID().toString();
                    idField = new StringField(HdfsLucene.DOCUMENT_ID, insertId, Field.Store.YES);
                    oldDoc.add(idField);
                    //替换旧的field
                    newDoc = docMap.get(keyValue);
                    newIterator = newDoc.iterator();
                    while (newIterator.hasNext()) {
                        field = newIterator.next();
                        oldDoc.removeField(field.name());
                        oldDoc.add(field);
                    }
                    //写入内存
                    this.ramIndexWriter.addDocument(oldDoc);
                }
                //刷新内存，重新组合索引
                this.reopenIndexReaderWhenOperate();
                //将删除id写入缓存
                FSDataOutputStream deleteOutputStream = this.fileSystem.create(this.deleteIdPath);
                for (String value : deleteIdList) {
                    deleteOutputStream.writeBytes(value);
                    deleteOutputStream.writeBytes("\r");
                }
                deleteOutputStream.close();
                //将当前文档key值写入当前临时缓存文件
                FSDataOutputStream insertOutputStream = this.fileSystem.create(this.ramPath);
                for (String value : keyValueList) {
                    insertOutputStream.writeBytes(value);
                    insertOutputStream.writeBytes("\r");
                }
                insertOutputStream.close();
            }
            //刷新内存，重新组合索引
            this.reopenIndexReaderWhenOperate();
        } catch (IOException ex) {
            this.logger.error("{} directory batch updateDocument error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteDocument(String keyValue) {
        //获取文档的对象
        Term term = new Term(this.keyName, keyValue);
        Query query = new TermQuery(term);
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(query, this.deleteFilter, 1);
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                //文档对象存在
                Document doc = searcher.doc(hits[0].doc);
                String id = doc.get(HdfsLucene.DOCUMENT_ID);
                //缓存被删除的document id
                this.deleteFilter.addDeleteId(id);
                FSDataOutputStream outputStream = this.fileSystem.create(this.deleteIdPath);
                outputStream.writeBytes(id);
                outputStream.writeBytes("\r");
                outputStream.close();
            }
        } catch (IOException ex) {
            this.logger.error("{} directory deleteDocument error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteDocument(List<String> keyValues) {
        //获取文档的对象
        Query query;
        Term term;
        BooleanQuery booleanQuery = new BooleanQuery();
        for (String keyValue : keyValues) {
            term = new Term(this.keyName, keyValue);
            query = new TermQuery(term);
            booleanQuery.add(query, BooleanClause.Occur.SHOULD);
        }
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(booleanQuery, this.deleteFilter, keyValues.size());
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                //文档对象存在
                Document doc;
                String id;
                List<String> deleteIdList = new ArrayList<String>(hits.length);
                for (int index = 0; index < hits.length; index++) {
                    doc = searcher.doc(hits[index].doc);
                    id = doc.get(HdfsLucene.DOCUMENT_ID);
                    //缓存被删除的document id
                    this.deleteFilter.addDeleteId(id);
                    deleteIdList.add(id);
                }
                //写入删除缓存
                FSDataOutputStream outputStream = this.fileSystem.create(this.deleteIdPath);
                for (String deleteId : deleteIdList) {
                    outputStream.writeBytes(deleteId);
                    outputStream.writeBytes("\r");
                }
                outputStream.close();
            }
        } catch (IOException ex) {
            this.logger.error("{} directory batch deleteDocuments error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
    }

    private String ScoreDocToString(ScoreDoc scoreDoc) {
        StringBuilder pageIndexBuilder = new StringBuilder(24);
        String doc = Integer.toString(scoreDoc.doc);
        String score = Float.toString(scoreDoc.score);
        pageIndexBuilder.append(doc).append('_').append(score);
        return pageIndexBuilder.toString();
    }

    private ScoreDoc pageIndexToScoreDoc(String pageIndex) {
        ScoreDoc result = null;
        if (!pageIndex.isEmpty()) {
            String[] parameter = pageIndex.split("_");
            if (parameter.length == 2) {
                int doc = Integer.parseInt(parameter[0]);
                float score = Float.parseFloat(parameter[1]);
                result = new ScoreDoc(doc, score);
            }
        }
        return result;
    }

    @Override
    public DocumentResult searchAfter(String pageIndex, Query query, int pageSize) {
        DocumentResultImpl documentResult = new DocumentResultImpl();
        TopDocs results;
        List<Document> docList;
        ScoreDoc lastScoreDoc = null;
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        ScoreDoc scoreDocAfter = this.pageIndexToScoreDoc(pageIndex);
        try {
            if (scoreDocAfter == null) {
                results = searcher.search(query, this.deleteFilter, pageSize);
            } else {
                results = searcher.searchAfter(scoreDocAfter, query, this.deleteFilter, pageSize);
            }
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length == 0) {
                docList = new ArrayList<Document>(0);
            } else {
                docList = new ArrayList<Document>(hits.length);
                Document doc;
                for (int index = 0; index < hits.length; index++) {
                    doc = searcher.doc(hits[index].doc);
                    docList.add(doc);
                }
                if (results.totalHits > hits.length && hits.length == pageSize) {
                    //分页
                    lastScoreDoc = hits[hits.length - 1];
                }
            }
        } catch (IOException ex) {
            this.logger.error("{} directory search error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
        documentResult.setTotal(results.totalHits);
        documentResult.setResultList(docList);
        documentResult.setPageSize(pageSize);
        if (lastScoreDoc != null) {
            String nextPageIndex = this.ScoreDocToString(lastScoreDoc);
            documentResult.setNextPageIndex(nextPageIndex);
        }
        return documentResult;
    }

    @Override
    public Document getByKey(String keyValue) {
        Document doc = null;
        Term term = new Term(this.keyName, keyValue);
        Query query = new TermQuery(term);
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(query, this.deleteFilter, 1);
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                doc = searcher.doc(hits[0].doc);
            }
            if (hits.length > 1) {
                this.logger.error("{}: has too many documents:keyValue:{}", this.rootPath.getName(), keyValue);
            }
        } catch (IOException ex) {
            this.logger.error("{} directory search error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
        return doc;
    }

    @Override
    public List<Document> getByKeys(List<String> keyValueList) {
        List<Document> docList;
        Document doc;
        Term term;
        Query query;
        BooleanQuery booleanQuery = new BooleanQuery();
        for (String keyValue : keyValueList) {
            term = new Term(this.keyName, keyValue);
            query = new TermQuery(term);
            booleanQuery.add(query, BooleanClause.Occur.SHOULD);
        }
        IndexSearcher searcher = new IndexSearcher(this.multiReader);
        try {
            TopDocs results = searcher.search(booleanQuery, this.deleteFilter, keyValueList.size());
            ScoreDoc[] hits = results.scoreDocs;
            if (hits.length > 0) {
                docList = new ArrayList<Document>(hits.length);
                for (int index = 0; index < hits.length; index++) {
                    doc = searcher.doc(hits[index].doc);
                    docList.add(doc);
                }
            } else {
                docList = new ArrayList<Document>(0);
            }
            if (hits.length > keyValueList.size()) {
                this.logger.error("{}: has too many documents:keyValues:{}", this.rootPath.getName(), keyValueList.toString());
            }
        } catch (IOException ex) {
            this.logger.error("{} directory search error.see log...", this.rootPath.getName());
            throw new RuntimeException(ex);
        }
        return docList;
    }
}
