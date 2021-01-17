package com.example.mockito.mocks

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doCallRealMethod
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.withSettings
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.lang.Exception
import kotlin.random.Random

class RandomCasesTest {

    @Mock
    private lateinit var mockList: MutableList<Long>

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `configure simple return behavior for mock`() {
        // Arrange
        `when`(mockList.add(any())).thenReturn(false)
        // Act
        val added = mockList.add(Random.nextLong())
        // Assert
        Assert.assertEquals(added, false)
    }

    @Test
    fun `configure return behavior for mock in an alternative way`() {
        // Arrange
        Mockito.doReturn(false).`when`(mockList).add(ArgumentMatchers.anyLong())
        // Act
        val added = mockList.add(Random.nextLong())
        // Assert
        Assert.assertEquals(added, false)
    }

    @Test(expected = IllegalStateException::class)
    fun `configure mock to throw an exception on a method call`() {
        // Arrange
        `when`(mockList.add(any())).thenThrow(IllegalStateException::class.java)
        // Act
        mockList.add(Random.nextLong())
    }

    @Test(expected = NullPointerException::class)
    fun `configure the behavior of a method with void return type – to throw an exception`() {
        // Arrange
        doThrow(NullPointerException::class.java).`when`(mockList).clear()
        // Act
        mockList.clear()
    }

    @Test
    fun `configure the behavior of a method with void return type`() {
        // Arrange
        doNothing().`when`(mockList).add(anyInt(), anyLong())
        // Act
        mockList.add(0, 1L)
        // Assert
        verify(mockList, times(1)).add(0, 1L)
    }

    /**
     * doNothing() is Mockito's default behavior for void methods!
     */
    @Test
    fun `configure the behavior of a method with void return type - default behavior`() {
        // Act
        mockList.add(0, 1L)
        // Assert
        verify(mockList, times(1)).add(0, 1L)
    }

    @Test(expected = Exception::class)
    fun `configure the behavior of a method with void return type - throw default exception`() {
        // Arrange
        val myNullableMockList = mock(MyList::class.java)
        doThrow().`when`(myNullableMockList).add(anyInt(), isNull())
        // Act
        myNullableMockList.add(0, null)
    }

    /**
     * One reason to override the default behavior with doNothing() is to capture arguments.
     */
    @Test
    fun `configure the behavior of a method with void return type - capturing`() {
        // Arrange
        val valueCapture = ArgumentCaptor.forClass(Long::class.java)
        doNothing().`when`(mockList).add(anyInt(), valueCapture.capture())
        // Act
        val entry = Random.nextLong()
        mockList.add(0, entry)
        // Assert
        Assert.assertEquals(entry, valueCapture.value)
    }

    /**
     * Performing complex behavior as part of the response
     */
    @Test
    fun `configure the behavior of a method with void return type - answering`() {
        // Arrange
        val expectedIndex = 0
        val expectedValue = 12L
        doAnswer {
            val index = it.arguments[0]
            val value = it.arguments[1]
            // Assert
            Assert.assertEquals(index, expectedIndex)
            Assert.assertEquals(value, expectedValue)
        }.`when`(mockList).add(anyInt(), anyLong())
        // Act
        mockList.add(expectedIndex, expectedValue)
    }

    /**
     * Partial mocks
     */
    @Test
    fun `configure the behavior of a method with void return type - calling real method`() {
        // Arrange
        val myMockList = mock(MyList::class.java)
        doCallRealMethod().`when`(myMockList).size
        // Act
        myMockList.size
        // Assert
        verify(myMockList, times(1)).size
    }

    @Test(expected = IllegalStateException::class)
    fun `configure the behavior of multiple calls`() {
        // Arrange
        `when`(mockList.add(any()))
            .thenReturn(false)
            .thenThrow(IllegalStateException::class.java)
        // Act
        mockList.add(Random.nextLong())
        mockList.add(Random.nextLong())
    }

    @Test(expected = NullPointerException::class)
    fun `configure the behavior of a spy`() {
        // Arrange
        val listInstance = mutableListOf<Long>()
        val listSpy = Mockito.spy(listInstance)
        doThrow(NullPointerException::class.java).`when`(listSpy).size
        // Act
        listSpy.size
    }

    @Test
    fun `mocking with mock names`() {
        // Arrange
        val myNamedMockList = mock(MyList::class.java, "myMockList")
        `when`(myNamedMockList.add(any())).thenReturn(false)
        // Assert
        verify(myNamedMockList, times(2)).add(any())
    }

    /**
     * Allows us to have a behavior without stubbing. Not commonly needed, useful for legacy systems
     */
    @Test
    fun `mocking with default answer`() {
        // Arrange
        val myAnsweredMockList = mock(MyList::class.java, CustomAnswer())
        // Act
        val added = myAnsweredMockList.add(Random.nextLong())
        // Assert
        verify(myAnsweredMockList).add(any())
        Assert.assertEquals(added, false)
    }

    @Test
    fun `mocking With MockSettings`() {
        // Arrange
        val customSettings = withSettings().defaultAnswer(CustomAnswer())
        val myCustomSettingsMockList = mock(MyList::class.java, customSettings)
        // Act
        val added = myCustomSettingsMockList.add(Random.nextLong())
        // Assert
        verify(myCustomSettingsMockList).add(any())
        Assert.assertEquals(added, false)
    }

    /**
     * testing async methods
     * https://stackoverflow.com/questions/53824465/junit-testing-an-asynchronous-method-with-mockito
     */

    /**
     * testing methods with callbacks
     * https://www.baeldung.com/mockito-callbacks
     */

    /**
     * https://www.baeldung.com/mockito-verify
     */

}

class MyList : AbstractMutableList<Long?>() {
    override fun get(index: Int): Long? {
        return null
    }

    override fun add(index: Int, element: Long?) {
        TODO("Not yet implemented")
    }

    override fun removeAt(index: Int): Long? {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: Long?): Long? {
        TODO("Not yet implemented")
    }

    override val size: Int
        get() = 1
}

internal class CustomAnswer : Answer<Boolean> {
    @Throws(Throwable::class)
    override fun answer(invocation: InvocationOnMock): Boolean {
        return false
    }
}