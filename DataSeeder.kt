package com.atrijahaldar.researchapi

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataSeeder(
    private val repository: ResearchPaperRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (repository.count() > 0) return  // Don't reseed if data exists

        val papers = listOf(
            ResearchPaper(title = "AI Safety and Alignment in Large Language Models",
                author = "Chen, L.", topic = "AI Safety",
                publishedDate = LocalDate.of(2024, 3, 15),
                citations = 142, status = "Published", impact = "High",
                abstract = "Examines alignment techniques for large-scale language models."),
            ResearchPaper(title = "Macroeconomic Forecasting Using Neural Networks",
                author = "Smith, J.", topic = "Economics",
                publishedDate = LocalDate.of(2024, 1, 22),
                citations = 87, status = "Published", impact = "Medium"),
            ResearchPaper(title = "Drug Resistance Mechanisms in Oncology",
                author = "Patel, R.", topic = "Healthcare",
                publishedDate = LocalDate.of(2024, 5, 1),
                citations = 203, status = "Published", impact = "High"),
            ResearchPaper(title = "Quantum Computing Applications in Cryptography",
                author = "Nakamura, Y.", topic = "Technology",
                publishedDate = LocalDate.of(2023, 11, 30),
                citations = 56, status = "Published", impact = "Medium"),
            ResearchPaper(title = "ESG Reporting Standards and Corporate Governance",
                author = "Williams, A.", topic = "Finance",
                publishedDate = LocalDate.of(2024, 2, 14),
                citations = 31, status = "Review", impact = "Low"),
            ResearchPaper(title = "Gene Editing CRISPR-Cas9 Clinical Trials",
                author = "Garcia, M.", topic = "Healthcare",
                publishedDate = LocalDate.of(2024, 4, 8),
                citations = 178, status = "Published", impact = "High"),
            ResearchPaper(title = "Distributed Systems Consistency Models",
                author = "Kim, S.", topic = "Technology",
                publishedDate = LocalDate.of(2023, 12, 5),
                citations = 94, status = "Published", impact = "Medium"),
            ResearchPaper(title = "Climate Risk Modelling for Financial Portfolios",
                author = "Brown, T.", topic = "Finance",
                publishedDate = LocalDate.of(2024, 3, 28),
                citations = 67, status = "Published", impact = "Medium"),
            ResearchPaper(title = "mRNA Vaccine Platform Efficacy Studies",
                author = "Johansson, E.", topic = "Healthcare",
                publishedDate = LocalDate.of(2024, 6, 1),
                citations = 312, status = "Published", impact = "High"),
            ResearchPaper(title = "Reinforcement Learning in Robotics Control",
                author = "Okafor, B.", topic = "AI Safety",
                publishedDate = LocalDate.of(2024, 1, 10),
                citations = 45, status = "Review", impact = "Medium"),
            ResearchPaper(title = "Central Bank Digital Currency Design Principles",
                author = "Martinez, C.", topic = "Finance",
                publishedDate = LocalDate.of(2023, 10, 18),
                citations = 129, status = "Published", impact = "High"),
            ResearchPaper(title = "Transformer Architecture Scaling Laws",
                author = "Thompson, D.", topic = "Technology",
                publishedDate = LocalDate.of(2024, 5, 15),
                citations = 267, status = "Published", impact = "High"),
            ResearchPaper(title = "Supply Chain Disruption Modelling Post-COVID",
                author = "Zhao, W.", topic = "Economics",
                publishedDate = LocalDate.of(2024, 1, 30),
                citations = 156, status = "Published", impact = "High"),
            ResearchPaper(title = "Explainability Methods for Black-Box ML Models",
                author = "Rossi, G.", topic = "AI Safety",
                publishedDate = LocalDate.of(2024, 6, 10),
                citations = 38, status = "Review", impact = "Medium"),
            ResearchPaper(title = "Personalised Medicine Genomic Biomarkers",
                author = "Hassan, N.", topic = "Healthcare",
                publishedDate = LocalDate.of(2024, 5, 28),
                citations = 241, status = "Published", impact = "High"),
        )

        repository.saveAll(papers)
        println("Seeded ${papers.size} research papers.")
    }
}
