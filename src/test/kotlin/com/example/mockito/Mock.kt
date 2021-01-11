package com.example.mockito

import com.nhaarman.mockito_kotlin.any
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.anyString

import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doReturn
import kotlin.random.Random


class MockTest {

    @Test
    fun `should lend a book`() {
        //
        val mockBookService = Mockito.mock(BookService::class.java)
        `when`(mockBookService.inStock(100)).thenReturn(true)
        val manager = LendBookManager(mockBookService)
        manager.checkout(100, 1)
        Mockito.verify(mockBookService).lend(100, 1)
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw IllegalStateException exception`() {
        val mockBookService = Mockito.mock(BookService::class.java)
        `when`(mockBookService.inStock(100)).thenReturn(false)
        val manager = LendBookManager(mockBookService)
        manager.checkout(100, 1)
    }
}

interface BookService {
    fun inStock(bookId: Int): Boolean
    fun lend(bookId: Int, memberId: Int)
}

class LendBookManager(private val bookService: BookService) {
    fun checkout(bookId: Int, memberId: Int) {
        if (bookService.inStock(bookId)) {
            bookService.lend(bookId, memberId)
        } else {
            throw IllegalStateException("Book is not available")
        }
    }
}