package com.switchfully.order;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class ControllerIntegrationTest {

    @LocalServerPort
    private int port;

    public int getPort() {
        return port;
    }

    @Before
    public void clearAndFlushDatabase() {
        clearDatabase();
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }

    public abstract void clearDatabase();
}