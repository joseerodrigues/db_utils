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
dbUtil.iterate("SELECT * FROM TEST.TABLE_NAME WHERE COL_NAME = ?", 
    Mappers.mapMapper(), new SimpleResultSetIterator<ResultSetMap>() {
    
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

* hasResults

````java
boolean hasResults = dbUtil.hasResults("SELECT * FROM TEST.TABLE_NAME WHERE COL_NAME = ?", "colValue");
System.out.println("hasResults = " + hasResults);
````

* selectAll

Considering the following table *TEST_TABLE*

id | shortinfo | quantity
--- | --- | ---
1 | This is a short info | 30
2 | another one | 20
3 | something something | 15
4 | another something | 10

And the following java Entity

````java
public class Product {
    @DBColumn(value = "shortinfo", trim = true)
    private String description = null;

    @DBColumn
    private int quantity = null;

    @DBColumn
    private long id = null;

    /* getters and setters*/    
}
````

The code for **selectAll** becomes:

````java
List<Product> products = dbUtil.selectAll("SELECT * FROM TEST_TABLE", 
                                Mappers.beanMapper(Product.class));
````

And to select only those items with quantity >= 20:

````java
List<Product> products = dbUtil.selectAll("SELECT * FROM TEST_TABLE WHERE quantity >= ?", 
                                Mappers.beanMapper(Product.class), 20);
````

>**Note**
>*selectAll* fetches eagerly. Be careful when using it to retrieve very large amounts of data.