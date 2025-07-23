package com.xhae.morak.service


@Service
class PortfolioService(
    private val designerRepository: DesignerRepository,
    private val portfolioRepository: PortfolioRepository,
    private val s3Service: S3Service
) {
    fun createPortfolio(
        designerId: Long,
        title: String,
        description: String,
        tags: String?,
        file: MultipartFile
    ): PortfolioDto {
        // 파일 체크
        val allowed = setOf("pdf", "jpg", "jpeg", "png", "webp", "svg")
        val ext = file.originalFilename?.substringAfterLast('.')?.lowercase() ?: ""
        require(ext in allowed) { "허용되지 않은 파일 형식입니다." }
        val s3Url = s3Service.upload(file, "portfolio/$designerId/")
        val designer = designerRepository.findById(designerId).orElseThrow()
        val portfolio = portfolioRepository.save(
            Portfolio(
                designer = designer,
                title = title,
                description = description,
                fileUrl = s3Url,
                tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
                createdAt = LocalDateTime.now()
            )
        )
        return PortfolioDto.of(portfolio)
    }

    fun getPortfolios(designerId: Long): List<PortfolioDto> =
        portfolioRepository.findAllByDesignerId(designerId)
            .map { PortfolioDto.of(it) }

    fun updatePortfolio(
        designerId: Long,
        portfolioId: Long,
        title: String?,
        description: String?,
        tags: String?,
        file: MultipartFile?
    ): PortfolioDto {
        val portfolio = portfolioRepository.findByIdAndDesignerId(portfolioId, designerId)
            ?: throw NoSuchElementException("포트폴리오를 찾을 수 없음")
        title?.let { portfolio.title = it }
        description?.let { portfolio.description = it }
        tags?.let { portfolio.tags = it.split(",").map { t -> t.trim() } }
        file?.let {
            val allowed = setOf("pdf", "jpg", "jpeg", "png", "webp", "svg")
            val ext = file.originalFilename?.substringAfterLast('.')?.lowercase() ?: ""
            require(ext in allowed) { "허용되지 않은 파일 형식입니다." }
            portfolio.fileUrl = s3Service.upload(file, "portfolio/$designerId/")
        }
        portfolioRepository.save(portfolio)
        return PortfolioDto.of(portfolio)
    }

    fun deletePortfolio(designerId: Long, portfolioId: Long) {
        val portfolio = portfolioRepository.findByIdAndDesignerId(portfolioId, designerId)
            ?: throw NoSuchElementException("포트폴리오를 찾을 수 없음")
        portfolioRepository.delete(portfolio)
    }
}
