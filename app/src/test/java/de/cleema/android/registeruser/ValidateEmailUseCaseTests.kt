package de.cleema.android.registeruser

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test

class ValidateEmailUseCaseTests {
    @Test
    fun `It validates an invalid email`() {
        val invalid = listOf("", "     ", "hi", "@test.de", "test@.de", "hi@there,com")
        invalid.forEach {
            assertNull(ValidateEmailUseCase()(it))
        }
    }

    @Test
    fun `It validates valid email`() {
        val validator = ValidateEmailUseCase()
        assertEquals("hi@there.com", validator("hi@there.com"))
        assertEquals("hi@there.com", validator("   hi@there.com  "))
        assertEquals("123@127.0.0.1", validator("123@127.0.0.1"))
        assertEquals("mail_with@domäin.com", validator("mail_with@domäin.com"))
    }
}
