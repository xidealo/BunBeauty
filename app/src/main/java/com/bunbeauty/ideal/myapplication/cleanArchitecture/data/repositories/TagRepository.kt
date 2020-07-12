package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.DeleteTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.UpdateTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.TagFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ITagRepository
import kotlinx.coroutines.launch

class TagRepository(
    private val tagDao: TagDao,
    private val tagFirebase: TagFirebase
) : ITagRepository, BaseRepository() {

    override fun insert(tag: Tag, insertTagCallback: InsertTagCallback) {
        launch {
            tag.id = getIdForNew(tag.serviceId, tag.userId)
            tagFirebase.insert(tag)
        }
    }

    override fun delete(tag: Tag, deleteTagCallback: DeleteTagCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(tag: Tag, updateTagCallback: UpdateTagCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getIdForNew(userId: String, serviceId: String): String =
        tagFirebase.getIdForNew(userId, serviceId)

}