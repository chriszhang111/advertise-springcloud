package com.chris.ad.mysql.listener;

import com.chris.ad.mysql.dto.BinlogRowData;

public interface Ilistener {

    void register();

    void onEvent(BinlogRowData eventData);
}
