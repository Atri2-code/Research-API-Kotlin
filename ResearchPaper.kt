package com.atrijahaldar.researchapi

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

// ── Entity ────────────────────────────────────────────────────────────────────

@Entity
@Table(name = "research_papers")
data class ResearchPaper(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 500)
    val title: String,

    @Column(nullable = false, length = 200)
    val author: String,

    @Column(nullable = false, length = 100)
    val topic: String,

    @Column(nullable = false)
    val publishedDate: LocalDate,

    @Column(nullable = false)
    val citations: Int = 0,

    @Column(nullable = false, length = 50)
    val status: String,          // Published | Review | Draft

    @Column(nullable = false, length = 50)
    val impact: String,          // High | Medium | Low

    @Column(length = 2000)
    val abstract: String? = null,

    @Column(length = 200)
    val doi: String? = null
)

// ── DTOs ──────────────────────────────────────────────────────────────────────

data class ResearchPaperRequest(
    val title:         String,
    val author:        String,
    val topic:         String,
    val publishedDate: LocalDate,
    val citations:     Int     = 0,
    val status:        String  = "Draft",
    val impact:        String  = "Low",
    val abstract:      String? = null,
    val doi:           String? = null
)

data class ResearchPaperResponse(
    val id:            Long,
    val title:         String,
    val author:        String,
    val topic:         String,
    val publishedDate: LocalDate,
    val citations:     Int,
    val status:        String,
    val impact:        String,
    val abstract:      String?,
    val doi:           String?
)

data class TopicStats(
    val topic:         String,
    val paperCount:    Long,
    val totalCitations: Long,
    val avgCitations:  Double
)

data class ApiResponse<T>(
    val success: Boolean,
    val data:    T?,
    val message: String? = null,
    val total:   Int?    = null
)

// ── Extensions ────────────────────────────────────────────────────────────────

fun ResearchPaper.toResponse() = ResearchPaperResponse(
    id            = id,
    title         = title,
    author        = author,
    topic         = topic,
    publishedDate = publishedDate,
    citations     = citations,
    status        = status,
    impact        = impact,
    abstract      = abstract,
    doi           = doi
)

fun ResearchPaperRequest.toEntity() = ResearchPaper(
    title         = title.trim(),
    author        = author.trim(),
    topic         = topic.trim(),
    publishedDate = publishedDate,
    citations     = citations.coerceAtLeast(0),
    status        = status,
    impact        = impact,
    abstract      = abstract?.trim(),
    doi           = doi?.trim()
)

// ── Repository ────────────────────────────────────────────────────────────────

@Repository
interface ResearchPaperRepository : JpaRepository<ResearchPaper, Long> {

    fun findByTopicIgnoreCase(topic: String): List<ResearchPaper>

    fun findByStatusIgnoreCase(status: String): List<ResearchPaper>

    fun findByImpactIgnoreCase(impact: String): List<ResearchPaper>

    fun findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
        title: String,
        author: String
    ): List<ResearchPaper>

    fun findByTopicIgnoreCaseAndStatusIgnoreCase(
        topic: String,
        status: String
    ): List<ResearchPaper>

    fun findByCitationsGreaterThanEqual(minCitations: Int): List<ResearchPaper>

    @Query("SELECT DISTINCT r.topic FROM ResearchPaper r ORDER BY r.topic")
    fun findDistinctTopics(): List<String>

    @Query("""
        SELECT new com.atrijahaldar.researchapi.TopicStats(
            r.topic,
            COUNT(r),
            SUM(r.citations),
            AVG(r.citations)
        )
        FROM ResearchPaper r
        GROUP BY r.topic
        ORDER BY COUNT(r) DESC
    """)
    fun getTopicStats(): List<TopicStats>

    @Query("SELECT COUNT(r) FROM ResearchPaper r WHERE r.status = :status")
    fun countByStatus(status: String): Long

    @Query("SELECT SUM(r.citations) FROM ResearchPaper r")
    fun sumAllCitations(): Long?
}
