package com.cave.utils.dbutil;

import org.junit.After;
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
import static org.mockito.Mockito.*;

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

    @After
    public void checkCloseResources() throws SQLException {
        verify(rs).close();
        verify(stmt).close();
        verify(c).close();
    }

    @Test
    public void dbUtil_mapper_lifecycle() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        ResultSetMapper<String> mockMapper = mock(ResultSetMapper.class);

        dbUtil.selectOne("SELECT * FROM GOT.JOHNSNOW", mockMapper);

        verify(mockMapper).init((ResultSet) any());
        verify(mockMapper, times(2)).mapObject((ResultSet) any());
        verify(mockMapper).terminate();
    }

    @Test
    public void dbUtil_iterator_lifecycle() throws SQLException {

        DBUtil dbUtil = new DBUtil(ds);

        ResultSetMapper<String> mockMapper = mock(ResultSetMapper.class);
        ResultSetIterator<String> mockIterator = mock(ResultSetIterator.class);

        when(mockMapper.mapObject((ResultSet) any())).thenReturn("");
        dbUtil.iterate("SELECT * FROM GOT.JOHNSNOW", mockMapper, mockIterator);

        verify(mockIterator).init();
        verify(mockIterator).iterate(anyString());
        verify(mockIterator).terminate();
    }

    @Test
    public void dbUtil_iterate() throws SQLException {

        DBUtil dbUtil = new DBUtil(ds);
        final AtomicInteger iterateCount = new AtomicInteger(0);

        ResultSetMapper<String> mockMapper = mock(ResultSetMapper.class);
        ResultSetIterator<String> mockIterator = mock(ResultSetIterator.class);

        when(mockMapper.mapObject((ResultSet) any())).thenReturn("");
        dbUtil.iterate("SELECT * FROM GOT.JOHNSNOW", mockMapper, mockIterator);

        verify(mockIterator).iterate(anyString());
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
