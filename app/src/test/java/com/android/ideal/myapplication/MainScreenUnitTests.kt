package com.android.ideal.myapplication

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class MainScreenUnitTests {

    @Mock
    internal lateinit var serviceRepository: ServiceRepository
    @Mock
    internal lateinit var userRepository: UserRepository

    private val mainScreenInteractor: MainScreenInteractor
        get() = MainScreenInteractor(userRepository, serviceRepository)

    @Test
    fun convertCacheDataToMainScreenData() {
        val cacheData = ArrayList<ArrayList<Any>>()
        val service = Service()
        val user = User()
        cacheData.add(getDataList(1, user, service))
        //MS data which we are waiting
        val mainScreenData = ArrayList<ArrayList<Any>>()
        mainScreenData.add(arrayListOf(cacheData[0][1], cacheData[0][2]))

        assertEquals(mainScreenInteractor.convertCacheDataToMainScreenData(cacheData), mainScreenData)
    }

    @Test
    fun convertCacheDataToMainScreenDataWithCategory() {
        val cacheData = ArrayList<ArrayList<Any>>()
        val service = Service()
        val user = User()
        service.category = "маникюр"
        cacheData.add(getDataList(2, user, service))
        //MS data which we are waiting
        val mainScreenData = ArrayList<ArrayList<Any>>()
        mainScreenData.add(arrayListOf(cacheData[0][1], cacheData[0][2]))
        assertEquals(mainScreenInteractor.convertCacheDataToMainScreenData("маникюр", cacheData), mainScreenData)
    }

    @Test
    fun convertCacheDataToMainScreenDataWithWrongCategory() {
        val cacheData = ArrayList<ArrayList<Any>>()
        val service = Service()
        val user = User()
        service.category = "маникюр"
        cacheData.add(getDataList(1, user, service))
        //MS data which we are waiting
        val mainScreenData = ArrayList<ArrayList<Any>>()
        assertEquals(mainScreenInteractor.convertCacheDataToMainScreenData("педикюр", cacheData), mainScreenData)
    }

    @Test
    fun convertCacheDataToMainScreenDataWithWrongTags() {
        val cacheData = ArrayList<ArrayList<Any>>()
        val service = Service()
        val user = User()
        val tag = Tag()
        tag.tag = "Бровничи"
        val selectedTagsArray = arrayListOf("Бровнич")
        service.tags.add(tag)
        cacheData.add(getDataList(1, user, service))
        //MS data which we are waiting
        val mainScreenData = ArrayList<ArrayList<Any>>()
        mainScreenData.add(arrayListOf(cacheData[0][1], cacheData[0][2]))
        assertEquals(mainScreenInteractor.convertCacheDataToMainScreenData(selectedTagsArray, cacheData), mainScreenData)
    }

    @Test
    fun whenUserIdEqualsServiceOwnerId() {
        val cacheUsers = ArrayList<User>()
        val user1 = User()
        user1.id = "1"
        val user2 = User()
        user2.id = "2"
        val service = Service()
        service.userId = "1"
        cacheUsers.add(user1)
        cacheUsers.add(user2)

        assertEquals(mainScreenInteractor.getUserByService(cacheUsers, service), user1)
    }

    @Test
    fun getCategoriesFromMsData() {
        val category1 = "реснички"
        val category2 = "бровушки"

        val categories = mutableSetOf<String>()
        categories.add(category1)
        categories.add(category2)

        val mainScreenData = ArrayList<ArrayList<Any>>()
        val service = Service()
        val service2 = Service()
        val service3 = Service()
        val user = User()
        service.category = category1
        service2.category = category2
        service3.category = category2

        mainScreenData.add(arrayListOf(service, user))
        mainScreenData.add(arrayListOf(service2, user))
        mainScreenData.add(arrayListOf(service3, user))

        assertEquals(mainScreenInteractor.getCategories(mainScreenData), categories)
    }

    @Test
    fun isFirstEnter() {
        val cacheUsers = ArrayList<String>()
        val user = "1"
        val user2 = "2"
        cacheUsers.add(user)
        assertEquals(mainScreenInteractor.isFirstEnter(user2, cacheUsers), true)
    }

    @Test
    fun notIsFirstEnter() {
        val cacheUsers = ArrayList<String>()
        val user = "1"
        cacheUsers.add(user)
        assertEquals(mainScreenInteractor.isFirstEnter(user, cacheUsers), false)
    }

    //choosePremiumServices

    private fun getDataList(points: Int, user: User, service: Service): ArrayList<Any> {
        val data = ArrayList<Any>()
        data.add(points)
        data.add(service)
        data.add(user)
        return data
    }

}

