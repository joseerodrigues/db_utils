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

