package io.github.joseerodrigues.utils.dbutil;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.Silent.class)
public class UncloseableConnectionTest {

    @Mock
    private Connection conn;
 
    @Before
    public void setUp() throws Exception {

        assertNotNull(conn);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalAccessError();
            }
        }).when(conn).close();
    }

    @Test
    public void testClose() throws SQLException {

        UncloseableConnectionImpl uc = new UncloseableConnectionImpl(conn);

        try{
            uc.close();
        }catch(IllegalAccessError e){
            fail("sqlConnection close called");
        }
    }
}
