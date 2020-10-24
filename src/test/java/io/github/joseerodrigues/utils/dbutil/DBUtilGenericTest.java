package io.github.joseerodrigues.utils.dbutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import io.github.joseerodrigues.utils.dbutil.DBUtil;
import io.github.joseerodrigues.utils.dbutil.ResultSetIterator;
import io.github.joseerodrigues.utils.dbutil.ResultSetMapper;
import io.github.joseerodrigues.utils.dbutil.mapper.SimpleResultSetMapper;

@RunWith(MockitoJUnitRunner.class)
public class DBUtilGenericTest {

    @Mock
    private DataSource ds;

    @Mock
    private Connection c;

    @Mock
    private PreparedStatement pstmt;
 
    @Mock
    private Statement stmt;

    @Mock
    private ResultSet rs;

    @Mock
    private ParameterMetaData paramMetadata;

    private boolean usingPreparedStatement = false;

    @Before
    public void setUp() throws Exception {

        assertNotNull(ds);

        usingPreparedStatement = false;
        when(c.prepareStatement(any(String.class))).thenAnswer(new Answer<PreparedStatement>() {
            @Override
            public PreparedStatement answer(InvocationOnMock invocation) throws Throwable {
                usingPreparedStatement = true;
                return pstmt;
            }
        });

        when(c.createStatement()).thenReturn(stmt);

        when(ds.getConnection()).thenReturn(c);
        when(pstmt.getParameterMetaData()).thenReturn(paramMetadata);
        when(paramMetadata.getParameterCount()).thenReturn(0);
        //
        when(rs.next()).thenReturn(true, true, false);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    @After
    public void checkCloseResources() throws SQLException {
        verify(rs).close();

        if (usingPreparedStatement){
            verify(pstmt).close();
        }else{
            verify(stmt).close();
        }

        verify(c).close();
    }

    @Test
    public void dbUtil_mapper_lifecycle() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        @SuppressWarnings("unchecked")
		ResultSetMapper<String> mockMapper = mock(ResultSetMapper.class);

        dbUtil.selectOne("SELECT * FROM GOT.JOHNSNOW", mockMapper);

        verify(mockMapper).init((ResultSet) any());
        verify(mockMapper, times(2)).mapObject((ResultSet) any());
        verify(mockMapper).terminate();
    }

    @Test
    public void dbUtil_mapper_lifecycle_param() throws SQLException {
        DBUtil dbUtil = new DBUtil(ds);

        @SuppressWarnings("unchecked")
		ResultSetMapper<String> mockMapper = mock(ResultSetMapper.class);

        dbUtil.selectOne("SELECT * FROM GOT.JOHNSNOW WHERE KNOWS_SOMETHING = ?", mockMapper, false);

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
