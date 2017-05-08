package com.cave.utils.dbutil;

import com.cave.utils.dbutil.DBUtil;
import com.cave.utils.dbutil.JDBCAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DBUtilConnectionTest {

    @Mock
    private DataSource ds;

    @Mock
    private Connection c;

    @Before
    public void setUp() throws Exception {

        assertNotNull(ds);
        when(ds.getConnection()).thenReturn(c);
    }

    @Test
    public void dbUtil_useConnection() throws SQLException {

        DBUtil dbUtil = new DBUtil(ds);

        dbUtil.useConnection(new JDBCAction<Object>() {
            @Override
            public Object execute(Connection conn) throws SQLException {
                conn.close();
                verifyZeroInteractions(c);
                return null;
            }
        });

        verify(c).close();
    }
}
