package com.atrijahaldar.researchapi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = ["http://localhost:3000"])  // Allow React dev server
class ResearchPaperController(
    private val service: ResearchPaperService
) {

    // ── GET all ───────────────────────────────────────────────────────────────

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<ResearchPaperResponse>>> {
        val papers = service.getAll()
        return ResponseEntity.ok(
            ApiResponse(success = true, data = papers, total = papers.size)
        )
    }

    // ── GET by id ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ApiResponse<ResearchPaperResponse>> {
        val paper = service.getById(id)
        return ResponseEntity.ok(ApiResponse(success = true, data = paper))
    }

    // ── POST create ───────────────────────────────────────────────────────────

    @PostMapping
    fun create(
        @RequestBody request: ResearchPaperRequest
    ): ResponseEntity<ApiResponse<ResearchPaperResponse>> {
        val created = service.create(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse(success = true, data = created))
    }

    // ── PUT update ────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: ResearchPaperRequest
    ): ResponseEntity<ApiResponse<ResearchPaperResponse>> {
        val updated = service.update(id, request)
        return ResponseEntity.ok(ApiResponse(success = true, data = updated))
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        service.delete(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = null, message = "Paper deleted")
        )
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @GetMapping("/search")
    fun search(
        @RequestParam q: String
    ): ResponseEntity<ApiResponse<List<ResearchPaperResponse>>> {
        val results = service.search(q)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = results, total = results.size)
        )
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) topic:        String?,
        @RequestParam(required = false) status:       String?,
        @RequestParam(required = false) minCitations: Int?
    ): ResponseEntity<ApiResponse<List<ResearchPaperResponse>>> {
        val results = service.filter(topic, status, minCitations)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = results, total = results.size)
        )
    }

    // ── Topic ─────────────────────────────────────────────────────────────────

    @GetMapping("/topic/{topic}")
    fun getByTopic(
        @PathVariable topic: String
    ): ResponseEntity<ApiResponse<List<ResearchPaperResponse>>> {
        val results = service.getByTopic(topic)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = results, total = results.size)
        )
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    @GetMapping("/stats/topics")
    fun getTopicStats(): ResponseEntity<ApiResponse<List<TopicStats>>> {
        val stats = service.getTopicStats()
        return ResponseEntity.ok(ApiResponse(success = true, data = stats))
    }

    @GetMapping("/stats/summary")
    fun getSummaryStats(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val stats = service.getSummaryStats()
        return ResponseEntity.ok(ApiResponse(success = true, data = stats))
    }
}

// ── Global exception handler ──────────────────────────────────────────────────

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse(success = false, data = null, message = ex.message))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(success = false, data = null, message = ex.message))

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(success = false, data = null,
                              message = "Internal server error: ${ex.message}"))
}
