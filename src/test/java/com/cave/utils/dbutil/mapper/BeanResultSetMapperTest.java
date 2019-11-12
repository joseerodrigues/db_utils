package com.cave.utils.dbutil.mapper;

import com.cave.utils.dbutil.ResultSetMapper;
import com.cave.utils.dbutil.annotation.DBColumn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanResultSetMapperTest {

    public static class MyEntity {
        @DBColumn
        private int id;

        @DBColumn(value ="shortinfo", trim = true)
        private String description;

        @DBColumn
        private long quantity;
    }

    @Mock
    private ResultSet rs;

    @Mock
    private ResultSetMetaData metadata;

    @Before
    public void setUp() throws Exception {

        assertNotNull(rs);
        assertNotNull(metadata);

        when(rs.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(3);

        when(metadata.getColumnLabel(1)).thenReturn("id");
        when(metadata.getColumnLabel(2)).thenReturn("shortinfo");
        when(metadata.getColumnLabel(3)).thenReturn("quantity");

        when(rs.getObject("id")).thenReturn(1);
        when(rs.getObject("shortinfo")).thenReturn("super short info   ");
        when(rs.getObject("quantity")).thenReturn(30);
    }

    @Test
    public void testBeanMapper() throws SQLException {

        ResultSetMapper<MyEntity> mapper = Mappers.beanMapper(MyEntity.class);

        mapper.init(rs);
        MyEntity result = mapper.mapObject(rs);
        assertNotNull(result);
        mapper.terminate();

        assertEquals(1, result.id);
        assertEquals("super short info", result.description);
        assertEquals(30, result.quantity);
    }
}
