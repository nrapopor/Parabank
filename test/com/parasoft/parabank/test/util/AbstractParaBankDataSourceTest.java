package com.parasoft.parabank.test.util;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

@SuppressWarnings("deprecation")
public abstract class AbstractParaBankDataSourceTest extends AbstractTransactionalDataSourceSpringContextTests {
    @Override
    protected final String[] getConfigLocations() {
        return new String[] { "classpath:test-context.xml" };
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.executeSqlScript("classpath:com/parasoft/parabank/dao/jdbc/sql/create.sql", true);
        super.executeSqlScript("classpath:com/parasoft/parabank/dao/jdbc/sql/insert.sql", true);
    }
}
