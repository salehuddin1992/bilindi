package com.example.model

enum class UserRole(val label: String) {
    STUDENT("Siswa"),
    TEACHER("Guru"),
    PRINCIPAL("Kepala Sekolah")
}

data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val username: String = "",
    val password: String = "123",
    val className: String? = null,
    val subject: String? = null,
    val avatarUrl: String,
    val coverUrl: String = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&q=80&w=1000",
    val bio: String = "Selalu semangat belajar hal baru setiap hari!",
    val location: String = "Jakarta",
    val school: String = "SMP Negeri sinombayuga",
    val badges: List<String> = listOf("Rajin", "Kreatif", "Juara Diskusi")
)

enum class PostType {
    LEARNING,
    ASSIGNMENT,
    STATUS
}

enum class PostStatus {
    APPROVED,
    PENDING
}

data class SectionData(
    val text: String = "",
    val fileUrl: String? = null,
    val fileType: String? = null, // "image", "video", "pdf"
    val fileName: String? = null,
    val embedUrl: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val pdfUrl: String? = null,
    val pdfName: String? = null,
    val pdfPageCount: Int = 4
)

data class StructuredLearningContent(
    val headerSection: SectionData = SectionData(),
    val pemantik: SectionData = SectionData(),
    val tujuan: SectionData = SectionData(),
    val inti: SectionData = SectionData(),
    val asesmen: SectionData = SectionData()
)

data class AssignmentDetail(
    val deadline: String = "Besok, 23:59",
    val targetClass: String = "VII-A",
    val attachmentName: String? = null,
    val attachmentType: String? = null,
    val attachmentUrl: String? = null
)

data class Post(
    val id: String,
    val authorId: String,
    val type: PostType,
    val status: PostStatus = PostStatus.APPROVED,
    val timestamp: String,
    val title: String? = null,
    val content: String? = null,
    val hashtag: String? = null,
    val imageUrl: String? = null,
    val targetClass: String? = null,
    val likes: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val structuredContent: StructuredLearningContent? = null,
    val assignmentDetail: AssignmentDetail? = null
)

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val timestamp: String = "Baru saja"
)

data class Story(
    val id: String,
    val authorId: String,
    val imageUrl: String,
    val caption: String = "",
    val timestamp: String = "Baru saja",
    val targetClass: String? = null
)

data class Submission(
    val id: String,
    val postId: String,
    val assignmentHashtag: String,
    val studentId: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: String,
    val grade: Int? = null,
    val feedback: String? = null,
    val isGraded: Boolean = false
)

data class ReelItem(
    val id: String,
    val authorId: String,
    val title: String,
    val subject: String,
    val description: String,
    val duration: String,
    val thumbnailUrl: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean = false
)

data class SchoolAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val isImportant: Boolean = false
)

data class ClassItem(
    val id: String,
    val name: String,
    val homeroomTeacher: String,
    val studentCount: Int,
    val subjectList: List<String>,
    val schedule: String
)

data class DirectMessage(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val text: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val imageUrl: String? = null
)
