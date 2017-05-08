package com.cave.utils.dbutil.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicResultSetMapperTest {

    @Mock
    private ResultSet rs;

    private String TEST_COL_NAME_STRING = "TEST";
    private int TEST_COL_NAME_INDEX = 1;
    private String TEST_COL_NAME_STRING_VALUE = "TEST value";

    @Before
    public void setUp() throws Exception {

        assertNotNull(rs);
        when(rs.getObject(TEST_COL_NAME_STRING)).thenReturn(TEST_COL_NAME_STRING_VALUE);
        when(rs.getObject(TEST_COL_NAME_INDEX)).thenReturn(TEST_COL_NAME_STRING_VALUE);
    }

    @Test
    public void testBasic() throws SQLException {

        BasicResultSetMapper<String> rsMapper = new BasicResultSetMapper<String>(TEST_COL_NAME_STRING);

        String r = rsMapper.mapObject(rs);

        assertEquals(TEST_COL_NAME_STRING_VALUE, r);
    }

    @Test
    public void testBasicByColIndex() throws SQLException {

        BasicResultSetMapper<String> rsMapper = new BasicResultSetMapper<String>(TEST_COL_NAME_INDEX);

        String r = rsMapper.mapObject(rs);

        assertEquals(TEST_COL_NAME_STRING_VALUE, r);
    }

}
