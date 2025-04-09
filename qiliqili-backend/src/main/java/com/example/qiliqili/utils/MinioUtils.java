package com.example.qiliqili.utils;

import com.example.qiliqili.enums.UploadEnum;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kuailemao
 * <p>
 * 创建时间：2023/12/26 19:16
 * 文件上传工具类
 */
@Slf4j
@Component
public class MinioUtils {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.videoBucket}")
    private String videoBucket;
    @Value("${minio.videoChunkBucket}")
    private String videoChunkBucket;
    @Value("${minio.videoCoverBucket}")
    private String videoCoverBucket;
    @Value("${minio.userAvatarBucket}")
    private String userAvatarBucket;
    @Value("${minio.chunkSize}")
    private String chunkSize;
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件
     *
     *
     * @param file       文件
     * @return 上传后的文件地址
     * @throws Exception 异常
     */
    public String uploadChunk(MultipartFile file, String hash, Integer index) throws Exception {
        String objectName = hash;


        PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(videoChunkBucket)
                        .object(objectName+"-"+index)
                        .stream(file.getInputStream(), file.getSize(), -1) // -1 表示不分片，由 MinIO 自动处理
                        .contentType(file.getContentType())
                        .build();

        ObjectWriteResponse response = minioClient.putObject(args);
            response.etag();

//            client.putObject(args);
//            return endpoint + "/" + bucketName + "/" + uploadEnum.getDir() + name + "." + getFileExtension(file.getOriginalFilename());
            return "ds";

    }
    /**
     * 合并分片并完成上传
     */
    public String completeMultipartUpload(String hash) throws Exception {
        String finalObjectName =hash+".mp4"; // 最终文件名
        long nums = countFilesWithPrefix(videoChunkBucket,hash+"-");
        // 合并分片（MinIO 不直接支持客户端合并，需要手动实现）
        List<ComposeSource> sources = new ArrayList<>();
        for (int i=0; i<nums; i++) {
            sources.add(ComposeSource.builder()
                    .bucket(videoChunkBucket)
                    .object(hash + "-" + i)
                    .build());
        }

        // 使用 composeObject 合并所有分片
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(videoBucket)
                        .object(finalObjectName)
                        .sources(sources)
                        .extraHeaders(Map.of("Content-Type", "video/mp4"))
                        .build()
        );

        // 可选：删除临时分片文件
//        for (PartResponse part : parts) {
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(uploadId + "_part_" + part.getPartNumber())
//                            .build()
//            );
//        }

        return endpoint+"/"+videoBucket+"/"+finalObjectName;
    }


    public String uploadVideoCover(MultipartFile file,String hash) throws Exception {
        String objectName = hash;
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));


        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(videoCoverBucket)
                .object(objectName+type)
                .stream(file.getInputStream(), file.getSize(), -1) // -1 表示不分片，由 MinIO 自动处理
                .contentType(file.getContentType())
                .build();

        ObjectWriteResponse response = minioClient.putObject(args);


//            client.putObject(args);
//            return endpoint + "/" + bucketName + "/" + uploadEnum.getDir() + name + "." + getFileExtension(file.getOriginalFilename());
        return endpoint+"/"+videoCoverBucket+"/"+objectName+type;

    }

    public String uploadUserAvatar(Integer uid, MultipartFile file) throws Exception {
        Integer objectName = uid;
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));


        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(userAvatarBucket)
                .object(objectName+type)
                .stream(file.getInputStream(), file.getSize(), -1) // -1 表示不分片，由 MinIO 自动处理
                .contentType(file.getContentType())
                .build();

        ObjectWriteResponse response = minioClient.putObject(args);


//            client.putObject(args);
//            return endpoint + "/" + bucketName + "/" + uploadEnum.getDir() + name + "." + getFileExtension(file.getOriginalFilename());
        return endpoint+"/"+userAvatarBucket+"/"+objectName+type;

    }
    /**
     * 计算指定桶中以特定前缀开头的文件数量
     * @param bucketName 桶名称
     * @param prefix 文件名前缀
     * @return 文件数量
     */
    public long countFilesWithPrefix(String bucketName, String prefix) {
        try {
            // 构建查询参数
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)    // 设置前缀
                    .recursive(true)   // 递归查询所有匹配文件
                    .build();

            // 获取对象迭代器
            Iterable<Result<Item>> results = minioClient.listObjects(args);

            // 计数器
            long count = 0;

            // 遍历结果并计数
            for (Result<Item> result : results) {
                // 确保是文件而不是目录
                Item item = result.get();
                if (!item.isDir()) {
                    count++;
                }
            }

            return count;

        } catch (Exception e) {
            throw new RuntimeException("Error counting files in MinIO: " + e.getMessage());
        }
    }
    /**
    /**
     * 文件上传合法校验
     *
     * @param uploadEnum 文件枚举
     * @param file       文件
     * @throws FileUploadException 文件上传异常
     */
    public void isCheck(UploadEnum uploadEnum, MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("上传文件为空");
        }
        // 验证文件大小
        if (verifyTheFileSize(file.getSize(), uploadEnum.getLimitSize())) {
            throw new FileUploadException("上传文件超过限制大小:" + uploadEnum.getLimitSize() + "MB");
        }
    }
    /**
     * 根据文件名删除指定桶中的文件
     * @param bucketName 桶名称
     * @param fileName 文件名（包含完整路径）
     * @return 删除是否成功
     */
    public boolean deleteFile(String bucketName, String fileName) {
        try {
            // 构建删除参数
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();

            // 执行删除操作
            minioClient.removeObject(args);
            return true;

        } catch (MinioException e) {
            throw new RuntimeException("Error deleting file from MinIO: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
    /**
     * 获取文件后缀
     *
     * @param originalFilename 文件名
     * @return 文件后缀
     */
    public String getFileExtension(String originalFilename) {
        String fileExtension = null;
        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return fileExtension;
    }


    public Boolean verifyTheFileSize(Long fileSize, Double limitSize) {
        // 转为相同大小格式
        double formatFileSize = convertFileSizeToMB(fileSize);
        if (formatFileSize < limitSize) {
            return false;
        }
        return true;
    }

    /**
     * B 转 MB
     *
     * @param sizeInBytes 文件大小 B
     * @return 文件大小 MB
     */
    public double convertFileSizeToMB(long sizeInBytes) {
        double sizeInMB = (double) sizeInBytes / (1024 * 1024);
        String formatted = String.format("%.2f", sizeInMB);
        // String转为Long
        return Double.parseDouble(formatted);
    }



    /**
     * 完整路径中截取文件名
     *
     * @param path 完整路径
     * @return 文件名
     */
    public String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 文件大小转换(kb)
     *
     * @param fileSize 文件大小
     * @return 文件大小(kb)
     */
    public Double convertFileSizeToKB(Long fileSize) {
        return fileSize / 1024.0;
    }
}
