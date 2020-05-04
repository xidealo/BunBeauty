package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.DeleteTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.UpdateTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag


interface ITagRepository {
    fun insert(tag: Tag, insertTagCallback: InsertTagCallback)
    fun delete(tag: Tag, deleteTagCallback: DeleteTagCallback)
    fun update(tag: Tag, updateTagCallback: UpdateTagCallback)
    fun get(): List<Tag>
}