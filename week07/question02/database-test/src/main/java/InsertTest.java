import java.sql.*;

public class InsertTest {
    private static final int TEST_NUM = 1_000_000;


    public static void main(String[] args) {
        // 运行时控制台提示现在不需要手动声明 mysql driver了
        String databaseUrl = "jdbc:mysql://127.0.0.1:3306/java_course";
        try (Connection conn = DriverManager.getConnection(databaseUrl, "blueman", "123456")) {
            insertOneByOne(conn);
            insertBatch(conn);
            insertValues(conn);
            insertPrepare(conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    /**
     * 逐条执行insert语句
     */
    private static void insertOneByOne(Connection conn) {
        System.out.println("insert one by one test...");
        try (Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(0);
            boolean commitFlag = conn.getAutoCommit();
            System.out.println("isolation level:" + conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            System.out.println("auto commit is:" + conn.getAutoCommit());
            long startTime = System.nanoTime();
            for (int i = 0; i < TEST_NUM; i++) {
                String id = String.valueOf(System.currentTimeMillis()) + i;
                String sql = "insert into t_order values ('"+id+"', 'blueman', '12345', 1, "+
                        System.currentTimeMillis()+", null, null, null)";
                statement.executeUpdate(sql);
            }
            conn.commit();
            System.out.println("time consume:"+(System.nanoTime() - startTime)/1000000+"ms");
            conn.setAutoCommit(commitFlag);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 批量执行insert语句
     */
    private static void insertBatch(Connection conn) {
        System.out.println("insert batch test...");
        try (Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(0);
            boolean commitFlag = conn.getAutoCommit();
            System.out.println("isolation level:" + conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            System.out.println("auto commit is:" + conn.getAutoCommit());
            long startTime = System.nanoTime();
            for (int i = 0; i < TEST_NUM; i++) {
                String id = String.valueOf(System.currentTimeMillis()) + i;
                String sql = "insert into t_order values ('"+id+"', 'blueman', '12345', 1, "+
                        System.currentTimeMillis()+", null, null, null);";
                statement.addBatch(sql);
            }
            statement.executeBatch();
            conn.commit();
            System.out.println("time consume:"+(System.nanoTime() - startTime)/1000000+"ms");
            conn.setAutoCommit(commitFlag);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 执行insert 多参数列表语句
     * 需要注意调整max_allowed_packet 参数
     */
    private static void insertValues(Connection conn) {
        System.out.println("insert value list test...");
        try (Statement statement = conn.createStatement()) {
            statement.setQueryTimeout(0);
            boolean commitFlag = conn.getAutoCommit();
            System.out.println("isolation level:" + conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            System.out.println("auto commit is:" + conn.getAutoCommit());
            long startTime = System.nanoTime();
            StringBuilder sqlBuilder = new StringBuilder("insert into t_order values");
            for (int i = 0; i < TEST_NUM; i++) {
                String id = String.valueOf(System.currentTimeMillis()) + i;
                sqlBuilder.append(" ('")
                        .append(id)
                        .append("', 'blueman', '12345', 1, ")
                        .append(System.currentTimeMillis())
                        .append(", null, null, null),");
            }
            int replaceIndex = sqlBuilder.lastIndexOf(",");
            sqlBuilder.replace(replaceIndex, replaceIndex+1, ";");
            statement.executeUpdate(sqlBuilder.toString());
            conn.commit();
            System.out.println("time consume:"+(System.nanoTime() - startTime)/1000000+"ms");
            conn.setAutoCommit(commitFlag);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 使用prepareStatement
     */
    private static void insertPrepare(Connection conn) {
        System.out.println("insert prepare statement test...");
        String sql = "insert into t_order values (?,?,?,?,?,?,?,?);";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setQueryTimeout(0);
            boolean commitFlag = conn.getAutoCommit();
            System.out.println("isolation level:" + conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            System.out.println("auto commit is:" + conn.getAutoCommit());
            long startTime = System.nanoTime();

            for (int i = 0; i < TEST_NUM; i++) {
                String id = String.valueOf(System.currentTimeMillis()) + i;
                statement.setString(1, id);
                statement.setString(2, "blueman");
                statement.setString(3, "12345");
                statement.setInt(4, 1);
                statement.setLong(5, System.currentTimeMillis());
                statement.setNull(6, Types.BIGINT);
                statement.setNull(7, Types.BIGINT);
                statement.setNull(8, Types.BIGINT);
                statement.addBatch();
            }

            statement.executeBatch();
            conn.commit();
            System.out.println("time consume:"+(System.nanoTime() - startTime)/1000000+"ms");
            conn.setAutoCommit(commitFlag);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
