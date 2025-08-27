package co.crediyacorp.r2dbc.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class MySQLConnectionPoolTest {

    @InjectMocks
    private MySQLConnectionPool connectionPool;

    @Mock
    private MySQLConnectionProperties properties;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.host()).thenReturn("localhost");
        when(properties.port()).thenReturn(3306);
        when(properties.database()).thenReturn("autenticacion");
        when(properties.username()).thenReturn("myuser");
        when(properties.password()).thenReturn("mypassword");
    }

    @Test
    void getConnectionConfigSuccess() {
        assertNotNull(connectionPool.getConnectionConfig(properties));
    }
}
