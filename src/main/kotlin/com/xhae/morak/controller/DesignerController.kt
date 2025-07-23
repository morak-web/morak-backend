package com.xhae.morak.controller



@RestController
@RequestMapping("/api/designers")
class DesignerController(
    private val portfolioService: PortfolioService
) {
    // 8. 포트폴리오 등록 (이미지/PDF만)
    @PostMapping("/{designerId}/portfolios")
    fun registerPortfolio(
        @PathVariable designerId: Long,
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam(required = false) tags: String?, // "웹,SaaS,Figma"
        @RequestPart file: MultipartFile
    ): ResponseEntity<PortfolioDto> =
        ResponseEntity.ok(portfolioService.createPortfolio(designerId, title, description, tags, file))

    // 9-1. 내 포트폴리오 목록
    @GetMapping("/{designerId}/portfolios")
    fun getPortfolios(
        @PathVariable designerId: Long
    ): ResponseEntity<List<PortfolioDto>> =
        ResponseEntity.ok(portfolioService.getPortfolios(designerId))

    // 9-2. 포트폴리오 수정
    @PatchMapping("/{designerId}/portfolios/{portfolioId}")
    fun updatePortfolio(
        @PathVariable designerId: Long,
        @PathVariable portfolioId: Long,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) tags: String?,
        @RequestPart(required = false) file: MultipartFile?
    ): ResponseEntity<PortfolioDto> =
        ResponseEntity.ok(
            portfolioService.updatePortfolio(
                designerId, portfolioId, title, description, tags, file
            )
        )

    // 9-3. 포트폴리오 삭제
    @DeleteMapping("/{designerId}/portfolios/{portfolioId}")
    fun deletePortfolio(
        @PathVariable designerId: Long,
        @PathVariable portfolioId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        portfolioService.deletePortfolio(designerId, portfolioId)
        return ResponseEntity.ok(mapOf("success" to true))
    }
}
