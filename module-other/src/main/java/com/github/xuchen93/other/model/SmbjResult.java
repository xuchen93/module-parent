package com.github.xuchen93.other.model;

import cn.hutool.core.io.IoUtil;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.Data;

@Data
public class SmbjResult {
	private SMBClient client;
	private Connection connection;
	private Session session;
	private DiskShare diskShare;

	public void close() {
		IoUtil.close(client);
		IoUtil.close(diskShare);
		IoUtil.close(connection);
		IoUtil.close(session);
	}
}
