# db_utils
Really simple helper for JDBC.

````java
DataSource ds = ...;
DBUtil dbUtil = new DBUtil(ds);
````

Or

````java
Connection conn  = ...;
DBUtil dbUtil = new DBUtil(conn);
````

Then

````java
dbUtil.iterate(...);
dbUtil.selectAll(...);
dbUtil.selectOne(...);
dbUtil.hasResults(...);

dbUtil.insert(...);
dbUtil.update(...);
dbUtil.delete(...);

dbUtil.useConnection(...);
````

#### Sample Usage

* iterate
````java
ResultSetMapper<ResultSetMap> rsMapper = new MapResultSetMapper();

dbUtil.iterate("SELECT * FROM TEST.TABLE_NAME WHERE COL_NAME = ?", 
    rsMapper, new SimpleResultSetIterator<ResultSetMap>() {
    
    @Override
    public boolean iterate(ResultSetMap item) {

        System.out.println(item);
        return true; // false to stop iterating
    }
}, "colValue");
````

* selectOne

````java
int colIndex = 1;
Integer count = dbUtil.selectOne("SELECT COUNT(1) FROM TEST.TABLE_NAME", Mappers.intMapper(colIndex));
System.out.println("Count  = " + count);
````