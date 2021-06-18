import java.sql.*;

public class InsertTest {
    private static final int TEST_NUM = 1000;

    private static final String[] USER_ARR = new String[] {"204043318363111424", "204043318363111425"};
    public static void main(String[] args) {
        // 运行时控制台提示现在不需要手动声明 mysql driver了
        String databaseUrl = "jdbc:mysql://127.0.0.1:3307/sharding_db";     // 连接 shardingsphere-proxy
        try (Connection conn = DriverManager.getConnection(databaseUrl, "root", "root")) {
            insertPrepare(conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 使用prepareStatement
     */
    private static void insertPrepare(Connection conn) {
        System.out.println("insert prepare statement test...");
        String sql = "insert into t_order(`create_by`, `goods_id`, `goods_num`, `create_time`) values (?,?,?,?);";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setQueryTimeout(0);
            boolean commitFlag = conn.getAutoCommit();
            System.out.println("isolation level:" + conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            System.out.println("auto commit is:" + conn.getAutoCommit());
            long startTime = System.nanoTime();

            for (int i = 0; i < TEST_NUM; i++) {
                statement.setLong(1, Long.parseLong(USER_ARR[i%2]));
                statement.setString(2, "g12345");
                statement.setInt(3, i % 10);
                statement.setLong(4, System.currentTimeMillis());
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
