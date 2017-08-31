package com.cave.utils.dbutil;

import com.cave.utils.dbutil.mapper.Mappers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DBUtilPreconditionsTest {

    @Mock
    private DataSource ds;

    @Mock
    private Connection c;

    @Mock
    private PreparedStatement pstmt;

    @Mock
    private ResultSet rs;

    @Mock
    private ParameterMetaData paramMetadata;

    @Before
    public void setUp() throws Exception {

        assertNotNull(ds);
        when(c.prepareStatement(any(String.class))).thenAnswer(new Answer<PreparedStatement>() {
            @Override
            public PreparedStatement answer(InvocationOnMock invocation) throws Throwable {
                return pstmt;
            }
        });

        when(ds.getConnection()).thenReturn(c);
        when(pstmt.getParameterMetaData()).thenReturn(paramMetadata);
        when(paramMetadata.getParameterCount()).thenReturn(0);
        when(rs.next()).thenReturn(true, true, false);
        when(pstmt.executeQuery()).thenReturn(rs);
    }

    @Test(expected = NullPointerException.class)
    public void checkNullDS() throws SQLException {
       new DBUtil((DataSource)null);
    }

    @Test(expected = NullPointerException.class)
    public void checkNullConn() throws SQLException {
        new DBUtil((Connection) null);
    }

    @Test(expected = NullPointerException.class)
    public void checkNull_iterate_query() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        dbUtil.iterate(null,null, null, (Object[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void checkNull_iterate_mapper() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        dbUtil.iterate("SELECT STARS FROM BIGBANG",null, null, (Object[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void checkNull_iterate_iterator() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        dbUtil.iterate("SELECT STARS FROM BIGBANG", Mappers.mapMapper(), null, (Object[]) null);
    }
}
