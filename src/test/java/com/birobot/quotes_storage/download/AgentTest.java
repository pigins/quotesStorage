package com.birobot.quotes_storage.download;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.database.QuotesDatabase;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class AgentTest {

    @Test
    public void testSetDelisted() {
        String symbol = "ETHUSD";
        QuotesDatabase dbMock = mock(QuotesDatabase.class);
        Client clientMock = mock(Client.class);
        when(clientMock.getDateOfLastClose(symbol)).thenAnswer((Answer<OffsetDateTime>) invocationOnMock -> OffsetDateTime.now().minusMinutes(1));
        when(clientMock.getOneMinuteBars(eq(symbol), any(OffsetDateTime.class))).thenReturn(new ArrayList<>());
        when(clientMock.isAvailable()).thenReturn(true);
        Agent agent = new Agent(symbol, dbMock, clientMock,new AgentParams(15, 10*60));
        ReflectionTestUtils.setField(agent, "latestClose", OffsetDateTime.now());
        agent.setSymbolDelisted();
        Assert.assertFalse(agent.needNext());
    }

    @Test
    public void testEmptyResult() throws InterruptedException {
        String symbol = "ETHUSD";
        QuotesDatabase dbMock = mock(QuotesDatabase.class);
        Client clientMock = mock(Client.class);
        when(clientMock.getDateOfLastClose(symbol)).thenAnswer((Answer<OffsetDateTime>) invocationOnMock -> OffsetDateTime.now().minusMinutes(1));
        when(clientMock.getOneMinuteBars(eq(symbol), any(OffsetDateTime.class))).thenReturn(new ArrayList<>());
        when(clientMock.isAvailable()).thenReturn(true);
        Agent agent = new Agent(symbol, dbMock, clientMock, new AgentParams(15, 2));
        agent.init();
        ReflectionTestUtils.setField(agent, "latestClose", OffsetDateTime.now().minusHours(20));
        agent.downloadNext();
        Assert.assertFalse(agent.needNext());
        Thread.sleep(3000);
        Assert.assertTrue(agent.needNext());
    }

}