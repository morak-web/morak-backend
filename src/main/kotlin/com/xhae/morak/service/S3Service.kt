package com.xhae.morak.service

import com.xhae.morak.util.S3Uploader
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class S3Service (
    private val s3Uploader: S3Uploader
){
    fun upload(file: MultipartFile): String {
        return s3Uploader.upload(file)
    }
}
