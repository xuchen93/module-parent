package com.github.xuchen93.other.util.smb;

import cn.hutool.core.io.IoUtil;
import com.github.xuchen93.other.model.SmbjResult;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * 只针对部分场景做了封装
 * 实际操作可以根据具体的权限要求修改参数
 */
@Slf4j
public class SmbjUtil {

	private static final SmbConfig SMB_CONFIG = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
			.withTimeout(120, TimeUnit.SECONDS) // 超时设置读，写和Transact超时（默认为60秒）
			.withSoTimeout(180, TimeUnit.SECONDS) // Socket超时（默认为0秒）
			.build();


	public static AuthenticationContext getAuthContext(String user, String password, String domain) {
		return new AuthenticationContext(user, password.toCharArray(), domain);
	}

	/**
	 * 获取链接
	 *
	 * @param server  服务器ip
	 * @param baseDir 根路径
	 */
	@SneakyThrows
	public static SmbjResult getSmbjResult(AuthenticationContext authenticationContext, String server, String baseDir) {
		SmbjResult result = new SmbjResult();
		SMBClient client = new SMBClient(SMB_CONFIG);
		result.setClient(client);
		Connection connection = client.connect(server);
		Session session = connection.authenticate(authenticationContext);
		DiskShare diskShare = (DiskShare) session.connectShare(baseDir);
		result.setDiskShare(diskShare);
		result.setConnection(connection);
		result.setSession(session);
		return result;
	}

	/**
	 * 获取文件
	 *
	 * @param fileAbsolutePath 文件绝对路径
	 */
	public static File getSmbjFile(SmbjResult result, String fileAbsolutePath) {
		return result.getDiskShare().openFile(fileAbsolutePath, EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
	}

	/**
	 * 删除文件
	 */
	public static void deleteFile(File file) {
		file.deleteOnClose();
	}

	/**
	 * 上传文件
	 *
	 * @param fileAbsolutePath 文件绝对路径
	 */
	public static void uploadFile(InputStream inputStream, SmbjResult smbjResult, String fileAbsolutePath) {
		File file = smbjResult.getDiskShare().openFile(fileAbsolutePath, EnumSet.of(AccessMask.GENERIC_ALL), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OVERWRITE_IF, null);
		OutputStream outputStream = file.getOutputStream();
		IoUtil.copy(inputStream, outputStream);
	}

}
