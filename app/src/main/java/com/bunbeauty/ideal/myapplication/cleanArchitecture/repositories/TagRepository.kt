package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.TagFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.ITagRepository
import kotlinx.coroutines.launch

class TagRepository(private val tagDao: TagDao,
                    private val tagFirebase: TagFirebase): ITagRepository, BaseRepository() {
    override fun insert(tag: Tag) {
        launch {
            tagDao.insert(tag)
        }
        tagFirebase.insert(tag)
    }

    override fun delete(tag: Tag) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(tag: Tag) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<Tag> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getIdForNew(userId: String, serviceId:String): String = tagFirebase.getIdForNew(userId,serviceId)


}