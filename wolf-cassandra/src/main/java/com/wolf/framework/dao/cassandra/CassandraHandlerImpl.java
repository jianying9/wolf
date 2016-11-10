package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import java.util.List;

/**
 * 普通表
 *
 * @author jianying9
 */
public class CassandraHandlerImpl {

//    private final Map<String, String> sets;
//    private final Map<String, String> lists;
//    private final Map<String, String> maps;

    public CassandraHandlerImpl(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList
    ) {
//        this.sets = sets;
//        this.lists = lists;
//        this.maps = maps;
    }

//    public void addSet(String keyValue, String columnName, String value) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(value);
//        this.addSet(keyValue, columnName, set);
//    }
//
//    public void addSet(String keyValue, String columnName, Set<String> values) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
    
//    public void removeSet(String keyValue, String columnName, String value) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(value);
//        this.removeSet(keyValue, columnName, set);
//    }
//
//    public void removeSet(String keyValue, String columnName, Set<String> values) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }

//    public void clearSet(String keyValue, String columnName) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }

//    public Set<String> getSet(String keyValue, String columnName) {
//        Set<String> result = Collections.EMPTY_SET;
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getSet(0, String.class);
//            }
//        }
//        return result;
//    }
//    public void addList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.addList(keyValue, columnName, list);
//    }
//
//    public void addList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void addFirstList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.addFirstList(keyValue, columnName, list);
//    }
//
//    public void addFirstList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append("? + ").append(dataMap).append(" WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void removeList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.removeList(keyValue, columnName, list);
//    }
//
//    public void removeList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void clearList(String keyValue, String columnName) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = [] WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public List<String> getList(String keyValue, String columnName) {
//        List<String> result = Collections.EMPTY_LIST;
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getList(0, String.class);
//            }
//        }
//        return result;
//    }
//    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue) {
//        Map<String, String> map = new HashMap<String, String>(2, 1);
//        map.put(mapKeyValue, mapValue);
//        this.addMap(keyValue, columnName, map);
//    }
//    public void addMap(String keyValue, String columnName, Map<String, String> values) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void removeMap(String keyValue, String columnName, String mapKeyValue) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(mapKeyValue);
//        this.removeMap(keyValue, columnName, set);
//    }
//    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(mapKeyValues, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void clearMap(String keyValue, String columnName) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public Map<String, String> getMap(String keyValue, String columnName) {
//        Map<String, String> result = Collections.EMPTY_MAP;
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getMap(0, String.class, String.class);
//            }
//        }
//        return result;
//    }
}
