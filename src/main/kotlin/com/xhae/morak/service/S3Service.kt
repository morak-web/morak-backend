package com.xhae.morak.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class S3Service {
    fun upload(file: MultipartFile, dir: String): String {
        // 실제 환경에선 AWS S3 SDK 등으로 구현 필요
        // 여기서는 가짜 URL 생성 : TODO
        val fileName = file.originalFilename ?: "file"
        return "https://s3-bucket.amazonaws.com/$dir$fileName"
    }
}
