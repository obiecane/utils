package com.ahzak.utils.resource;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯COS上传工具
 * 
 * @author: tom
 * @date: 2018年4月14日 下午4:00:16
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved. Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class QcoscloudClient {

//	/**
//	 * 本地文件上传腾讯cos
//	 * @Title: uploadFileByLocal
//	 * @param bucketArea bucketArea
//	 * @param bucketName bucketName
//	 * @param appId appId
//	 * @param secretId  secretId
//	 * @param appKey appKey
//	 * @param cosPath
//	 *            远程文件夹的名称
//	 * @param localPath
//	 *            文件的本地路径
//	 * @return: String
//	 */
//	public static String uploadFileByLocal(String bucketArea, String bucketName, String appId, String secretId,
//			String appKey, String cosPath, String localPath) {
//		COSClient cosClient = getCOSClient(bucketArea, appId, secretId, appKey);
//		if (cosClient != null) {
//			UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, cosPath, localPath);
//			String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
//			return uploadFileRet;
//		} else {
//			return "";
//		}
//
//	}

//	/**
//	 * 以字节数组的形式上传文件
//	 * @Title: uploadFileByByte
//	 * @param bucketArea bucketArea
//	 * @param bucketName bucketName
//	 * @param appId appId
//	 * @param secretId secretId
//	 * @param appKey appKey
//	 * @param cosPath
//	 *            目标路径
//	 * @param content
//	 *            字节数组
//	 * @return: String
//	 */
//	public static String uploadFileByByte(String bucketArea, String bucketName, String appId, String secretId,
//			String appKey, String cosPath, byte[] content) {
//		COSClient cosClient = getCOSClient(bucketArea, appId, secretId, appKey);
//		if (cosClient != null) {
//			UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, cosPath, content);
//			String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
//			return uploadFileRet;
//		} else {
//			return "";
//		}
//	}

	/**
	 * 上传文件 以输入流的形式
	 * @Title: uploadFileByInputStream
	 * @param bucketArea bucketArea
	 * @param bucketName bucketName
	 * @param appId appId
	 * @param secretId secretId
	 * @param appKey appKey
	 * @param cosPath
	 *            目标路径
	 * @param inputStream
	 *            输入流
	 * @return: String
	 */
	public static String uploadFileByInputStream(String bucketArea, String bucketName,
			String appId, String secretId,
			String appKey, String cosPath, InputStream inputStream) throws IOException {
		COSClient cosClient = getCOSClient(bucketArea, appId, secretId, appKey);
		if (cosClient != null) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(inputStream.available());
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, cosPath, inputStream, objectMetadata);
			PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
			// putobjectResult会返回文件的etag
			String etag = putObjectResult.getETag();
			cosClient.shutdown();
			return etag;
		} else {
			return "";
		}
	}

//	/**
//	 * 移动文件
//	 * @Title: moveFile
//	 * @param bucketArea bucketArea
//	 * @param bucketName bucketName
//	 * @param appId appId
//	 * @param secretId  secretId
//	 * @param appKey appKey
//	 * @param cosFilePath
//	 *            原路径
//	 * @param dstCosFilePath
//	 *            目标路径
//	 * @return: String
//	 */
//	public static String moveFile(String bucketArea, String bucketName, String appId,
//			String secretId, String appKey,
//			String cosFilePath, String dstCosFilePath) {
//		COSClient cosClient = getCOSClient(bucketArea, appId, secretId, appKey);
//		if (cosClient != null) {
//			MoveFileRequest moveRequest = new MoveFileRequest(bucketName, cosFilePath, dstCosFilePath);
//			String moveFileResult = cosClient.moveFile(moveRequest);
//			return moveFileResult;
//		} else {
//			return "";
//		}
//	}

//	/**
//	 * 删除文件
//	 * @Title: deleteFile
//	 * @param bucketArea bucketArea
//	 * @param bucketName bucketName
//	 * @param appId appId
//	 * @param secretId secretId
//	 * @param appKey appKey
//	 * @param cosFilePath
//	 *            目标文件路径
//	 * @return: String
//	 */
//	public static String deleteFile(String bucketArea, String bucketName,
//			String appId, String secretId, String appKey,String cosFilePath) {
//		COSClient cosClient = getCOSClient(bucketArea, appId, secretId, appKey);
//		if (cosClient != null) {
//			DelFileRequest delFileRequest = new DelFileRequest(bucketName, cosFilePath);
//			String delFileRet = cosClient.delFile(delFileRequest);
//			return delFileRet;
//		} else {
//			return "";
//		}
//	}




	/**
	 * 获取腾讯COS Client
	 * @Title: getCOSClient
	 * @param bucketArea bucketArea
	 * @param appId  appId
	 * @param secretId secretId
	 * @param appKey appKey
	 * @return: COSClient
	 */
	public static COSClient getCOSClient(String bucketArea, String appId, String secretId, String appKey) {
		if (StringUtils.isNotBlank(appId) && StringUtils.isNumeric(appId)) {
			// 1 初始化用户身份信息（secretId, secretKey）。
			COSCredentials cred = new BasicCOSCredentials(secretId, appKey);
			// 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
			// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
			Region region = new Region(bucketArea);
			ClientConfig clientConfig = new ClientConfig(region);
			// 3 生成 cos 客户端。
			COSClient cosClient = new COSClient(cred, clientConfig);
			return cosClient;
		} else {
			log.error("get cos client  error");
			return null;
		}
	}

	/**
	 * 输入流转换字节数组
	 * @Title: toByteArray  
	 * @param input  InputStream
	 * @return: byte[]
	 */
	public static byte[] toByteArray(InputStream input) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		try {
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return output.toByteArray();
	}
	

}