package com.chris.ad.sender;

import com.chris.ad.mysql.dto.MySqlRowData;

public interface ISender {

    void sender(MySqlRowData rowData);
}
