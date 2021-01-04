package com.example.mockito

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class KotlinLikeExamples {

    @Test
    fun `should lend a book`() {
        val mockBookService : BookService = mock()
        whenever(mockBookService.inStock(100)).thenReturn(true)
        val manager = LendBookManager(mockBookService)
        manager.checkout(100, 1)
        verify(mockBookService).lend(100, 1)
    }

}