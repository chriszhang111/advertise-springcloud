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

    static {

        try {

            Class.forName(driver);

        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }



// 单例模式返回数据库连接对象

    public static Connection getConnection() throws Exception {

        if (conn == null) {

            conn = DriverManager.getConnection(url, username, password);

            return conn;

        }

        return conn;

    }

    /*
    Update--------------
    UpdateRowsEventData{tableId=81, includedColumnsBeforeUpdate={0, 1, 2}, includedColumns={0, 1, 2}, rows=[
    {before=[12, 10, 大众], after=[12, 10, 大众1]}
    ]}
    Write---------------
    WriteRowsEventData{tableId=81, includedColumns={0, 1, 2}, rows=[
    [13, 10, NBA]
    ]}
     */


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
