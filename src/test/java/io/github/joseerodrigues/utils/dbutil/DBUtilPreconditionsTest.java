package io.github.joseerodrigues.utils.dbutil;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.joseerodrigues.utils.dbutil.DBUtil;
import io.github.joseerodrigues.utils.dbutil.mapper.Mappers;

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
        when(ds.getConnection()).thenReturn(c);       
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
