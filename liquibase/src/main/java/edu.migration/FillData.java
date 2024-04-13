package edu.migration;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class FillData implements CustomTaskChange{
    @Override
    public void execute(Database database) throws CustomChangeException {
        JdbcConnection jdbcConnection = (JdbcConnection) database.getConnection();
        Connection connection = jdbcConnection.getWrappedConnection();
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        var tableNames = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'ACC'", String.class);
        for (String table : tableNames) {
            Map<String, Object> columnValueMap = new HashMap<>();
            for (String column : jdbcTemplate.queryForList("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?", new Object[]{table}, String.class)) {
                switch (column){
                    case "ID": columnValueMap.put(column, 1); break;
                    default: if (column.contains("ID")) columnValueMap.put(column, 1);
                }
            }
            try {
                System.out.println(insertData(jdbcTemplate, table, columnValueMap));
            } catch (RuntimeException ex){

            }
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    public String insertData(JdbcTemplate jdbcTemplate, String tableName, Map<String, Object> columnValueMap) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, Object> entry : columnValueMap.entrySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(entry.getKey());
            values.append("?");
        }
        String sql = "INSERT INTO ACC." + tableName + " (" + columns + ") VALUES (" + values + ")";
        jdbcTemplate.update(sql, columnValueMap.values().toArray());
        return sql;
    }
}