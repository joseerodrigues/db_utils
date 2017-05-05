package dbutil;

import com.cave.utils.dbutil.DBUtil;
import com.cave.utils.dbutil.SimpleResultSetIterator;
import com.cave.utils.dbutil.SimpleResultSetMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DBUtilTest {

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
