package luke.qam2;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HelperFunctionsTest {

    @Test
    void openConnection() throws SQLException {
        HelperFunctions.openConnection();
        assertEquals(false, HelperFunctions.connection.isClosed());
    }

    @Test
    void closeConnection() throws SQLException {
        HelperFunctions.openConnection();
        HelperFunctions.closeConnection();
        assertEquals(true, HelperFunctions.connection.isClosed());
    }

    @Test
    void convertDateTimeUTC() {
        //testing against my PST time
        LocalDateTime testTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
        LocalDateTime expectedTime = LocalDateTime.of(2022, 1, 1, 8, 0, 0, 0);
        assertEquals(expectedTime, HelperFunctions.convertDateTimeUTC(testTime));
    }

    @Test
    void convertDateTimeEST() {
        //testing against my PST time
        LocalDateTime testTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
        LocalDateTime expectedTime = LocalDateTime.of(2022, 1, 1, 3, 0, 0, 0);
        assertEquals(expectedTime, HelperFunctions.convertDateTimeEST(testTime));
    }

    @Test
    void convertUTCToLocal() {
        //testing against my PST time
        LocalDateTime testTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
        LocalDateTime expectedTime = LocalDateTime.of(2021, 12, 31, 16, 0, 0, 0);
        assertEquals(expectedTime, HelperFunctions.convertUTCToLocal(testTime));
    }
}