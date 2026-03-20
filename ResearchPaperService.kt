package com.atrijahaldar.researchapi

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ResearchPaperService(
    private val repository: ResearchPaperRepository
) {

    // ── Validation ─────────────────────────────────────────────────────────────

    private val validStatuses = setOf("Published", "Review", "Draft")
    private val validImpacts  = setOf("High", "Medium", "Low")

    private fun validate(request: ResearchPaperRequest) {
        require(request.title.isNotBlank())  { "Title must not be blank" }
        require(request.author.isNotBlank()) { "Author must not be blank" }
        require(request.topic.isNotBlank())  { "Topic must not be blank" }
        require(request.citations >= 0)      { "Citations must be non-negative" }
        require(request.status in validStatuses) {
            "Status must be one of: ${validStatuses.joinToString()}"
        }
        require(request.impact in validImpacts) {
            "Impact must be one of: ${validImpacts.joinToString()}"
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getAll(): List<ResearchPaperResponse> =
        repository.findAll()
            .sortedByDescending { it.publishedDate }
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: Long): ResearchPaperResponse =
        repository.findById(id)
            .orElseThrow { NoSuchElementException("Paper not found: id=$id") }
            .toResponse()

    fun create(request: ResearchPaperRequest): ResearchPaperResponse {
        validate(request)
        return repository.save(request.toEntity()).toResponse()
    }

    fun update(id: Long, request: ResearchPaperRequest): ResearchPaperResponse {
        validate(request)
        val existing = repository.findById(id)
            .orElseThrow { NoSuchElementException("Paper not found: id=$id") }

        val updated = existing.copy(
            title         = request.title.trim(),
            author        = request.author.trim(),
            topic         = request.topic.trim(),
            publishedDate = request.publishedDate,
            citations     = request.citations.coerceAtLeast(0),
            status        = request.status,
            impact        = request.impact,
            abstract      = request.abstract?.trim(),
            doi           = request.doi?.trim()
        )
        return repository.save(updated).toResponse()
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Paper not found: id=$id")
        }
        repository.deleteById(id)
    }

    // ── Search & filter ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    fun search(query: String): List<ResearchPaperResponse> =
        repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            query, query
        ).sortedByDescending { it.publishedDate }
         .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getByTopic(topic: String): List<ResearchPaperResponse> =
        repository.findByTopicIgnoreCase(topic)
            .sortedByDescending { it.publishedDate }
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getByStatus(status: String): List<ResearchPaperResponse> =
        repository.findByStatusIgnoreCase(status)
            .sortedByDescending { it.publishedDate }
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getByImpact(impact: String): List<ResearchPaperResponse> =
        repository.findByImpactIgnoreCase(impact)
            .sortedByDescending { it.citations }
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun filter(
        topic:       String? = null,
        status:      String? = null,
        minCitations: Int?   = null
    ): List<ResearchPaperResponse> {
        var results = repository.findAll()

        if (!topic.isNullOrBlank())
            results = results.filter { it.topic.equals(topic, ignoreCase = true) }
        if (!status.isNullOrBlank())
            results = results.filter { it.status.equals(status, ignoreCase = true) }
        if (minCitations != null)
            results = results.filter { it.citations >= minCitations }

        return results
            .sortedByDescending { it.publishedDate }
            .map { it.toResponse() }
    }

    // ── Analytics ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getTopicStats(): List<TopicStats> =
        repository.getTopicStats()

    @Transactional(readOnly = true)
    fun getSummaryStats(): Map<String, Any> {
        val total         = repository.count()
        val totalCitations = repository.sumAllCitations() ?: 0L
        val published     = repository.countByStatus("Published")
        val review        = repository.countByStatus("Review")
        val draft         = repository.countByStatus("Draft")
        val topics        = repository.findDistinctTopics()

        return mapOf(
            "totalPapers"     to total,
            "totalCitations"  to totalCitations,
            "avgCitations"    to if (total > 0) totalCitations / total else 0,
            "publishedCount"  to published,
            "reviewCount"     to review,
            "draftCount"      to draft,
            "topicCount"      to topics.size,
            "topics"          to topics
        )
    }
}
