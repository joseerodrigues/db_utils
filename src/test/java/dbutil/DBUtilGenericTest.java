package dbutil;

import com.cave.utils.dbutil.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DBUtilGenericTest {

    @Mock
    private DataSource ds;

    @Mock
    private Connection c;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;

    @Mock
    private ParameterMetaData paramMetadata;

    private boolean rsCloseCalled = false;
    private boolean stmtCloseCalled = false;
    private boolean connCloseCalled = false;

    @Before
    public void setUp() throws Exception {

        assertNotNull(ds);

        when(c.prepareStatement(any(String.class))).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(c);
        when(stmt.getParameterMetaData()).thenReturn(paramMetadata);
        when(paramMetadata.getParameterCount()).thenReturn(0);
        //
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        when(stmt.executeQuery()).thenReturn(rs);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                rsCloseCalled = true;
                return null;
            }
        }).when(rs).close();

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                stmtCloseCalled = true;
                return null;
            }
        }).when(stmt).close();

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                connCloseCalled = true;
                return null;
            }
        }).when(c).close();

        rsCloseCalled = false;
        stmtCloseCalled = false;
        connCloseCalled = false;
    }

    @After
    public void checkCloseResources(){
        assertEquals(true, rsCloseCalled);
        assertEquals(true, stmtCloseCalled);
        assertEquals(true, connCloseCalled);
    }

    @Test
    public void dbUtil_mapper_lifecycle() {

        final String RET = "MEGA ITEM";
        DBUtil dbUtil = new DBUtil(ds);
        final AtomicReference<Boolean> initCalled = new AtomicReference<>(false);
        final AtomicReference<Boolean> terminateCalled = new AtomicReference<>(false);
        final AtomicReference<Boolean> mapCalled = new AtomicReference<>(false);

        dbUtil.selectOne("SELECT * FROM GOT.JOHNSNOW", new ResultSetMapper<String>() {

            @Override
            public void init(ResultSet rs) throws SQLException {
                initCalled.getAndSet(true);
            }

            @Override
            public void terminate() {
                terminateCalled.getAndSet(true);
            }

            @Override
            public String mapObject(ResultSet rs) throws SQLException {
                mapCalled.getAndSet(true);
                return RET;
            }
        });

        assertEquals(true, initCalled.get());
        assertEquals(true, terminateCalled.get());
        assertEquals(true, mapCalled.get());
    }

    @Test
    public void dbUtil_iterator_lifecycle() {

        final String RET = "MEGA ITEM";
        DBUtil dbUtil = new DBUtil(ds);
        final AtomicReference<Boolean> initCalled = new AtomicReference<>(false);
        final AtomicReference<Boolean> terminateCalled = new AtomicReference<>(false);
        final AtomicReference<Boolean> iterateCalled = new AtomicReference<>(false);

        dbUtil.iterate("SELECT * FROM GOT.JOHNSNOW", new SimpleResultSetMapper<String>() {
            @Override
            public String mapObject(ResultSet rs) throws SQLException {
                return RET;
            }
        }, new ResultSetIterator<String>() {
            @Override
            public void init() {
                initCalled.getAndSet(true);
            }

            @Override
            public void terminate() {
                terminateCalled.getAndSet(true);
            }

            @Override
            public boolean iterate(String item) {
                iterateCalled.getAndSet(true);
                return false;
            }
        });

        assertEquals(true, initCalled.get());
        assertEquals(true, terminateCalled.get());
        assertEquals(true, iterateCalled.get());
    }

    @Test
    public void dbUtil_iterate() {

        final String RET = "MEGA ITEM";
        DBUtil dbUtil = new DBUtil(ds);
        final AtomicInteger iterateCount = new AtomicInteger(0);

        dbUtil.iterate("SELECT * FROM GOT.JOHNSNOW", new SimpleResultSetMapper<String>() {

            @Override
            public String mapObject(ResultSet rs) throws SQLException {
                return RET;
            }
        }, new SimpleResultSetIterator<String>() {
            @Override
            public boolean iterate(String item) {
                assertEquals(RET, item);
                iterateCount.incrementAndGet();
                return false;
            }
        });

        assertEquals(1, iterateCount.get());
    }

    @Test
    public void dbUtil_selectOne() {

        final String RET = "MEGA ITEM";
        DBUtil dbUtil = new DBUtil(ds);

        String selectedObj = dbUtil.selectOne("SELECT * FROM GOT.JOHNSNOW", new SimpleResultSetMapper<String>() {
            @Override
            public String mapObject(ResultSet rs) throws SQLException {
                return RET;
            }
        });

        assertEquals(RET, selectedObj);
    }

    @Test
    public void dbUtil_selectAll() {

        final String RET = "MEGA ITEM";
        DBUtil dbUtil = new DBUtil(ds);

        List<String> ret = dbUtil.selectAll("SELECT * FROM GOT.JOHNSNOW", new SimpleResultSetMapper<String>() {
            @Override
            public String mapObject(ResultSet rs) throws SQLException {
                return RET;
            }
        });

        assertEquals(2, ret.size());
        assertEquals(RET, ret.get(0));
        assertEquals(RET, ret.get(1));
    }
}
