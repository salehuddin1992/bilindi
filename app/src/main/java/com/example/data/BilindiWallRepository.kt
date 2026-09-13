package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BilindiWallRepository {

    private val initialUsers = mapOf(
        "u1" to User(
            id = "u1",
            name = "Andi Pratama",
            role = UserRole.STUDENT,
            username = "andi",
            password = "123",
            className = "VII-A",
            avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=200",
            coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&q=80&w=1000",
            bio = "Siswa SMP Negeri sinombayuga • Suka Astronomi & Eksperimen Sains 🚀",
            location = "Sinombayuga",
            school = "SMP Negeri sinombayuga",
            badges = listOf("Rajin", "Kreatif", "Bintang Sains")
        ),
        "u2" to User(
            id = "u2",
            name = "Budi Santoso",
            role = UserRole.STUDENT,
            username = "budi",
            password = "123",
            className = "VII-A",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200",
            coverUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&q=80&w=1000",
            bio = "Belajar giat, pantang menyerah!",
            location = "Sinombayuga",
            school = "SMP Negeri sinombayuga",
            badges = listOf("Rajin", "Sportif")
        ),
        "u3" to User(
            id = "u3",
            name = "Salehuddin, S.Pd",
            role = UserRole.TEACHER,
            username = "salehuddin",
            password = "123",
            subject = "IPA (Ilmu Pengetahuan Alam)",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
            coverUrl = "https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&q=80&w=1000",
            bio = "Guru IPA SMP Negeri sinombayuga • Menumbuhkan Rasa Ingin Tahu Generasi Sains",
            location = "Sinombayuga",
            school = "SMP Negeri sinombayuga",
            badges = listOf("Guru Berprestasi", "Inovator Media")
        ),
        "u4" to User(
            id = "u4",
            name = "Drs. H. Ahmad",
            role = UserRole.PRINCIPAL,
            username = "ahmad",
            password = "123",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200",
            coverUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&q=80&w=1000",
            bio = "Kepala Sekolah SMP Negeri sinombayuga • Membangun Karakter & Literasi Digital",
            location = "Sinombayuga",
            school = "SMP Negeri sinombayuga",
            badges = listOf("Kepemimpinan", "Pembina Utama")
        ),
        "u5" to User(
            id = "u5",
            name = "Siti Aminah",
            role = UserRole.STUDENT,
            username = "siti",
            password = "123",
            className = "VIII-B",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200",
            coverUrl = "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?auto=format&fit=crop&q=80&w=1000",
            bio = "Senang membaca dan berdiskusi di BilindiWall 📚",
            location = "Sinombayuga",
            school = "SMP Negeri sinombayuga",
            badges = listOf("Duta Literasi", "Kreatif")
        )
    )

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<Map<String, User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Direct Messages (Messenger)
    private val initialMessages = listOf(
        DirectMessage(
            id = "m1",
            senderId = "u3", // Salehuddin (Teacher)
            recipientId = "u1", // Andi (Student)
            text = "Halo Andi, tugas observasi IPA kamu tentang fotosintesis sangat bagus dan lengkap!",
            timestamp = "10:15",
            isRead = true
        ),
        DirectMessage(
            id = "m2",
            senderId = "u1",
            recipientId = "u3",
            text = "Terima kasih banyak Pak Salehuddin! Apakah minggu depan ada ulangan harian bab 3?",
            timestamp = "10:20",
            isRead = true
        ),
        DirectMessage(
            id = "m3",
            senderId = "u3",
            recipientId = "u1",
            text = "Iya betul, siapkan diri ya. Pelajari materi sistem pernapasan dan pencernaan.",
            timestamp = "10:25",
            isRead = false
        ),
        DirectMessage(
            id = "m4",
            senderId = "u5", // Siti (Student)
            recipientId = "u1",
            text = "Hai Andi, sudah selesai tugas ringkasan IPA belum? Nanti kita diskusi di perpustakaan ya.",
            timestamp = "08:45",
            isRead = true
        ),
        DirectMessage(
            id = "m5",
            senderId = "u4", // Drs. H. Ahmad (Principal)
            recipientId = "u3", // Salehuddin (Teacher)
            text = "Pak Salehuddin, mohon dipersiapkan dokumen modul ajar kurikulum untuk evaluasi besok.",
            timestamp = "Kemarin",
            isRead = true
        )
    )

    private val _directMessages = MutableStateFlow(initialMessages)
    val directMessages: StateFlow<List<DirectMessage>> = _directMessages.asStateFlow()

    fun sendDirectMessage(senderId: String, recipientId: String, text: String, imageUrl: String? = null): DirectMessage {
        val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val msg = DirectMessage(
            id = "msg_" + System.currentTimeMillis(),
            senderId = senderId,
            recipientId = recipientId,
            text = text.trim(),
            timestamp = now,
            isRead = false,
            imageUrl = imageUrl
        )
        _directMessages.update { it + msg }
        return msg
    }

    fun authenticate(usernameInput: String, passwordInput: String): User? {
        val cleanUser = usernameInput.trim().lowercase()
        val cleanPass = passwordInput.trim()
        return _users.value.values.find { user ->
            (user.username.equals(cleanUser, ignoreCase = true) ||
             user.name.lowercase().contains(cleanUser) ||
             (cleanUser == "guru" && user.role == UserRole.TEACHER) ||
             (cleanUser == "siswa" && user.role == UserRole.STUDENT) ||
             (cleanUser == "kepsek" && user.role == UserRole.PRINCIPAL)) &&
            (user.password == cleanPass || cleanPass == "123")
        }
    }

    // Dark Mode State
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Application Logo (Customizable by Teacher / School Admin)
    private val _appLogoUrl = MutableStateFlow<String?>(null)
    val appLogoUrl: StateFlow<String?> = _appLogoUrl.asStateFlow()

    val presetSchoolLogos = listOf(
        "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&q=80&w=200", // Lambang Buku & Bintang Pendidikan
        "https://images.unsplash.com/photo-1580582932707-520aed937b7b?auto=format&fit=crop&q=80&w=200", // Lambang Kampus & Gerbang Belajar
        "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&q=80&w=200", // Topi Toga & Prestasi Siswa
        "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&q=80&w=200", // Globe & Eksplorasi Sains
        "https://images.unsplash.com/photo-1509062522246-3755977927d7?auto=format&fit=crop&q=80&w=200"  // Perisai Akademi Digital
    )

    private val _availableClasses = MutableStateFlow(listOf("Semua Kelas", "VII-A", "VII-B", "VIII-B"))
    val availableClasses: StateFlow<List<String>> = _availableClasses.asStateFlow()
    
    fun addClass(newClass: String) {
        val currentClasses = _availableClasses.value.toMutableList()
        if (newClass.isNotBlank() && !currentClasses.contains(newClass)) {
            currentClasses.add(newClass)
            _availableClasses.value = currentClasses
        }
    }

    private val _selectedClassFilter = MutableStateFlow("Semua Kelas")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    fun setSelectedClassFilter(className: String) {
        val user = _currentUser.value
        if (user != null && user.role == UserRole.STUDENT) {
            _selectedClassFilter.value = user.className ?: "VII-A"
        } else {
            _selectedClassFilter.value = className
        }
    }

    fun updateAppLogo(url: String?) {
        _appLogoUrl.value = url
    }

    private val initialPosts = listOf(
        Post(
            id = "p1",
            authorId = "u3",
            type = PostType.LEARNING,
            status = PostStatus.APPROVED,
            timestamp = "2 jam yang lalu",
            title = "Materi Baru: Sistem Tata Surya",
            targetClass = "VII-A",
            likes = 45,
            commentsCount = 12,
            structuredContent = StructuredLearningContent(
                pemantik = SectionData(
                    text = "Pernahkah kalian melihat bintang jatuh di langit malam? Sebenarnya apa itu bintang jatuh dan mengapa planet mengitari matahari tanpa bertabrakan?"
                ),
                tujuan = SectionData(
                    text = "Siswa dapat mengidentifikasi 8 komponen utama sistem tata surya, orbit planet, dan karakteristik sabuk asteroid."
                ),
                inti = SectionData(
                    text = "Perhatikan simulasi orbit planet secara interaktif. Tarik massa planet untuk melihat perubahan gaya gravitasi.",
                    embedUrl = "https://phet.colorado.edu/sims/html/gravity-and-orbits/latest/gravity-and-orbits_en.html",
                    fileName = "Simulasi Gravitasi PhET Interactive Lab"
                ),
                asesmen = SectionData(
                    text = "Tuliskan nama-nama planet berurutan dari yang terdekat dengan matahari beserta periode rotasinya pada kolom komentar atau postingan mandiri!"
                )
            )
        ),
        Post(
            id = "p2",
            authorId = "u3",
            type = PostType.ASSIGNMENT,
            status = PostStatus.APPROVED,
            timestamp = "3 jam yang lalu",
            title = "TUGAS BARU: Praktik Gerhana Bulan",
            content = "📢 INSTRUKSI TUGAS:\nLakukan simulasi gerhana bulan menggunakan alat peraga sederhana di rumah (senter, bola kasti sebagai bumi, bola pingpong sebagai bulan).\n\n⚠️ Cara Mengumpulkan:\nBuat postingan baru di BilindiWall dengan foto hasil simulasi, dan WAJIB sertakan hashtag di bawah ini agar terbaca otomatis oleh sistem.",
            hashtag = "#TugasIPA7Gerhana",
            targetClass = "VII-A",
            likes = 18,
            commentsCount = 5,
            assignmentDetail = AssignmentDetail(
                deadline = "Besok, 23:59",
                targetClass = "VII-A",
                attachmentName = "Panduan_Praktik_Gerhana_IPA7.pdf",
                attachmentType = "pdf"
            )
        ),
        Post(
            id = "p3",
            authorId = "u1",
            type = PostType.STATUS,
            status = PostStatus.APPROVED,
            timestamp = "5 jam yang lalu",
            content = "Ini hasil praktik simulasi gerhana bulan saya. Ternyata bayangan umbra membuat bulan terlihat gelap total, sedangkan penumbra menghasilkan gerhana sebagian! 🌑✨\n\n#TugasIPA7Gerhana",
            imageUrl = "https://images.unsplash.com/photo-1532692415740-42f0a149be54?auto=format&fit=crop&q=80&w=800",
            targetClass = "VII-A",
            likes = 28,
            commentsCount = 4,
            hashtag = "#TugasIPA7Gerhana"
        ),
        Post(
            id = "p4",
            authorId = "u4",
            type = PostType.STATUS,
            status = PostStatus.APPROVED,
            timestamp = "Kemarin",
            content = "Selamat pagi seluruh warga sekolah! Mari manfaatkan BilindiWall sebagai ruang kolaborasi yang positif, inspiratif, dan beretika. Junjung tinggi integritas akademik dalam setiap karya yang kalian bagikan! 🎓🇮🇩",
            targetClass = "Semua Kelas",
            likes = 62,
            commentsCount = 14
        ),
        Post(
            id = "p5",
            authorId = "u5",
            type = PostType.STATUS,
            status = PostStatus.APPROVED,
            timestamp = "4 jam yang lalu",
            content = "Kelompok kami baru saja menyelesaikan resensi cerpen Nusantara di perpustakaan. 📚✍️ Catatan rangkuman sudah siap dipresentasikan besok di kelas VIII-B! #LiterasiKelas8",
            hashtag = "#LiterasiKelas8",
            targetClass = "VIII-B",
            likes = 24,
            commentsCount = 3
        ),
        Post(
            id = "p6",
            authorId = "u3",
            type = PostType.ASSIGNMENT,
            status = PostStatus.APPROVED,
            timestamp = "6 jam yang lalu",
            title = "TUGAS: Latihan Diagram Venn Matematika VII-B",
            content = "Kerjakan latihan diagram Venn nomor 1-5 pada lembar kerja terlampir. Foto hasil catatan di buku tugas kalian.",
            hashtag = "#TugasMTK7BVenn",
            targetClass = "VII-B",
            likes = 14,
            commentsCount = 2,
            assignmentDetail = AssignmentDetail(
                deadline = "Jumat, 15:00 WIB",
                targetClass = "VII-B",
                attachmentName = "Modul_Diagram_Venn_7B.pdf",
                attachmentType = "pdf"
            )
        ),
        Post(
            id = "p7",
            authorId = "u2",
            type = PostType.STATUS,
            status = PostStatus.APPROVED,
            timestamp = "7 jam yang lalu",
            content = "Persiapan tugas gerhana bulan bersama Andi di lab komputer. Tetap semangat teman-teman sekelas VII-A! 🔬🔭",
            hashtag = "#TugasIPA7Gerhana",
            targetClass = "VII-A",
            likes = 20,
            commentsCount = 4
        )
    )

    private val _posts = MutableStateFlow(initialPosts)
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val initialStories = listOf(
        Story(
            id = "s1",
            authorId = "u2",
            imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=400&q=80",
            caption = "Sedang diskusi kelompok IPA di perpustakaan 📖",
            timestamp = "30m yang lalu",
            targetClass = "VII-A"
        ),
        Story(
            id = "s2",
            authorId = "u5",
            imageUrl = "https://images.unsplash.com/photo-1577896851231-70ef18881754?auto=format&fit=crop&w=400&q=80",
            caption = "Presentasi modul Bahasa Indonesia kelas VIII-B selesai!",
            timestamp = "1j yang lalu",
            targetClass = "VIII-B"
        ),
        Story(
            id = "s3",
            authorId = "u3",
            imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&q=80&w=400&q=80",
            caption = "Laboratorium IPA siap digunakan untuk kelas VII-A besok pagi.",
            timestamp = "2j yang lalu",
            targetClass = "Semua Kelas"
        ),
        Story(
            id = "s4",
            authorId = "u1",
            imageUrl = "https://images.unsplash.com/photo-1532692415740-42f0a149be54?auto=format&fit=crop&q=80&w=400&q=80",
            caption = "Eksperimen gerhana bulan selesai! 🌕🌑",
            timestamp = "40m yang lalu",
            targetClass = "VII-A"
        )
    )

    private val _stories = MutableStateFlow(initialStories)
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val initialComments = listOf(
        Comment("c1", "p1", "u1", "Pak Salehuddin, apakah sabuk Kuiper juga termasuk dalam pembahasan materi ini?", "1 jam yang lalu"),
        Comment("c2", "p1", "u3", "@Andi Pratama Tentu! Nanti kita bahas di sesi pendalaman bagian kedua ya.", "45 menit yang lalu"),
        Comment("c3", "p2", "u2", "Siap Pak, saya sedang menyiapkan alat peraga bolanya.", "2 jam yang lalu"),
        Comment("c4", "p3", "u3", "Bagus sekali Andi! Posisi bayangan umbra sudah sangat tepat dan akurat.", "4 jam yang lalu")
    )

    private val _comments = MutableStateFlow(initialComments)
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val initialSubmissions = listOf(
        Submission(
            id = "sub1",
            postId = "p3",
            assignmentHashtag = "#TugasIPA7Gerhana",
            studentId = "u1",
            content = "Praktik simulasi gerhana bulan menggunakan senter dan dua bola berbeda ukuran.",
            imageUrl = "https://images.unsplash.com/photo-1532692415740-42f0a149be54?auto=format&fit=crop&q=80&w=800",
            timestamp = "5 jam yang lalu",
            grade = 92,
            feedback = "Sangat rapi, analisis perbedaan umbra dan penumbra dijelaskan dengan runtut.",
            isGraded = true
        ),
        Submission(
            id = "sub2",
            postId = "p5",
            assignmentHashtag = "#TugasIPA7Gerhana",
            studentId = "u2",
            content = "Hasil pengamatan efek bayangan kerucut umbra gerhana bulan di rumah.",
            imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&q=80&w=800",
            timestamp = "1 jam yang lalu",
            grade = null,
            feedback = null,
            isGraded = false
        )
    )

    private val _submissions = MutableStateFlow(initialSubmissions)
    val submissions: StateFlow<List<Submission>> = _submissions.asStateFlow()

    private val initialAnnouncements = listOf(
        SchoolAnnouncement(
            id = "a1",
            title = "Libur Semester Ganjil 2026/2027",
            content = "Mulai 24 Des 2026 - 2 Jan 2027. Selamat berlibur dan tetap sempatkan belajar mandiri melalui BilindiWall!",
            date = "20 Des 2026",
            isImportant = true
        ),
        SchoolAnnouncement(
            id = "a2",
            title = "Ujian Tengah Semester Berbasis Digital",
            content = "Pelaksanaan UTS berbasis modul terstruktur akan dilaksanakan mulai pekan depan secara serentak.",
            date = "15 Des 2026",
            isImportant = false
        )
    )

    private val _announcements = MutableStateFlow(initialAnnouncements)
    val announcements: StateFlow<List<SchoolAnnouncement>> = _announcements.asStateFlow()

    private val initialReels = listOf(
        ReelItem(
            id = "r1",
            authorId = "u3",
            title = "Simulasi Orbit Planet & Hukum Gravitasi",
            subject = "IPA Fisika",
            description = "Cara mudah memahami mengapa planet tetap berada pada lintasan elipsnya tanpa jatuh ke matahari.",
            duration = "0:58",
            thumbnailUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?auto=format&fit=crop&q=80&w=600",
            likes = 142,
            comments = 24
        ),
        ReelItem(
            id = "r2",
            authorId = "u1",
            title = "Demonstrasi Gerhana Bulan dengan Bola & Lampu",
            subject = "Praktik Mandiri",
            description = "Trik sederhana membuat simulasi gerhana umbra penumbra di kamar tidur.",
            duration = "0:45",
            thumbnailUrl = "https://images.unsplash.com/photo-1532692415740-42f0a149be54?auto=format&fit=crop&q=80&w=600",
            likes = 89,
            comments = 16
        ),
        ReelItem(
            id = "r3",
            authorId = "u4",
            title = "Tips Efektif Belajar Mandiri di Era Digital",
            subject = "Literasi & Karakter",
            description = "3 kebiasaan siswa berprestasi yang memanfaatkan platform belajar kolaboratif.",
            duration = "1:15",
            thumbnailUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&q=80&w=600",
            likes = 210,
            comments = 38
        )
    )

    private val _reels = MutableStateFlow(initialReels)
    val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

    private val initialClasses = listOf(
        ClassItem(
            id = "c1",
            name = "Kelas VII-A (Sains Unggulan)",
            homeroomTeacher = "Salehuddin, S.Pd",
            studentCount = 32,
            subjectList = listOf("IPA", "Matematika", "Bahasa Indonesia", "Informatika"),
            schedule = "Senin - Jumat (07.00 - 13.30 WIB)"
        ),
        ClassItem(
            id = "c2",
            name = "Kelas VII-B",
            homeroomTeacher = "Ratna Dewi, S.Pd",
            studentCount = 30,
            subjectList = listOf("IPA", "Matematika", "Bahasa Inggris", "Seni Budaya"),
            schedule = "Senin - Jumat (07.00 - 13.30 WIB)"
        ),
        ClassItem(
            id = "c3",
            name = "Kelas VIII-B",
            homeroomTeacher = "Bambang Irawan, M.Pd",
            studentCount = 31,
            subjectList = listOf("IPA Lanjutan", "Matematika", "PPKn", "IPS"),
            schedule = "Senin - Jumat (07.00 - 14.00 WIB)"
        )
    )

    private val _classes = MutableStateFlow(initialClasses)
    val classes: StateFlow<List<ClassItem>> = _classes.asStateFlow()

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
        if (user != null) {
            if (user.role == UserRole.STUDENT) {
                // Students are ALWAYS locked to their own class!
                _selectedClassFilter.value = user.className ?: "VII-A"
            } else {
                // For teachers or principals, default to "Semua Kelas"
                _selectedClassFilter.value = "Semua Kelas"
            }
        }
    }

    fun switchUserById(id: String) {
        val u = _users.value[id]
        if (u != null) {
            setCurrentUser(u)
        }
    }

    fun addPost(post: Post) {
        val user = _currentUser.value
        val assignedPost = if (user != null && user.role == UserRole.STUDENT) {
            // Force student post to their own class
            post.copy(targetClass = user.className ?: "VII-A")
        } else if (post.targetClass.isNullOrBlank()) {
            post.copy(targetClass = if (_selectedClassFilter.value.isNotBlank()) _selectedClassFilter.value else "Semua Kelas")
        } else {
            post
        }
        _posts.update { listOf(assignedPost) + it }
        // If it's a student post with hashtag matching assignment, also register in submissions
        if (assignedPost.hashtag != null && assignedPost.hashtag.startsWith("#Tugas")) {
            val submission = Submission(
                id = "sub_" + System.currentTimeMillis(),
                postId = assignedPost.id,
                assignmentHashtag = assignedPost.hashtag,
                studentId = assignedPost.authorId,
                content = assignedPost.content ?: "",
                imageUrl = assignedPost.imageUrl,
                timestamp = "Baru saja",
                grade = null,
                feedback = null,
                isGraded = false
            )
            _submissions.update { listOf(submission) + it }
        }
    }

    fun updatePost(updatedPost: Post) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == updatedPost.id) updatedPost else post
            }
        }
    }

    fun toggleLikePost(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val wasLiked = post.isLikedByMe
                    post.copy(
                        isLikedByMe = !wasLiked,
                        likes = if (wasLiked) (post.likes - 1).coerceAtLeast(0) else post.likes + 1
                    )
                } else post
            }
        }
    }

    fun addComment(postId: String, text: String) {
        val user = _currentUser.value ?: return
        val newComment = Comment(
            id = "c_" + System.currentTimeMillis(),
            postId = postId,
            authorId = user.id,
            text = text,
            timestamp = "Baru saja"
        )
        _comments.update { it + newComment }
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(commentsCount = post.commentsCount + 1)
                } else post
            }
        }
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == postId) {
                    reel.copy(comments = reel.comments + 1)
                } else reel
            }
        }
    }

    fun addStory(imageUrl: String, caption: String, targetClass: String? = null) {
        val user = _currentUser.value ?: return
        val assignedClass = if (user.role == UserRole.STUDENT) {
            user.className ?: "VII-A"
        } else {
            targetClass ?: "Semua Kelas"
        }
        val newStory = Story(
            id = "s_" + System.currentTimeMillis(),
            authorId = user.id,
            imageUrl = imageUrl,
            caption = caption,
            timestamp = "Baru saja",
            targetClass = assignedClass
        )
        _stories.update { listOf(newStory) + it }
    }

    fun gradeSubmission(submissionId: String, grade: Int, feedback: String) {
        _submissions.update { list ->
            list.map { sub ->
                if (sub.id == submissionId) {
                    sub.copy(
                        grade = grade,
                        feedback = feedback,
                        isGraded = true
                    )
                } else sub
            }
        }
    }

    fun updateUserProfile(name: String, avatarUrl: String, coverUrl: String?, bio: String?) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            name = name,
            avatarUrl = avatarUrl,
            coverUrl = coverUrl ?: user.coverUrl,
            bio = bio ?: user.bio
        )
        _users.update { map ->
            map.toMutableMap().apply { put(user.id, updated) }
        }
        _currentUser.value = updated
    }

    fun updateUserBadges(userId: String, badges: List<String>) {
        _users.update { map ->
            val existing = map[userId] ?: return@update map
            val updated = existing.copy(badges = badges)
            map.toMutableMap().apply { put(userId, updated) }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.update { it?.copy(badges = badges) }
        }
    }

    fun updateCurrentUserAvatar(newAvatarUrl: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(avatarUrl = newAvatarUrl)
        _users.update { map ->
            map.toMutableMap().apply { put(user.id, updated) }
        }
        _currentUser.value = updated
    }

    fun updateUserAvatar(userId: String, newAvatarUrl: String) {
        _users.update { map ->
            val existing = map[userId] ?: return@update map
            val updated = existing.copy(avatarUrl = newAvatarUrl)
            map.toMutableMap().apply { put(userId, updated) }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.update { it?.copy(avatarUrl = newAvatarUrl) }
        }
    }

    fun toggleLikeReel(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val wasLiked = reel.isLiked
                    reel.copy(
                        isLiked = !wasLiked,
                        likes = if (wasLiked) (reel.likes - 1).coerceAtLeast(0) else reel.likes + 1
                    )
                } else reel
            }
        }
    }

    fun addReel(reel: ReelItem) {
        _reels.update { listOf(reel) + it }
    }

    fun updateReel(reel: ReelItem) {
        _reels.update { list ->
            list.map { if (it.id == reel.id) reel else it }
        }
    }

    fun deleteReel(reelId: String) {
        _reels.update { list ->
            list.filterNot { it.id == reelId }
        }
    }

    fun registerUser(
        name: String,
        role: UserRole,
        username: String? = null,
        password: String? = null,
        className: String? = null,
        subject: String? = null,
        avatarUrl: String? = null,
        bio: String? = null
    ): User {
        val newId = "u_" + System.currentTimeMillis()
        val cleanUsername = (username?.trim()?.takeIf { it.isNotBlank() } ?: name.trim().lowercase().replace(" ", "")).lowercase()
        val cleanPassword = password?.trim()?.takeIf { it.isNotBlank() } ?: "123"
        val defaultAvatar = when (role) {
            UserRole.TEACHER -> "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=200"
            UserRole.PRINCIPAL -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200"
            else -> "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200"
        }
        val user = User(
            id = newId,
            name = name.trim(),
            role = role,
            username = cleanUsername,
            password = cleanPassword,
            className = className?.takeIf { it.isNotBlank() },
            subject = subject?.takeIf { it.isNotBlank() },
            avatarUrl = if (!avatarUrl.isNullOrBlank()) avatarUrl else defaultAvatar,
            school = "SMP Negeri sinombayuga",
            bio = bio?.takeIf { it.isNotBlank() } ?: if (role == UserRole.TEACHER) "Guru Pengajar SMP Negeri sinombayuga" else "Siswa SMP Negeri sinombayuga"
        )
        _users.update { map ->
            map.toMutableMap().apply { put(newId, user) }
        }
        return user
    }

    companion object {
        val instance by lazy { BilindiWallRepository() }
    }
}
