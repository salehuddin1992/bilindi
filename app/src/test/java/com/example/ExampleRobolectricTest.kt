package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BilindiWall", appName)
  }

  @Test
  fun `verify avatar update for student and teacher`() {
    val repository = com.example.data.BilindiWallRepository.instance
    val student = repository.users.value["u1"]
    if (student != null) {
      repository.setCurrentUser(student)
    }
    val newAvatar = "content://media/external/images/media/999"
    repository.updateCurrentUserAvatar(newAvatar)
    assertEquals(newAvatar, repository.currentUser.value?.avatarUrl)
  }

  @Test
  fun `verify student is locked to their class while teacher defaults to all classes`() {
    val repository = com.example.data.BilindiWallRepository.instance
    val student = repository.users.value["u1"]!! // VII-A
    repository.setCurrentUser(student)
    assertEquals("VII-A", repository.selectedClassFilter.value)

    val teacher = repository.users.value["u3"]!! // Teacher
    repository.setCurrentUser(teacher)
    assertEquals("Semua Kelas", repository.selectedClassFilter.value)

    repository.setSelectedClassFilter("VII-B")
    assertEquals("VII-B", repository.selectedClassFilter.value)
  }

  @Test
  fun `verify teacher can update application logo`() {
    val repository = com.example.data.BilindiWallRepository.instance
    val customLogo = "content://media/external/images/media/school_logo_123"
    repository.updateAppLogo(customLogo)
    assertEquals(customLogo, repository.appLogoUrl.value)

    // Verify reset to default
    repository.updateAppLogo(null)
    assertEquals(null, repository.appLogoUrl.value)
  }
}
