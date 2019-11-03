package com.chris.ad.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class BinlogServiceTest {

    private static final String driver = "com.mysql.cj.jdbc.Driver"; // 数据库驱动

// 连接数据库的URL地址

    private static final String url = "jdbc:mysql://localhost:3306/imooc_ad_data?serverTimezone=GMT";

    private static final String username = "root";// 数据库的用户名

    private static final String password = "31415926";// 数据库的密码

    private static Connection conn = null;



// 静态代码块负责加载驱动
    /*

    WriteRowsEventData{tableId=88, includedColumns={0, 1, 2, 3, 4, 5, 6, 7},
     rows=[
    [11, 15, Test Plan, 1,
    Sun Nov 03 09:35:45 EST 2019,
    Tue Nov 03 09:35:48 EST 2020,
    Sat Nov 02 20:00:00 EDT 2019,
    Sat Nov 02 20:00:00 EDT 2019]
     */

    static {

        try {

            Class.forName(driver);

        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }
    public static Connection getConnection() throws Exception {
        if (conn == null) {
            conn = DriverManager.getConnection(url, username, password);
            return conn;
        }
        return conn;
    }

    public static void main(String[] args) throws Exception {

        BinaryLogClient client = new BinaryLogClient(
                "127.0.0.1",
                3306,
                "root",
                "31415926"
        );

        client.setBinlogFilename("mysql-bin.000002");

        client.registerEventListener(event->{
            EventHeader header = event.getHeader();
//            log.info("header:{}",header);
            EventData data = event.getData();
            if (data instanceof UpdateRowsEventData) {
                System.out.println("Update--------------");
                System.out.println(data.toString());
            } else if (data instanceof WriteRowsEventData) {
                System.out.println("Write---------------");
                System.out.println(data.toString());
            } else if (data instanceof DeleteRowsEventData) {
                System.out.println("Delete--------------");
                System.out.println(data.toString());
            }
        });
        client.connect();

    }
}
