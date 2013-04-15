package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.hbase.HTableHandler;
import com.wolf.framework.task.TaskExecutor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.ehcache.CacheManager;
import org.apache.hadoop.fs.FileSystem;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class EntityDaoContextImpl<T extends Entity> implements EntityDaoContext<T> {

    //缓存管理对象
    private final CacheManager cacheManager;
    private final HTableHandler hTableHandler;
    private final Map<String, String> existClassMap = new HashMap<String, String>(128);
//    private final FileSystem fileSystem;
//    private final String indexRoot = "/lucene";
//    private final Analyzer analyzer;
//    private final IndexWriterConfig iwc;
//    private final IndexWriterConfig ramIwc;
    private final TaskExecutor taskExecutor;
    private final String ip;
    private final DataSource dataSource;
    private final DataHandlerFactory dataHandlerFactory;
    //entity处理类集合
    private final Map<Class<T>, EntityDao<T>> entityDaoMap;
    //
    private final ApplicationContext applicationContext;

    @Override
    public final CacheManager getCacheManager() {
        return cacheManager;
    }
    //sql查询缓存对象
//    private final InquireCache inquireCache;

//    @Override
//    public final InquireCache getInquireCache() {
//        return this.inquireCache;
//    }
    //lucene filter 缓存
//    private final DeleteFilterCache deleteFilterCache;

//    @Override
//    public DeleteFilterCache getDeleteFilterCache() {
//        return deleteFilterCache;
//    }

    @Override
    public final void putEntityDao(final Class<T> clazz, final EntityDao<T> entityDao, final String entityName) {
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("There was an error putting entityDao. Cause: entityName reduplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public Map<Class<T>, EntityDao<T>> getEntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    /**
     * 构造函数
     *
     * @param properties
     */
    public EntityDaoContextImpl(ApplicationContext applicationContext, final HTableHandler hTableHandler, final CacheManager cacheManager, final FileSystem fileSystem, final TaskExecutor taskExecutor, final String ip, final DataSource dataSource, final DataHandlerFactory dataHandlerFactory) {
        this.entityDaoMap = new HashMap<Class<T>, EntityDao<T>>(64, 1);
        this.dataHandlerFactory = dataHandlerFactory;
        this.applicationContext = applicationContext;
        this.hTableHandler = hTableHandler;
        this.cacheManager = cacheManager;
//        this.fileSystem = fileSystem;
        this.taskExecutor = taskExecutor;
        this.ip = ip;
        //创建sql cache
//        final CacheConfiguration sqlCacheConfig = new DefaultCacheConfiguration().getDefault();
//        String uuid = UUID.randomUUID().toString();
//        String inquireAndCountCacheName = "InquireAndCount-cache-".concat(uuid);
//        sqlCacheConfig.name(inquireAndCountCacheName).maxEntriesLocalHeap(20000);
//        final Cache sqlCache = new Cache(sqlCacheConfig);
//        this.cacheManager.addCache(sqlCache);
//        this.inquireCache = new InquireCacheImpl(sqlCache);
        //创建lucene delete filter cache
//        final CacheConfiguration luceneCacheConfig = new DefaultCacheConfiguration().getDefault();
//        String deleteFilterCacheName = "Lucene-Delete-cache-".concat(uuid);
//        luceneCacheConfig.name(deleteFilterCacheName).maxEntriesLocalHeap(20000);
//        final Cache luceneCache = new Cache(luceneCacheConfig);
//        this.cacheManager.addCache(luceneCache);
//        this.deleteFilterCache = new DeleteFilterCacheImpl(luceneCache);
        //检测lucene索引目录是否存在，如果不存在，则创建
//        Path indexRootPath = new Path(this.indexRoot);
//        try {
//            boolean flag = this.fileSystem.exists(indexRootPath);
//            if (!flag) {
//                this.fileSystem.mkdirs(indexRootPath);
//            }
//        } catch (IOException ex) {
//            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.DAO);
//            logger.error("DAO:create lucene index directory error...see log");
//            throw new RuntimeException(ex);
//        }
        //创建索引分词对象
//        this.analyzer = new StandardAnalyzer(Version.LUCENE_41);
        //hdfs写入配置对象
//        this.iwc = new IndexWriterConfig(Version.LUCENE_41, analyzer);
//        this.iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//        this.iwc.setMaxThreadStates(1);
//        this.iwc.setMergeScheduler(new SerialMergeScheduler());
        //ram写入配置对象
//        this.ramIwc = new IndexWriterConfig(Version.LUCENE_41, analyzer);
//        this.ramIwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//        this.ramIwc.setMaxThreadStates(1);
//        this.ramIwc.setMergeScheduler(NoMergeScheduler.INSTANCE);
        //
        this.dataSource = dataSource;
    }

    @Override
    public boolean assertExistEntity(final Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }

    @Override
    public EntityDao getEntityDao(final Class<T> clazz) {
        return this.entityDaoMap.get(clazz);
    }

    @Override
    public HTableHandler getHTableHandler() {
        return this.hTableHandler;
    }

//    @Override
//    public FileSystem getFileSystem() {
//        return this.fileSystem;
//    }

//    @Override
//    public String getIndexRoot() {
//        return this.indexRoot;
//    }

//    @Override
//    public Analyzer getAnalyzer() {
//        return this.analyzer;
//    }

//    @Override
//    public IndexWriterConfig getIndexWriterConfig() {
//        return this.iwc;
//    }

//    @Override
//    public IndexWriterConfig getRamIndexWriterConfig() {
//        return this.ramIwc;
//    }

    @Override
    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    public String getIP() {
        return this.ip;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    public DataHandlerFactory getDataHandlerFactory() {
        return this.dataHandlerFactory;
    }
}
